package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.Enum.EDiscountType;
import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.convert.OrderConverter;
import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.dto.response.OrderCompleteRespone;
import hcmute.edu.vn.techstore.dto.response.OrderResponse;
import hcmute.edu.vn.techstore.dto.response.ReportResponse;
import hcmute.edu.vn.techstore.entity.*;
import hcmute.edu.vn.techstore.repository.*;
import hcmute.edu.vn.techstore.service.impl.strategy.ReportContext;
import hcmute.edu.vn.techstore.service.interfaces.*;
import hcmute.edu.vn.techstore.service.mail.EmailNotificationObserver;
import hcmute.edu.vn.techstore.service.mail.EmailSender;
import hcmute.edu.vn.techstore.service.mail.OrderConcreteSubject;
import hcmute.edu.vn.techstore.utils.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartRepository cartRepository;
    private final ICartDetailService cartDetailService;
    private final IDiscountService discountService;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final IProductService productService;
    private final PriceUtil priceUtil;
    private final OrderConverter orderConverter;
    private final ReportContext reportContext;
    private final EmailSender emailSender;



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
            if (discountService.checkDiscount(checkoutRequest.getDiscountCode())) {
                DiscountEntity discountEntity = discountService.findByCode(checkoutRequest.getDiscountCode());
                CheckoutRequest.DiscountCheckout discountCheckout = new CheckoutRequest.DiscountCheckout();
                discountCheckout.setDiscountCode(discountEntity.getCode());
                discountCheckout.setDiscountName(discountEntity.getName());
                // Calculate total price with discount
                if (discountEntity.getDiscountType().equals(EDiscountType.COUPON)) {
                    discountCheckout.setDiscountValue(discountEntity.getAmount().toString() + "%");
                } else {
                    discountCheckout.setDiscountValue(discountEntity.getAmount().toString() + " VND");
                }
                if (checkoutRequest.getDiscounts() == null) {
                    checkoutRequest.setDiscounts(new ArrayList<>());
                }
                // Add discount to checkout request
                checkoutRequest.getDiscounts().add(discountCheckout);
                // Calculate total price with discount
                checkoutRequest.setTotalPrice(priceUtil.formatPrice(getTotalPriceWithDiscount(checkoutRequest)));
            }
        }
        return checkoutRequest;
    }

    public BigDecimal getTotalPriceWithDiscount(CheckoutRequest request) {
        OrderPriceCalculator calculator = new BaseOrderPriceCalculator();

        if (discountService.findByCode(request.getDiscounts().getLast().getDiscountCode()).getDiscountType().equals(EDiscountType.COUPON)) {
            calculator = new CouponDecorator(calculator);
        } else if (discountService.findByCode(request.getDiscounts().getLast().getDiscountCode()).getDiscountType().equals(EDiscountType.VOUCHER)) {
            calculator = new VoucherDecorator(calculator);
        }

        return calculator.calculateTotal(priceUtil.parsePrice(request.getTotalPrice()), request.getDiscounts().getLast());
    }

    @Override
    public Long createOrder(CheckoutRequest checkoutRequest) {
        // Decrease discount quantity
        if (checkoutRequest.getDiscounts() != null && !checkoutRequest.getDiscounts().isEmpty()) {
            for (CheckoutRequest.DiscountCheckout discountCheckout : checkoutRequest.getDiscounts()) {
                discountService.decreaseQuantity(discountCheckout.getDiscountCode(), 1);
            }
        }
        // Decrease product quantity
        for (CheckoutRequest.ProductCheckout productCheckout : checkoutRequest.getProductCheckouts()) {
            productService.decreaseQuantity(productCheckout.getId(), productCheckout.getQuantity());
        }

        // Create order
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderDate(java.time.LocalDateTime.now());
        orderEntity.setAddress(checkoutRequest.getAddress());
        orderEntity.setOrderStatus(EOrderStatus.PENDING_CONFIRMATION);
        orderEntity.setTotalPrice(priceUtil.parsePrice(checkoutRequest.getTotalPrice()));
        orderEntity.setUser(userRepository.findByAccount_Email(checkoutRequest.getEmail()).orElse(null));
        if (checkoutRequest.getDiscounts() != null && !checkoutRequest.getDiscounts().isEmpty()) {
            orderEntity.setDiscounts(checkoutRequest.getDiscounts().stream()
                    .map(discountCheckout -> {
                        DiscountEntity discountEntity = discountService.findByCode(discountCheckout.getDiscountCode());
                        if (discountEntity != null) {
                            return discountEntity;
                        }
                        return null;
                    }).collect(Collectors.toSet()));
        }
        orderEntity.setPayment(paymentRepository.findByName(checkoutRequest.getPaymentMethod().name()).orElse(null));
        // Save order first to get generated ID
        orderEntity = orderRepository.save(orderEntity);

        // Create and save order details
        List<OrderDetailEntity> orderDetails = new ArrayList<>();
        for (CheckoutRequest.ProductCheckout productCheckout : checkoutRequest.getProductCheckouts()) {
            OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
            orderDetailEntity.setProduct(productService.findById(productCheckout.getId()).orElse(null));
            orderDetailEntity.setQuantity(productCheckout.getQuantity());
            orderDetailEntity.setOrder(orderEntity);
            orderDetails.add(orderDetailEntity);
        }
        // Explicitly save all order details
        orderDetailRepository.saveAll(orderDetails);

        // Clear cart details after order is created
        CartEntity cartEntity = cartRepository.findByCart_User_Account_Email(checkoutRequest.getEmail()).orElse(null);
        for (CheckoutRequest.ProductCheckout productCheckout : checkoutRequest.getProductCheckouts()) {
            cartDetailService.deleteCartDetail(productCheckout.getId(),cartEntity);
        }

        return orderEntity.getId();
    }

    @Override
    public boolean changeStatusOrder(Long orderId, EOrderStatus status) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        if (orderEntity != null) {
            orderEntity.setOrderStatus(status);
            orderRepository.save(orderEntity);

            OrderConcreteSubject orderSubject = new OrderConcreteSubject(orderEntity);
            orderSubject.attach(new EmailNotificationObserver(emailSender));
            orderSubject.setOrderStatus(status);

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
        if (orderEntity != null) {
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

    @Override
    public String getTotalPurchaseDue() {
        List<OrderEntity> orderEntities = orderRepository.findAllOrderByOrderDateDesc();
        BigDecimal totalPurchaseDue = BigDecimal.ZERO;
        for (OrderEntity orderEntity : orderEntities) {
            if (orderEntity.getOrderStatus().equals(EOrderStatus.DELIVERED_SUCCESSFULLY)) {
                totalPurchaseDue = totalPurchaseDue.add(orderEntity.getTotalPrice());
            }
        }
        return priceUtil.formatPrice(totalPurchaseDue);
    }

    @Override
    public String getTotalProductsSold() {
        List<OrderEntity> orderEntities = orderRepository.findAllOrderByOrderDateDesc();
        int totalProductsSold = 0;
        for (OrderEntity orderEntity : orderEntities) {
            if (orderEntity.getOrderStatus().equals(EOrderStatus.DELIVERED_SUCCESSFULLY)) {
                for (OrderDetailEntity orderDetail : orderEntity.getOrderDetails()) {
                    totalProductsSold += orderDetail.getQuantity();
                }
            }
        }
        return String.valueOf(totalProductsSold);
    }

    @Override
    public ReportResponse getReport(LocalDate startDate, LocalDate endDate, String reportType) {
        return reportContext.generateReport(reportType, startDate, endDate);
    }
}
