package com.example.techstore.Entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "orderdetails")
public class OrderDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderDetailId;

    @ManyToOne
    @JoinColumn(name = "orderId", referencedColumnName = "orderId")
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private ProductEntity product;

    @Column(name = "quantity", nullable = false)
    private int quantity;
}
