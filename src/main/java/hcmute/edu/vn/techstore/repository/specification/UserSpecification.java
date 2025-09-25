package hcmute.edu.vn.techstore.repository.specification;

import hcmute.edu.vn.techstore.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<UserEntity> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("account").get("email"), email);
    }

    public static Specification<UserEntity> hasPhone(String phone) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("phoneNumber"), phone);
    }

    public static Specification<UserEntity> emailContains(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("account").get("email"), "%" + email + "%");
    }

    public static Specification<UserEntity> phoneContains(String phone) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("phoneNumber"), "%" + phone + "%");
    }
}
