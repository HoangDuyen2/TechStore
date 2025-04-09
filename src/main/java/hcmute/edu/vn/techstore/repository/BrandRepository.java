package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.entity.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find a brand by its name:
    // Optional<BrandEntity> findByName(String name);
}
