package hcmute.edu.vn.techstore.repository;

import hcmute.edu.vn.techstore.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    boolean existsByName(String name);
}
