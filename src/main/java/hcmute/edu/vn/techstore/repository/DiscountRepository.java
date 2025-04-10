package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.entity.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<DiscountEntity, Long> {
    DiscountEntity findByName(String name);
    DiscountEntity findByCode(String code);
}
