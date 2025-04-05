package hcmute.edu.vn.techstore.Entity;

import hcmute.edu.vn.techstore.Enum.ERole;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private ERole name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<UserEntity> userEntities;
}
