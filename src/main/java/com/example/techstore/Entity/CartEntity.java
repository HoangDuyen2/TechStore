package com.example.techstore.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "carts")
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private UserEntity user;

    @OneToMany(mappedBy = "cart")
    private List<CartDetailEntity> cartDetailEntityList;

    @Column(name = "total_price",nullable = false, columnDefinition = "DECIMAL(10, 2) DEFAULT 0")
    private Long totalPrice;
}
