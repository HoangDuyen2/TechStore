package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.response.ReportResponse;
import hcmute.edu.vn.techstore.entity.OrderEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.repository.OrderRepository;
import hcmute.edu.vn.techstore.service.interfaces.IAccountService;
import hcmute.edu.vn.techstore.service.interfaces.ReportStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements IAccountService {
    @Component
    @RequiredArgsConstructor
    public static class DefaultReportStrategy implements ReportStrategy {
        private final OrderRepository orderRepository;

        @Override
        public ReportResponse generateReport(LocalDate startDate, LocalDate endDate) {
            // Convert dates to LocalDateTime for query
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

            // Get all orders in the date range
            List<OrderEntity> orders = orderRepository.findAllByOrderDateBetween(startDateTime, endDateTime);

            // Calculate revenue and total orders
            BigDecimal totalRevenue = BigDecimal.ZERO;
            int totalProductsSold = 0;

            // Maps to track product sales
            Map<ProductEntity, Integer> productQuantityMap = new HashMap<>();
            Map<ProductEntity, BigDecimal> productRevenueMap = new HashMap<>();

            // Process each order
            for (OrderEntity order : orders) {
                totalRevenue = totalRevenue.add(order.getTotalPrice());

                for (var detail : order.getOrderDetails()) {
                    ProductEntity product = detail.getProduct();
                    int quantity = detail.getQuantity();
                    BigDecimal itemRevenue = detail.getProduct().getPrice().multiply(BigDecimal.valueOf(quantity));

                    totalProductsSold += quantity;

                    // Update product maps
                    productQuantityMap.put(product, productQuantityMap.getOrDefault(product, 0) + quantity);
                    productRevenueMap.put(product, productRevenueMap.getOrDefault(product, BigDecimal.ZERO).add(itemRevenue));
                }
            }

            // Get top selling products
            List<ReportResponse.TopSellingProduct> topProducts = productQuantityMap.entrySet().stream()
                    .sorted(Map.Entry.<ProductEntity, Integer>comparingByValue().reversed())
                    .limit(20) // Top 20 products
                    .map(entry -> {
                        ProductEntity product = entry.getKey();
                        int quantity = entry.getValue();
                        BigDecimal revenue = productRevenueMap.get(product);

                        return ReportResponse.TopSellingProduct.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .image(product.getThumbnail())
                                .quantitySold(quantity)
                                .revenue(revenue)
                                .build();
                    })
                    .collect(Collectors.toList());

            // Get recent orders
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            List<ReportResponse.RecentOrder> recentOrders = orders.stream()
                    .sorted(Comparator.comparing(OrderEntity::getOrderDate).reversed())
                    .limit(8) // Show 8 recent orders
                    .map(order -> ReportResponse.RecentOrder.builder()
                            .orderId(order.getId().toString())
                            .customerName(order.getUser().getFirstName())
                            .orderDate(order.getOrderDate().format(formatter))
                            .amount(order.getTotalPrice())
                            .status(order.getOrderStatus().name())
                            .build())
                    .collect(Collectors.toList());

            // Build and return the report
            return ReportResponse.builder()
                    .totalRevenue(totalRevenue)
                    .totalOrders(orders.size())
                    .totalProductsSold(totalProductsSold)
                    .topSellingProducts(topProducts)
                    .recentOrders(recentOrders)
                    .build();
        }
    }
}
