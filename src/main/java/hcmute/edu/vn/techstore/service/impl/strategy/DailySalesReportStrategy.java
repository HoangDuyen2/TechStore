package hcmute.edu.vn.techstore.service.impl.strategy;

import hcmute.edu.vn.techstore.dto.response.ReportResponse;
import hcmute.edu.vn.techstore.entity.OrderEntity;
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

@Component("dailySalesReport")
@RequiredArgsConstructor
public class DailySalesReportStrategy implements ReportStrategy {
    private final OrderRepository orderRepository;

    @Override
    public ReportResponse generateReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<OrderEntity> orders = orderRepository.findAllByOrderDateBetween(startDateTime, endDateTime);

        // Maps to track daily sales
        Map<LocalDate, Integer> dailyOrderCount = new HashMap<>();
        Map<LocalDate, BigDecimal> dailyRevenue = new HashMap<>();
        Map<LocalDate, Integer> dailyProductsSold = new HashMap<>();
        Map<LocalDate, String> dailyImageMap = new HashMap<>();
        int totalProductsSold = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        // Process each order
        for (OrderEntity order : orders) {
            LocalDate orderDate = order.getOrderDate().toLocalDate();
            totalRevenue = totalRevenue.add(order.getTotalPrice());

            // Update daily order count
            dailyOrderCount.put(orderDate, dailyOrderCount.getOrDefault(orderDate, 0) + 1);
            dailyRevenue.put(orderDate, dailyRevenue.getOrDefault(orderDate, BigDecimal.ZERO).add(order.getTotalPrice()));

            int orderProductsSold = 0;
            for (var detail : order.getOrderDetails()) {
                orderProductsSold += detail.getQuantity();
                // Store the first product's image for each day
                if (!dailyImageMap.containsKey(orderDate)) {
                    dailyImageMap.put(orderDate, detail.getProduct().getThumbnail());
                }
            }
            totalProductsSold += orderProductsSold;
            dailyProductsSold.put(orderDate, dailyProductsSold.getOrDefault(orderDate, 0) + orderProductsSold);
        }

        // Get top selling days
        List<ReportResponse.TopSellingProduct> topDays = dailyOrderCount.entrySet().stream()
                .sorted(Map.Entry.<LocalDate, Integer>comparingByValue().reversed())
                .limit(10) // Top 10 days
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    int order = entry.getValue();
                    BigDecimal revenue = dailyRevenue.get(date);
                    int productsSold = dailyProductsSold.get(date);
                    String image = dailyImageMap.getOrDefault(date, "");

                    return ReportResponse.TopSellingProduct.builder()
                            .id(0L)
                            .name(date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                            .image(image)
                            .quantitySold(productsSold)
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
                .topSellingProducts(topDays)
                .recentOrders(recentOrders)
                .build();
    }
} 