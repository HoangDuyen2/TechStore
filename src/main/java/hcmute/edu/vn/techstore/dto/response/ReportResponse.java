package hcmute.edu.vn.techstore.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private BigDecimal totalRevenue;
    private int totalOrders;
    private int totalProductsSold;
    private List<TopSellingProduct> topSellingProducts;
    private List<RecentOrder> recentOrders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopSellingProduct {
        private Long id;
        private String name;
        private String image;
        private int quantitySold;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentOrder {
        private String orderId;
        private String customerName;
        private String orderDate;
        private BigDecimal amount;
        private String status;
    }
}