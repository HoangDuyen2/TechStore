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
@Table(name = "payment")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long paymentId;

    @Column(name = "paymentName", nullable = false, length = 255)
    private String name;

    @OneToMany(mappedBy = "payment")
    private List<OrderEntity> orders;
}
