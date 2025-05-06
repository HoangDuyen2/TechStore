package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
