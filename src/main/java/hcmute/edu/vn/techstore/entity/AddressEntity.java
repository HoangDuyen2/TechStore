package hcmute.edu.vn.techstore.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String detail;

    @Column(nullable = false, length = 100)
    private String street;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String province;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference(value = "user-address")
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private UserEntity user;
}
