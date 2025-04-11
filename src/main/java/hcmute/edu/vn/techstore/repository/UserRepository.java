package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByAccount_Email(String username);
    UserEntity findByPhoneNumber(String phoneNumber);
}
