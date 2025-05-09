package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.entity.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
    int countAllByOrder_OrderStatusAndProduct_Brand_Id(EOrderStatus orderStatus, Long productBrandId);

    @Query("SELECT SUM(oi.quantity) FROM OrderDetailEntity oi " +
            "JOIN oi.order o " +
            "WHERE o.orderDate BETWEEN :startDate AND :endDate")
    Long sumQuantityByOrderDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT oi FROM OrderDetailEntity oi " +
            "JOIN oi.order o " +
            "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.product " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<OrderDetailEntity> findTopSellingProducts(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
