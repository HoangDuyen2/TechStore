package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    int countAllByDiscount_Code(String discountCode);
    @Query("select o from OrderEntity o where o.user.account.email = ?1 order by o.orderDate desc ")
    List<OrderEntity> findAllByUserAccount_Email(String email);
    @Query("select o from OrderEntity o order by o.orderDate desc ")
    List<OrderEntity> findAllOrderByOrderDateDesc();
    @Query("select o from OrderEntity o where o.orderStatus = ?1 order by o.orderDate desc ")
    List<OrderEntity> findAllOrderByOrderStatusAndDateDesc(EOrderStatus status);

    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    Long countByOrderDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(o.totalPrice) FROM OrderEntity o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT new map(o.orderDate as date, SUM(o.totalPrice) as amount) " +
            "FROM OrderEntity o " +
            "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY o.orderDate " +
            "ORDER BY o.orderDate")
    List<Object[]> findDailySales(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<OrderEntity> findAllByOrderDateBetween(LocalDateTime orderDateAfter, LocalDateTime orderDateBefore);
}
