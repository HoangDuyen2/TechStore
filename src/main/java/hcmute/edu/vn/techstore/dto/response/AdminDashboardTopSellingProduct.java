package hcmute.edu.vn.techstore.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDashboardTopSellingProduct {
    private Long productId;
    private String productName;
    private String productImage;
    private String totalSold;
    private String stock;
    private String brand;
}
