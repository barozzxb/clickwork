package vn.clickwork.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import vn.clickwork.entity.Job;
import vn.clickwork.model.request.JobFilterRequest;

public class JobSpecification {
	
	public static Specification<Job> filter(JobFilterRequest request) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			
			if (request.getName() != null && !request.getName().isEmpty()) {
				predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
			}
			if (request.getJobType() != null && !request.getJobType().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("jobtype"), request.getJobType()));
			}
//			if (request.getSalaryMax() != null && !request.getSalaryMax().isEmpty()) {
//				predicates.add(criteriaBuilder.equal(root.get("salary"), request.getSalary()));
//			}
			if (request.getTags() != null && !request.getTags().isEmpty()) {
				for (String tag : request.getTags()) {
					predicates.add(criteriaBuilder.isMember(tag, root.get("tags")));
				}
			}
			if (request.getEmployerId() != null) {
				predicates.add(criteriaBuilder.like(root.get("employerid"), "%" + request.getEmployerId() + "%"));
			}
			if (request.getDateFrom() != null && request.getDateTo() != null) {
				predicates.add(criteriaBuilder.between(root.get("createdat"), request.getDateFrom(), request.getDateTo()));
			}
			
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

}
