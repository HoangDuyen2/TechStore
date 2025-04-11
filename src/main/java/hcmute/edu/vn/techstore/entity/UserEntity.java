package hcmute.edu.vn.techstore.entity;

import hcmute.edu.vn.techstore.Enum.EGender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "users")
public class UserEntity extends TrackingDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;


    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "dateOfBirth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private EGender gender;

    @Column(name = "address")
    private String address;

    @OneToOne
    @JoinColumn(name = "imageId", referencedColumnName = "id")
    private ImageEntity image;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "accountId", referencedColumnName = "id")
    private AccountEntity account;

    @ManyToOne
    @JoinColumn(name = "roleId", referencedColumnName = "id")
    private RoleEntity role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cartId", referencedColumnName = "id")
    private CartEntity cart;

    @OneToMany(mappedBy = "user")
    private List<OrderEntity> orders;

    @Column(name = "cccd")
    private String cccd;

    @Column(name = "relativeName")
    private String relativeName;

    @Column(name = "relativePhoneNumber")
    private String relativePhoneNumber;

    @OneToMany(mappedBy = "user")
    private List<ReviewEntity> reviews;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private WalletEntity wallet;
}
