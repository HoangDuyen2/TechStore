package hcmute.edu.vn.techstore.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "cart")
    private UserEntity user;

    @OneToMany(mappedBy = "cart")
    private List<CartDetailEntity> cartDetails;

    @Column(name = "total_price",nullable = false, columnDefinition = "DECIMAL(10, 2) DEFAULT 0")
    private BigDecimal totalPrice;
}
