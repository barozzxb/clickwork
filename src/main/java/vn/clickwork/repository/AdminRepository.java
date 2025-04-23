package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

}
