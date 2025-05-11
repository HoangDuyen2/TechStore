package hcmute.edu.vn.techstore.entity;

import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class OrderEntity{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orderDate", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "address", nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "orderStatus", nullable = false)
    private EOrderStatus orderStatus;

    @Column(name = "totalPrice", nullable = false, columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private UserEntity user;

    @ManyToMany
    @JoinTable(
            name = "order_discount",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "discount_id")
    )
    private Set<DiscountEntity> discounts;

    @ManyToOne
    @JoinColumn(name = "paymentId", referencedColumnName = "id")
    private PaymentEntity payment;

    @OneToMany(mappedBy = "order")
    private List<OrderDetailEntity> orderDetails;

}
