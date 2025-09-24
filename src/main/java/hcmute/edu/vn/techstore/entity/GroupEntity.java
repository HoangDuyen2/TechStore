package hcmute.edu.vn.techstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "groups")
public class GroupEntity extends TrackingDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "description",nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "groups")
    private Set<UserEntity> users = new HashSet<>();
}
