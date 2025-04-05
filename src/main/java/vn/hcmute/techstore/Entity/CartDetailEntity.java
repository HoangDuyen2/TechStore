package vn.hcmute.techstore.Entity;

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
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cartId", referencedColumnName = "id")
    private CartEntity cart;

    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "id")
    private ProductEntity product;

    @Column(name = "quantity", nullable = false)
    private int quantity;
}