package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.Enum.EDiscountType;
import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.entity.CartEntity;
import hcmute.edu.vn.techstore.entity.DiscountEntity;
import hcmute.edu.vn.techstore.repository.CartRepository;
import hcmute.edu.vn.techstore.repository.DiscountRepository;
import hcmute.edu.vn.techstore.repository.OrderRepository;
import hcmute.edu.vn.techstore.repository.UserRepository;
import hcmute.edu.vn.techstore.service.interfaces.IDiscountService;
import hcmute.edu.vn.techstore.service.interfaces.IOrderService;
import hcmute.edu.vn.techstore.service.interfaces.OrderPriceCalculator;
import hcmute.edu.vn.techstore.utils.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final IDiscountService discountService;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final DiscountRepository discountRepository;
    private final PriceUtil priceUtil;

    @Override
    public CheckoutRequest getCheckoutRequest(String email) {
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setFirstName(Objects.requireNonNull(userRepository.findByAccount_Email(email).orElse(null)).getFirstName());
        checkoutRequest.setLastName(Objects.requireNonNull(userRepository.findByAccount_Email(email).orElse(null)).getLastName());
        checkoutRequest.setEmail(Objects.requireNonNull(userRepository.findByAccount_Email(email).orElse(null)).getAccount().getEmail());
        checkoutRequest.setPhone(Objects.requireNonNull(userRepository.findByAccount_Email(email).orElse(null)).getPhoneNumber());
        checkoutRequest.setAddress(Objects.requireNonNull(userRepository.findByAccount_Email(email).orElse(null)).getAddress());
        CartEntity cartEntity = cartRepository.findByCart_User_Account_Email(email).orElse(null);
        if (cartEntity != null) {
            checkoutRequest.setProductCheckouts(cartEntity.getCartDetails().stream()
                    .map(cartDetail -> {
                        CheckoutRequest.ProductCheckout productCheckout = new CheckoutRequest.ProductCheckout();
                        productCheckout.setName(cartDetail.getProduct().getName());
                        productCheckout.setQuantity(cartDetail.getQuantity());
                        productCheckout.setPrice(priceUtil.formatPrice(cartDetail.getProduct().getPrice()));
                        productCheckout.setImage(cartDetail.getProduct().getThumbnail());
                        productCheckout.setTotalPrice(priceUtil.formatPrice(cartDetail.getProduct().getPrice().multiply(BigDecimal.valueOf(cartDetail.getQuantity()))));
                        return productCheckout;
                    }).toList());
        }
        checkoutRequest.setTotalPrice(priceUtil.formatPrice(cartEntity.getTotalPrice()));
        return checkoutRequest;
    }

    @Override
    public CheckoutRequest applyDiscount(CheckoutRequest checkoutRequest) {
        if (checkoutRequest.getDiscountCode() != null && !checkoutRequest.getDiscountCode().isEmpty()) {
            DiscountEntity discountEntity = discountRepository.findByCode(checkoutRequest.getDiscountCode());
            if (discountEntity != null) {
                checkoutRequest.setDiscountName(discountEntity.getName());
//                if (discountEntity.getDiscountType().equals(EDiscountType.COUPON)) {
//                    checkoutRequest.setDiscountValue(discountEntity.getAmount().toString() + "%");
//                } else {
//                    checkoutRequest.setDiscountValue(discountEntity.getAmount().toString() + " VND" );
//                }
                checkoutRequest.setDiscountValue(discountEntity.getAmount().toString());
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
}
