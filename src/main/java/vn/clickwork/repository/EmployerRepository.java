package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Employer;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {

}
