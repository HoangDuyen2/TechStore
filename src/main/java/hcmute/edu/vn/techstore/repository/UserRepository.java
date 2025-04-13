package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByAccount_Email(String username);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
}
