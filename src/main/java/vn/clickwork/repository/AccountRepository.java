package vn.clickwork.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Account;
import vn.clickwork.enumeration.EAccountStatus;
import vn.clickwork.enumeration.ERole;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>{

	Optional<Account> findByUsername(String username);
	long countByStatus(Enum<?> status);

	@Query(value = "SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS count " +
			"FROM account " +
			"GROUP BY DATE_FORMAT(created_at, '%Y-%m')", nativeQuery = true)
	List<Object[]> countAccountsByMonth();

    Page<Account> findAll(Specification<Account> spec, Pageable pageable);

	List<Account> findByRole(ERole role);

	List<Account> findAllByStatus(EAccountStatus status);

	@Query("SELECT a.role AS role, COUNT(a) AS count FROM Account a GROUP BY a.role")
	List<Object[]> countAccountsByRole();

	@Query("SELECT a.status AS status, COUNT(a) AS count FROM Account a GROUP BY a.status")
	List<Object[]> countAccountsByStatus();
}
