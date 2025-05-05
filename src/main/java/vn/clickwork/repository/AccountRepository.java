package vn.clickwork.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Account;
import vn.clickwork.enumeration.EAccountStatus;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>{

	Optional<Account> findByUsername(String username);
	long countByStatus(Enum<?> status);

	@Query(value = "SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS count " +
			"FROM Account " +
			"GROUP BY DATE_FORMAT(created_at, '%Y-%m')", nativeQuery = true)
	List<Object[]> countAccountsByMonth();
}
