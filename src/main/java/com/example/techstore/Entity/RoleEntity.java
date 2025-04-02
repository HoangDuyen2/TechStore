package com.example.techstore.Entity;

import com.example.techstore.Enum.ERole;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "roles")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roleId")
    private int roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "roleName", nullable = false)
    private ERole roleName;
}
