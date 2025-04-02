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
@Table(name = "cartdetails")
public class CartDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartDetailId;

    @ManyToOne
    @JoinColumn(name = "cartId", referencedColumnName = "cartId")
    private CartEntity cart;

    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private ProductEntity product;

    @Column(name = "quantity", nullable = false)
    private int quantity;
}