package hcmute.edu.vn.techstore.entity;

import hcmute.edu.vn.techstore.Enum.EDiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    @Column(name = "discountType", nullable = false)
    private EDiscountType discountType;

    @Column(name = "amount", columnDefinition = "DECIMAL(10, 2) DEFAULT 0")
    private BigDecimal amount;

    @Column(name = "expiriedDate")
    private LocalDate expiredDate;

    @Column(name = "quantity")  
    private int quantity;

    @OneToMany(mappedBy = "discount")
    private List<OrderEntity> orders;
}
