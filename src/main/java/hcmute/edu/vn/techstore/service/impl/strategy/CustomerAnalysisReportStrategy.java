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

@Component("customerAnalysisReport")
@RequiredArgsConstructor
public class CustomerAnalysisReportStrategy implements ReportStrategy {
    private final OrderRepository orderRepository;

    @Override
    public ReportResponse generateReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<OrderEntity> orders = orderRepository.findAllByOrderDateBetween(startDateTime, endDateTime);

        // Maps to track customer purchases
        Map<String, Integer> customerOrderCount = new HashMap<>();
        Map<String, BigDecimal> customerSpending = new HashMap<>();
        Map<String, Integer> customerProductsBought = new HashMap<>();
        Map<String, String> customerImageMap = new HashMap<>();
        int totalProductsSold = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        // Process each order
        for (OrderEntity order : orders) {
            String customerName = order.getUser().getFirstName() + " " + order.getUser().getLastName();
            totalRevenue = totalRevenue.add(order.getTotalPrice());

            // Update customer statistics
            customerOrderCount.put(customerName, customerOrderCount.getOrDefault(customerName, 0) + 1);
            customerSpending.put(customerName, customerSpending.getOrDefault(customerName, BigDecimal.ZERO).add(order.getTotalPrice()));

            // Store customer avatar if available
            if (!customerImageMap.containsKey(customerName)) {
                String avatar = order.getUser().getImage();
                customerImageMap.put(customerName, avatar != null ? avatar : "/admin/asset/images/avatar-default.png");
            }

            int orderProductsBought = 0;
            for (var detail : order.getOrderDetails()) {
                orderProductsBought += detail.getQuantity();
            }
            totalProductsSold += orderProductsBought;
            customerProductsBought.put(customerName, customerProductsBought.getOrDefault(customerName, 0) + orderProductsBought);
        }

        // Get top customers
        List<ReportResponse.TopSellingProduct> topCustomers = customerSpending.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(10) // Top 10 customers
                .map(entry -> {
                    String customerName = entry.getKey();
                    BigDecimal spending = entry.getValue();
                    int productsBought = customerProductsBought.get(customerName);
                    int orderCount = customerOrderCount.get(customerName);
                    String image = customerImageMap.getOrDefault(customerName, "/admin/asset/images/avatar-default.png");

                    return ReportResponse.TopSellingProduct.builder()
                            .id(0L)
                            .name(customerName + " (" + orderCount + " orders)")
                            .image(image)
                            .quantitySold(productsBought)
                            .revenue(spending)
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
                .topSellingProducts(topCustomers)
                .recentOrders(recentOrders)
                .build();
    }
} 