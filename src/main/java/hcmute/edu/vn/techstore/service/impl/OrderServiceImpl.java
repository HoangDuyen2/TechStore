package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.Enum.EDiscountType;
import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.convert.OrderConverter;
import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.dto.response.OrderCompleteRespone;
import hcmute.edu.vn.techstore.dto.response.OrderResponse;
import hcmute.edu.vn.techstore.entity.*;
import hcmute.edu.vn.techstore.repository.*;
import hcmute.edu.vn.techstore.service.interfaces.ICartService;
import hcmute.edu.vn.techstore.service.interfaces.IDiscountService;
import hcmute.edu.vn.techstore.service.interfaces.IOrderService;
import hcmute.edu.vn.techstore.service.interfaces.OrderPriceCalculator;
import hcmute.edu.vn.techstore.utils.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final IDiscountService discountService;
    private final CartRepository cartRepository;
    private final ICartService cartService;
    private final UserRepository userRepository;
    private final DiscountRepository discountRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PriceUtil priceUtil;
    private final OrderConverter orderConverter;

    @Override
    public CheckoutRequest getCheckoutRequest(String email, List<Long> selectedProductIds) {
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setFirstName(Objects.requireNonNull(userRepository.findByAccount_Email(email).orElse(null)).getFirstName());
        checkoutRequest.setLastName(Objects.requireNonNull(userRepository.findByAccount_Email(email).orElse(null)).getLastName());
        checkoutRequest.setEmail(Objects.requireNonNull(userRepository.findByAccount_Email(email).orElse(null)).getAccount().getEmail());
        checkoutRequest.setPhone(Objects.requireNonNull(userRepository.findByAccount_Email(email).orElse(null)).getPhoneNumber());
        checkoutRequest.setAddress(Objects.requireNonNull(userRepository.findByAccount_Email(email).orElse(null)).getAddress());
        CartEntity cartEntity = cartRepository.findByCart_User_Account_Email(email).orElse(null);
        if (cartEntity != null) {
            checkoutRequest.setProductCheckouts(cartEntity.getCartDetails().stream()
                    .filter(cartDetail -> selectedProductIds == null || selectedProductIds.isEmpty() || selectedProductIds.contains(cartDetail.getProduct().getId()))
                    .map(cartDetail -> {
                        CheckoutRequest.ProductCheckout productCheckout = new CheckoutRequest.ProductCheckout();
                        productCheckout.setId(cartDetail.getProduct().getId());
                        productCheckout.setName(cartDetail.getProduct().getName());
                        productCheckout.setQuantity(cartDetail.getQuantity());
                        productCheckout.setPrice(priceUtil.formatPrice(cartDetail.getProduct().getPrice()));
                        productCheckout.setImage(cartDetail.getProduct().getThumbnail());
                        productCheckout.setTotalPrice(priceUtil.formatPrice(cartDetail.getProduct().getPrice().multiply(BigDecimal.valueOf(cartDetail.getQuantity()))));
                        return productCheckout;
                    }).toList());
        }
        // Calculate total price
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CheckoutRequest.ProductCheckout productCheckout : checkoutRequest.getProductCheckouts()) {
            totalPrice = totalPrice.add(priceUtil.parsePrice(productCheckout.getTotalPrice()));
        }
        checkoutRequest.setTotalPrice(priceUtil.formatPrice(totalPrice));
        return checkoutRequest;
    }

    @Override
    public CheckoutRequest applyDiscount(CheckoutRequest checkoutRequest) {
        if (checkoutRequest.getDiscountCode() != null && !checkoutRequest.getDiscountCode().isEmpty()) {
            DiscountEntity discountEntity = discountRepository.findByCode(checkoutRequest.getDiscountCode());
            if (checkDiscount(discountEntity)) {
                checkoutRequest.setDiscountName(discountEntity.getName());
                if (discountEntity.getDiscountType().equals(EDiscountType.COUPON)) {
                    checkoutRequest.setDiscountValue(discountEntity.getAmount().toString() + "%");
                } else {
                    checkoutRequest.setDiscountValue(discountEntity.getAmount().toString() + " VND");
                }
                checkoutRequest.setTotalPrice(priceUtil.formatPrice(getTotalPriceWithDiscount(checkoutRequest)));
            }
        }
        return checkoutRequest;
    }

    public BigDecimal getTotalPriceWithDiscount(CheckoutRequest request) {
        OrderPriceCalculator calculator = new BaseOrderPriceCalculator();

        if (request.getDiscountCode() != null) {
            if (discountRepository.findByCode(request.getDiscountCode()).getDiscountType().equals(EDiscountType.COUPON)) {
                calculator = new CouponDecorator(calculator);
            } else if (discountRepository.findByCode(request.getDiscountCode()).getDiscountType().equals(EDiscountType.VOUCHER)) {
                calculator = new VoucherDecorator(calculator);
            }
        }

        return calculator.calculateTotal(request);
    }

    @Override
    public Long createOrder(CheckoutRequest checkoutRequest) {
        // Check discount
        if (checkoutRequest.getDiscountCode() != null && !checkoutRequest.getDiscountCode().isEmpty()) {
            DiscountEntity discountEntity = discountRepository.findByCode(checkoutRequest.getDiscountCode());
            if (checkDiscount(discountEntity)) {
                discountService.decreaseQuantity(discountEntity.getId(), 1);
            } else {
                throw new IllegalArgumentException("Discount code is invalid or expired");
            }
        }
        // Check payment method
        if (checkoutRequest.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Payment method is required");
        }
        if (paymentRepository.findByName(checkoutRequest.getPaymentMethod().name()).isEmpty()) {
            throw new IllegalArgumentException("Payment method is invalid");
        }
        // Check product
        for (CheckoutRequest.ProductCheckout productCheckout : checkoutRequest.getProductCheckouts()) {
            ProductEntity productEntity = productRepository.findById(productCheckout.getId()).orElse(null);
            if (productEntity == null) {
                throw new IllegalArgumentException("Product is invalid");
            }
            if (productEntity.getStockQuantity() < productCheckout.getQuantity()) {
                throw new IllegalArgumentException("Product quantity is not enough");
            }
            if (!productEntity.isActived()) {
                throw new IllegalArgumentException("Product is not available");
            }
            if (!productEntity.getBrand().getIsActived()) {
                throw new IllegalArgumentException("Product brand is not available");
            }
            // Decrease product quantity
            productEntity.setStockQuantity(productEntity.getStockQuantity() - productCheckout.getQuantity());
            productRepository.save(productEntity);
        }

        // Create order
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderDate(java.time.LocalDateTime.now());
        orderEntity.setAddress(checkoutRequest.getAddress());
        orderEntity.setOrderStatus(EOrderStatus.PENDING_CONFIRMATION);
        orderEntity.setTotalPrice(priceUtil.parsePrice(checkoutRequest.getTotalPrice()));
        orderEntity.setUser(userRepository.findByAccount_Email(checkoutRequest.getEmail()).orElse(null));
        if (checkoutRequest.getDiscountCode() != null && !checkoutRequest.getDiscountCode().isEmpty()) {
            DiscountEntity discountEntity = discountRepository.findByCode(checkoutRequest.getDiscountCode());
            orderEntity.setDiscount(discountEntity);
        }
        orderEntity.setPayment(paymentRepository.findByName(checkoutRequest.getPaymentMethod().name()).orElse(null));

        // Save order first to get generated ID
        orderEntity = orderRepository.save(orderEntity);

        // Create and save order details
        List<OrderDetailEntity> orderDetails = new ArrayList<>();
        for (CheckoutRequest.ProductCheckout productCheckout : checkoutRequest.getProductCheckouts()) {
            OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
            orderDetailEntity.setProduct(productRepository.findById(productCheckout.getId()).orElse(null));
            orderDetailEntity.setQuantity(productCheckout.getQuantity());
            orderDetailEntity.setOrder(orderEntity);
            orderDetails.add(orderDetailEntity);
        }

        // Explicitly save all order details
        orderDetailRepository.saveAll(orderDetails);
//        cartService.deleteAllCartDetails(checkoutRequest.getEmail());
        return orderEntity.getId();
    }

    @Override
    public boolean changeStatusOrder(Long orderId, EOrderStatus status){
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        if (orderEntity != null) {
            orderEntity.setOrderStatus(status);
            orderRepository.save(orderEntity);
            return true;
        }
        return false;
    }

    @Override
    public List<OrderResponse> getAllOrdersByUserEmail(String email) {
        List<OrderEntity> orderEntities = orderRepository.findAllByUserAccount_Email(email);
        return orderEntities.stream().map(orderEntity -> orderConverter.toResponse(orderEntity)).toList();
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<OrderEntity> orderEntities = orderRepository.findAllOrderByOrderDateDesc();
        return orderEntities.stream().map(orderEntity -> orderConverter.toResponse(orderEntity)).toList();
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        if (orderEntity != null) {
            return orderConverter.toResponse(orderEntity);
        }
        return null;
    }


    private boolean checkDiscount(DiscountEntity discountEntity) {
        if (discountEntity != null && discountEntity.getQuantity() > 0 && discountEntity.getExpiredDate().isAfter(java.time.LocalDate.now())) {
            return true;
        }
        return false;
    }

    @Override
    public OrderCompleteRespone getOrderCompleteResponse(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        if (orderEntity != null) {
            OrderCompleteRespone orderCompleteRespone = new OrderCompleteRespone();
            orderCompleteRespone.setOrderId(orderEntity.getId());
            orderCompleteRespone.setOrderDate(orderEntity.getOrderDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            orderCompleteRespone.setTotalPrice(priceUtil.formatPrice(orderEntity.getTotalPrice()));
            orderCompleteRespone.setPaymentMethod(orderEntity.getPayment().getName());
            orderCompleteRespone.setAddress(orderEntity.getAddress());
            orderCompleteRespone.setPhoneNumber(orderEntity.getUser().getPhoneNumber());
            return orderCompleteRespone;
        }
        return null;
    }

    @Override
    public boolean updateOrderAddress(Long orderId, String address) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        if (orderEntity != null){
            orderEntity.setAddress(address);
            orderRepository.save(orderEntity);
            return true;
        }
        return false;
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(EOrderStatus status) {
        List<OrderEntity> orderResponseList = orderRepository.findAllOrderByOrderStatusAndDateDesc(EOrderStatus.valueOf(status.name()));
        return orderResponseList.stream().map(orderEntity -> orderConverter.toResponse(orderEntity)).toList();
    }
}
