package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.entity.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
    int countAllByProduct_Brand_Id(Long productBrandId);
}
