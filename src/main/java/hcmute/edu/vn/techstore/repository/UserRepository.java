package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.Enum.ERole;
import hcmute.edu.vn.techstore.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByAccount_Email(String username);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    @Query("select u from UserEntity u where u.role.name != ?1")
    List<UserEntity> findByRoleNotContains(ERole role);
}
