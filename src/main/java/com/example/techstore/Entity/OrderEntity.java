package com.example.techstore.Entity;

import com.example.techstore.Enum.EOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "orders")
public class OrderEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderId;

    @Column(name = "orderDate", nullable = false)
    private LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "addressId", referencedColumnName = "addressId")
    private AddressEntity address;


    @Enumerated(EnumType.STRING)
    @Column(name = "orderStatus", nullable = false)
    private EOrderStatus orderStatus;

    @Column(name = "totalPrice", nullable = false, columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "discountId", referencedColumnName = "discountId")
    private DiscountEntity discount;

    @ManyToOne
    @JoinColumn(name = "paymentId", referencedColumnName = "paymentId")
    private PaymentEntity payment;

    @OneToMany(mappedBy = "order")
    private List<OrderDetailEntity> orderDetails;

}
