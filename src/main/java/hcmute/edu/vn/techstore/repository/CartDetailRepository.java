package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.entity.CartDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetailEntity, Long> {
    CartDetailEntity findByProduct_Id(Long product_id);
}
