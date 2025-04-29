package hcmute.edu.vn.techstore.dto.request;


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
    private String discountCode;
    private String discountName;
    private String discountValue;
    private List<ProductCheckout> productCheckouts;
    private String totalPrice;

    @Setter
    @Getter
    public static class ProductCheckout {
        private String name;
        private int quantity;
        private String price;
        private String image;
        private String totalPrice;
    }
}
