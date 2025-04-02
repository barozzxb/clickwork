package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>{

}
