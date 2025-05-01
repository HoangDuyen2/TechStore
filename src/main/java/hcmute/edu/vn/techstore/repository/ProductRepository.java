package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    int countByBrand_Id(Long brandId);

    // find product sort by createdAt and updatedAt
    @Query("SELECT p FROM ProductEntity p ORDER BY p.createdAt DESC")
    List<ProductEntity> findByOrderByCreatedDateDesc();

    @Query("SELECT p FROM ProductEntity p JOIN OrderDetailEntity od ON p.id = od.product.id " +
            "GROUP BY p.id ORDER BY SUM(od.quantity) DESC")
    List<ProductEntity> findTopMostBuyingProductsByOrderDetail();

    @Override
    Optional<ProductEntity> findById(Long aLong);

    @Query("SELECT p FROM ProductEntity p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND p.actived = true " +
            "AND p.brand.isActived = true")
    List<ProductEntity> searchByKeywordAndActivedBrand(@Param("keyword") String keyword);

    ProductEntity findByName(String name);
}
