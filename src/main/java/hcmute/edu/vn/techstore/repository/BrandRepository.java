package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.entity.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, Long> {
    BrandEntity findByName(String name);
    List<BrandEntity> findAllByIsActivedTrue();
}
