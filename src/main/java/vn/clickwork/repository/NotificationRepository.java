package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{

}
