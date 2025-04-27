package hcmute.edu.vn.techstore.dto.response;

import java.util.List;

public class CartDetailWrapper {
    private List<CartDetailResponse> activeCartDetails;
    private List<CartDetailResponse> inactiveCartDetails;

    public CartDetailWrapper(List<CartDetailResponse> activeCartDetails, List<CartDetailResponse> inactiveCartDetails) {
        this.activeCartDetails = activeCartDetails;
        this.inactiveCartDetails = inactiveCartDetails;
    }

    // Getter and Setter
    public List<CartDetailResponse> getActiveCartDetails() {
        return activeCartDetails;
    }

    public void setActiveCartDetails(List<CartDetailResponse> activeCartDetails) {
        this.activeCartDetails = activeCartDetails;
    }

    public List<CartDetailResponse> getInactiveCartDetails() {
        return inactiveCartDetails;
    }

    public void setInactiveCartDetails(List<CartDetailResponse> inactiveCartDetails) {
        this.inactiveCartDetails = inactiveCartDetails;
    }
}

