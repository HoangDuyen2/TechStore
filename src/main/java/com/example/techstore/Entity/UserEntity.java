package com.example.techstore.Entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Sử dụng bảng chung cho tất cả các loại sản phẩm
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING) // Cột phân biệt loại sản phẩm
public abstract class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private int userId;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "address")
    private String address;

    @Column(name = "phoneNumber")
    private String phone;

    @Column(name = "dateOfBirth")
    private String dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @OneToOne(cascade = CascadeType.ALL)
    private ImageEntity image;

    @OneToOne(cascade = CascadeType.ALL)
    private AccountEntity account;

    @ManyToOne
    @JoinColumn(name = "roleId", referencedColumnName = "roleId")
    private RoleEntity role;
}
