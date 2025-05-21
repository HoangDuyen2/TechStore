package hcmute.edu.vn.techstore.service.impl.strategy;

import hcmute.edu.vn.techstore.dto.response.ReportResponse;
import hcmute.edu.vn.techstore.entity.OrderEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.repository.OrderRepository;
import hcmute.edu.vn.techstore.service.interfaces.ReportStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component("categoryReport")
@RequiredArgsConstructor
public class CategoryReportStrategy implements ReportStrategy {
    private final OrderRepository orderRepository;

    @Override
    public ReportResponse generateReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<OrderEntity> orders = orderRepository.findAllByOrderDateBetween(startDateTime, endDateTime);

        // Maps to track category sales
        Map<String, Integer> categoryQuantityMap = new HashMap<>();
        Map<String, BigDecimal> categoryRevenueMap = new HashMap<>();
        Map<String, String> categoryImageMap = new HashMap<>();
        int totalProductsSold = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        // Process each order
        for (OrderEntity order : orders) {
            totalRevenue = totalRevenue.add(order.getTotalPrice());

            for (var detail : order.getOrderDetails()) {
                ProductEntity product = detail.getProduct();
                String category = product.getBrand().getName(); // Using brand as category
                int quantity = detail.getQuantity();
                BigDecimal itemRevenue = product.getPrice().multiply(BigDecimal.valueOf(quantity));

                totalProductsSold += quantity;

                // Update category maps
                categoryQuantityMap.put(category, categoryQuantityMap.getOrDefault(category, 0) + quantity);
                categoryRevenueMap.put(category, categoryRevenueMap.getOrDefault(category, BigDecimal.ZERO).add(itemRevenue));
                // Store the first product's brand image for each category
                if (!categoryImageMap.containsKey(category)) {
                    categoryImageMap.put(category, product.getBrand().getImage());
                }
            }
        }

        // Get top selling categories
        List<ReportResponse.TopSellingProduct> topCategories = categoryQuantityMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10) // Top 10 categories
                .map(entry -> {
                    String category = entry.getKey();
                    int quantity = entry.getValue();
                    BigDecimal revenue = categoryRevenueMap.get(category);
                    String image = categoryImageMap.getOrDefault(category, "");

                    return ReportResponse.TopSellingProduct.builder()
                            .id(0L) // No ID for categories
                            .name(category)
                            .image(image)
                            .quantitySold(quantity)
                            .revenue(revenue)
                            .build();
                })
                .collect(Collectors.toList());

        // Get recent orders
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        List<ReportResponse.RecentOrder> recentOrders = orders.stream()
                .sorted(Comparator.comparing(OrderEntity::getOrderDate).reversed())
                .limit(8)
                .map(order -> ReportResponse.RecentOrder.builder()
                        .orderId(order.getId().toString())
                        .customerName(order.getUser().getFirstName() + " " + order.getUser().getLastName())
                        .orderDate(order.getOrderDate().format(formatter))
                        .amount(order.getTotalPrice())
                        .status(order.getOrderStatus().name())
                        .build())
                .collect(Collectors.toList());

        return ReportResponse.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(orders.size())
                .totalProductsSold(totalProductsSold)
                .topSellingProducts(topCategories)
                .recentOrders(recentOrders)
                .build();
    }
} 