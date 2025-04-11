package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.Enum.ERole;
import hcmute.edu.vn.techstore.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByName(ERole name);
}
