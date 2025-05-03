package hcmute.edu.vn.techstore.repository.custome;


import hcmute.edu.vn.techstore.builder.ProductFilterBuilder;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductRepositoryCustom {
    public static Specification<ProductEntity> filter(ProductFilterBuilder builder) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by product name (like)
            if (builder.getName() != null && !builder.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + builder.getName().toLowerCase() + "%"
                ));
            }

            // Filter by brand name
            if (builder.getBrandNames() != null && !builder.getBrandNames().isEmpty()) {
                List<String> filteredBrandNames = builder.getBrandNames().stream()
                        .filter(name -> !name.trim().isEmpty())
                        .collect(Collectors.toList());

                if (!filteredBrandNames.isEmpty()) {
                    Join<ProductEntity, BrandEntity> brandJoin = root.join("brand");
                    predicates.add(brandJoin.get("name").in(filteredBrandNames));

                    predicates.add(criteriaBuilder.isTrue(brandJoin.get("isActived")));
                }
            } else {
                Join<ProductEntity, BrandEntity> brandJoin = root.join("brand");
                predicates.add(criteriaBuilder.isTrue(brandJoin.get("isActived")));
            }

            // Filter by price range
            if (builder.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), builder.getMinPrice()));
            }
            if (builder.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), builder.getMaxPrice()));
            }

           if (builder.getSim() != null && !builder.getSim().isEmpty()) {
                List<Predicate> simPredicates = builder.getSim().stream()
                        .filter(val -> val != null && !val.trim().isEmpty())
                        .map(val -> criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("sim")),
                                "%" + val.toLowerCase() + "%"
                        ))
                        .collect(Collectors.toList());
                predicates.add(criteriaBuilder.or(simPredicates.toArray(new Predicate[0])));
            }

            if (builder.getConnectivity() != null && !builder.getConnectivity().isEmpty()) {
                List<Predicate> connPredicates = builder.getConnectivity().stream()
                        .filter(val -> val != null && !val.trim().isEmpty())
                        .map(val -> criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("connectivity")),
                                "%" + val.toLowerCase() + "%"
                        ))
                        .collect(Collectors.toList());
                predicates.add(criteriaBuilder.or(connPredicates.toArray(new Predicate[0])));
            }

            if (builder.getOs() != null && !builder.getOs().isEmpty()) {
                List<Predicate> osPredicates = builder.getOs().stream()
                        .filter(val -> val != null && !val.trim().isEmpty())
                        .map(val -> criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("operatingSystem")),
                                "%" + val.toLowerCase() + "%"
                        ))
                        .collect(Collectors.toList());
                predicates.add(criteriaBuilder.or(osPredicates.toArray(new Predicate[0])));
            }

            if (builder.getProcessor() != null && !builder.getProcessor().isEmpty()) {
                List<Predicate> processorPredicates = builder.getProcessor().stream()
                        .filter(val -> val != null && !val.trim().isEmpty())
                        .map(val -> criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("processor")),
                                "%" + val.toLowerCase() + "%"
                        ))
                        .collect(Collectors.toList());
                predicates.add(criteriaBuilder.or(processorPredicates.toArray(new Predicate[0])));
            }

            predicates.add(criteriaBuilder.isTrue(root.get("actived")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
