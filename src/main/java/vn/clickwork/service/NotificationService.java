package vn.clickwork.service;

import java.util.List;
import vn.clickwork.model.dto.NotificationDTO;
import vn.clickwork.model.Response;

public interface NotificationService {
    /**
     * Get all notifications for an admin
     * 
     * @param username Admin's username
     * @return List of notifications
     */
    List<NotificationDTO> getAdminNotifications(String username);

    /**
     * Get count of unread notifications for an admin
     * 
     * @param username Admin's username
     * @return Count of unread notifications
     */
    long getUnreadNotificationCount(String username);

    /**
     * Mark a notification as read for an admin
     * 
     * @param username       Admin's username
     * @param notificationId Notification ID
     */
    void markNotificationAsRead(String username, Long notificationId);

    /**
     * Mark all notifications as read for an admin
     * 
     * @param username Admin's username
     */
    void markAllNotificationsAsRead(String username);

    void createNotificationForApplicant(vn.clickwork.entity.Applicant applicant, String content);

    Response getEmployerNotifications(String username);

    void markEmployerNotificationAsRead(String username, Long notificationId);
}
