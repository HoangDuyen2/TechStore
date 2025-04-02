package com.example.techstore.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "discounts")
public class DiscountEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "discountName", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "discountPercent")
    private float discountPercent;

    @Column(name = "expiriedDate")
    private LocalDate expiredDate;

    @OneToMany(mappedBy = "discount")
    private List<OrderEntity> orders;
}
