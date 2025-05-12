package vn.clickwork.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import vn.clickwork.entity.Job;
import vn.clickwork.model.request.JobFilterRequest;
import vn.clickwork.repository.JobRepositoryCustom;

import java.util.List;

@Repository
public class JobRepositoryCustomImpl implements JobRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Job> findJobsWithFilter(JobFilterRequest request) {
        String jpql = "SELECT j FROM Job j WHERE j.isActive = true";
        TypedQuery<Job> query = entityManager.createQuery(jpql, Job.class);
        return query.getResultList();
    }

    @Override
    public List<Job> filterJobs(JobFilterRequest filter) {
        return List.of();
    }
}
