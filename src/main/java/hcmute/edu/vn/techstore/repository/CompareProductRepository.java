package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.entity.CompareProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompareProductRepository extends JpaRepository<CompareProductEntity, Long> {
    
    List<CompareProductEntity> findByUser_IdAndIsActiveTrue(Long userId);
    
    Optional<CompareProductEntity> findByUser_IdAndProduct_IdAndIsActiveTrue(Long userId, Long productId);
    
    @Query("SELECT COUNT(cp) FROM CompareProductEntity cp WHERE cp.user.id = :userId AND cp.isActive = true")
    int countByUser_IdAndIsActiveTrue(@Param("userId") Long userId);
    
    void deleteByUser_IdAndIsActiveTrue(Long userId);
}