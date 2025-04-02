package com.example.techstore.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "wallets")
public class WalletEntity {
    @Id
    @Column(name = "walletId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private BigDecimal balance;
}
