package vn.clickwork.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.clickwork.entity.Support;
import vn.clickwork.enumeration.EResponseStatus;

@Repository
public interface SupportRepository extends JpaRepository<Support, Long> {
    List<Support> findByStatus(EResponseStatus status);

    Page<Support> findAll(Specification<Support> spec, Pageable pageable);
}
