package vn.clickwork.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n JOIN n.admins a WHERE a.account.username = :username")
    List<Notification> findByAdminUsername(@Param("username") String username);

    @Query("SELECT n FROM Notification n JOIN n.admins a WHERE a.account.username = :username AND n.isRead = false")
    List<Notification> findUnreadByAdminUsername(@Param("username") String username);

    @Query("SELECT COUNT(n) FROM Notification n JOIN n.admins a WHERE a.account.username = :username AND n.isRead = false")
    long countUnreadByAdminUsername(@Param("username") String username);

    @Query("SELECT n FROM Notification n JOIN n.employers e WHERE e.account.username = :username")
    List<Notification> findByEmployerUsername(@Param("username") String username);
}
