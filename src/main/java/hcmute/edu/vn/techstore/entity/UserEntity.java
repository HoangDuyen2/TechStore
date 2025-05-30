package hcmute.edu.vn.techstore.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(name = "image")
    private String image;

    @OneToOne(cascade = CascadeType.PERSIST)
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

    @Column(name = "isActived")
    private boolean isActived;

    @OneToMany(mappedBy = "user")
    private List<ReviewEntity> reviews;
}
