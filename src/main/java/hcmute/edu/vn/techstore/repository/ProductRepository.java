package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    int countByBrand_Id(Long brandId);

    // find product sort by createdAt and updatedAt
    @Query("SELECT p FROM ProductEntity p ORDER BY p.createdAt DESC, p.updatedAt DESC")
    List<ProductEntity> findByOrderByCreatedDateAndLastModifiedDateDesc(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p JOIN OrderDetailEntity od ON p.id = od.product.id " +
            "GROUP BY p.id ORDER BY SUM(od.quantity) DESC")
    List<ProductEntity> findTopMostBuyingProductsByOrderDetail(Pageable pageable);
}
