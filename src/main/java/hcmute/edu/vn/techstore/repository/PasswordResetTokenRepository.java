package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    
    Optional<PasswordResetTokenEntity> findByToken(String token);
    
    Optional<PasswordResetTokenEntity> findByEmailAndUsedFalse(String email);
    
    @Query("SELECT COUNT(p) FROM PasswordResetTokenEntity p WHERE p.email = :email AND p.createdAt >= :since")
    int countByEmailAndCreatedAtAfter(@Param("email") String email, @Param("since") LocalDateTime since);
    
    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetTokenEntity p SET p.used = true WHERE p.token = :token")
    void markTokenAsUsed(@Param("token") String token);
    
    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetTokenEntity p SET p.attempts = p.attempts + 1 WHERE p.token = :token")
    void incrementAttempts(@Param("token") String token);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetTokenEntity p WHERE p.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
