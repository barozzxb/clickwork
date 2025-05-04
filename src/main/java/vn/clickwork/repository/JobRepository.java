package vn.clickwork.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Job;
import vn.clickwork.entity.SaveJob;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>{

	List<Job> findByTags(String tags);
	
    List<Job> findBySave(SaveJob save);
    
    Optional<Job> findById(Long id);


}
