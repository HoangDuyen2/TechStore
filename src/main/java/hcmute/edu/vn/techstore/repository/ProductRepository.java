package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    int countByBrand_Id(Long brandId);
}
