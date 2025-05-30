package hcmute.edu.vn.techstore.entity;

import hcmute.edu.vn.techstore.Enum.EDiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "discountType", nullable = false)
    private EDiscountType discountType;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "expiriedDate")
    private LocalDate expiredDate;

    @Column(name = "quantity")  
    private int quantity;

    @ManyToMany(mappedBy = "discounts")
    private Set<OrderEntity> orders;
}
