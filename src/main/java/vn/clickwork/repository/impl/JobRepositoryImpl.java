package vn.clickwork.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import vn.clickwork.entity.Job;
import vn.clickwork.model.request.JobFilterRequest;
import vn.clickwork.repository.JobRepositoryCustom;


public class JobRepositoryImpl implements JobRepositoryCustom {

	@PersistenceContext
    private EntityManager em;

    @Override
    public List<Job> filterJobs(JobFilterRequest filter) {
        StringBuilder jpql = new StringBuilder("SELECT j FROM Job j WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();

        if (filter.getName() != null && !filter.getName().isBlank()) {
            jpql.append("AND LOWER(j.name) LIKE LOWER(:name) ");
            params.put("name", "%" + filter.getName() + "%");
        }
        if (filter.getDateFrom() != null) {
            jpql.append("AND j.createdAt >= :dateFrom ");
            params.put("dateFrom", filter.getDateFrom());
        }
        if (filter.getDateTo() != null) {
            jpql.append("AND j.createdAt <= :dateTo ");
            params.put("dateTo", filter.getDateTo());
        }
        if (filter.getSalaryMin() != null) {
            jpql.append("AND j.salary >= :salaryMin ");
            params.put("salaryMin", filter.getSalaryMin());
        }
        if (filter.getSalaryMax() != null) {
            jpql.append("AND j.salary <= :salaryMax ");
            params.put("salaryMax", filter.getSalaryMax());
        }
        if (filter.getEmployerId() != null) {
            jpql.append("AND j.employer.id = :employerId ");
            params.put("employerId", filter.getEmployerId());
        }
        if (filter.getJobType() != null && !filter.getJobType().isBlank()) {
            jpql.append("AND j.jobType = :jobType ");
            params.put("jobType", filter.getJobType());
        }

        TypedQuery<Job> query = em.createQuery(jpql.toString(), Job.class);
        params.forEach(query::setParameter);
        return query.getResultList();
    }

}
