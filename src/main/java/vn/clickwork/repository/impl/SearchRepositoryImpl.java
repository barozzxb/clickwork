package vn.clickwork.repository.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import vn.clickwork.entity.Employer;
import vn.clickwork.entity.Job;
import vn.clickwork.enumeration.EAccountStatus;
import vn.clickwork.repository.SearchRepository;

@Repository
public class SearchRepositoryImpl implements SearchRepository {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public List<Job> searchKeyword(String keyword) {
	    String jpql =
	      "SELECT DISTINCT j " +
	      "FROM Job j " +
	      "LEFT JOIN j.tags t " +                     // join bảng tags (element collection)
	      "WHERE j.isActive = true " +
	      "  AND (LOWER(j.name) LIKE :kw " +
	      "       OR LOWER(t) LIKE :kw)";             // so sánh keyword với từng tag
	    return em.createQuery(jpql, Job.class)
	             .setParameter("kw", "%" + keyword.toLowerCase() + "%")
	             .getResultList();
	}


	@Override
	public List<Job> searchKeyword(String keyword, int page, int size) {
		String jpql = "SELECT j FROM Job j "
				+ "WHERE j.isActive = true "
				+ "  AND (LOWER(j.name) LIKE :kw "
				+ "       OR LOWER(j.tags)  LIKE :kw)";
		return em.createQuery(jpql, Job.class)
				.setParameter("kw", "%" + keyword.toLowerCase() + "%")
				.setFirstResult(page * size)
				.setMaxResults(size)
				.getResultList();
	}
	
	@Override
	public List<Employer> searchEmployer(String keyword) {
	    String jpql =
	        "SELECT DISTINCT e " +
	        "FROM Employer e " +
	        "JOIN e.account a " +                       // JOIN vào Account
	        "WHERE a.status = :activeStatus " +          // chỉ lấy account ACTIVE
	        "  AND (LOWER(e.fullname) LIKE :kw " +
	        "       OR LOWER(e.overview) LIKE :kw)";
	    return em.createQuery(jpql, Employer.class)
	             .setParameter("activeStatus", EAccountStatus.ACTIVE)
	             .setParameter("kw", "%" + keyword.toLowerCase() + "%")
	             .getResultList();
	}

}
