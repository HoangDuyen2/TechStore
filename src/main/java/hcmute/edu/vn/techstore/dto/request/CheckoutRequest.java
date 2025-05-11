package hcmute.edu.vn.techstore.dto.request;


import hcmute.edu.vn.techstore.Enum.EDiscountType;
import hcmute.edu.vn.techstore.Enum.EPayment;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CheckoutRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private List<DiscountCheckout> discounts;
    private List<ProductCheckout> productCheckouts;
    private String totalPrice;
    private EPayment paymentMethod;

    @Setter
    @Getter
    public static class ProductCheckout {
        private Long id;
        private String name;
        private int quantity;
        private String price;
        private String image;
        private String totalPrice;
    }

    @Setter
    @Getter
    public static class DiscountCheckout {
        private String discountCode;
        private String discountName;
        private String discountValue;
    }
}
