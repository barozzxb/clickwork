package vn.clickwork.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>{

	List<Job> findByTags(String tags);
}
