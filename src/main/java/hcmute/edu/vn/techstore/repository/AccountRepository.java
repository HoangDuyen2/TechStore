package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    AccountEntity findByEmailAndPassword(String email, String password);
    Optional<AccountEntity> findByEmail(String email);
}
