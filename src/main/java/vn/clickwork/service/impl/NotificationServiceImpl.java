package vn.clickwork.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.clickwork.entity.Admin;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.Notification;
import vn.clickwork.entity.Employer;
import vn.clickwork.enumeration.ENotiType;
import vn.clickwork.model.dto.NotificationDTO;
import vn.clickwork.model.Response;
import vn.clickwork.repository.AdminRepository;
import vn.clickwork.repository.NotificationRepository;
import vn.clickwork.repository.EmployerRepository;
import vn.clickwork.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Override
    public List<NotificationDTO> getAdminNotifications(String username) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        logger.info("Fetching notifications for admin: {}", username);
        Admin admin = adminRepository.findByAccountUsername(username);
        if (admin == null) {
            logger.error("Admin not found for username: {}", username);
            throw new RuntimeException("Admin not found");
        }

        List<Notification> notifications = admin.getNotifications();
        if (notifications == null) {
            return new ArrayList<>();
        }

        return notifications.stream()
                .map(this::mapToNotificationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long getUnreadNotificationCount(String username) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        logger.info("Counting unread notifications for admin: {}", username);
        Admin admin = adminRepository.findByAccountUsername(username);
        if (admin == null) {
            logger.error("Admin not found for username: {}", username);
            throw new RuntimeException("Admin not found");
        }

        List<Notification> notifications = admin.getNotifications();
        if (notifications == null) {
            return 0;
        }

        return notifications.stream()
                .filter(notification -> !notification.isRead())
                .count();
    }

    @Override
    @Transactional
    public void markNotificationAsRead(String username, Long notificationId) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (notificationId == null) {
            logger.error("Notification ID is null");
            throw new IllegalArgumentException("Notification ID cannot be null");
        }

        logger.info("Marking notification {} as read for admin: {}", notificationId, username);
        Admin admin = adminRepository.findByAccountUsername(username);
        if (admin == null) {
            logger.error("Admin not found for username: {}", username);
            throw new RuntimeException("Admin not found");
        }

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // Check if this notification belongs to the admin
        if (!admin.getNotifications().contains(notification)) {
            logger.error("Notification {} does not belong to admin: {}", notificationId, username);
            throw new RuntimeException("Notification does not belong to this admin");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
        logger.info("Successfully marked notification {} as read for admin: {}", notificationId, username);
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead(String username) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        logger.info("Marking all notifications as read for admin: {}", username);
        Admin admin = adminRepository.findByAccountUsername(username);
        if (admin == null) {
            logger.error("Admin not found for username: {}", username);
            throw new RuntimeException("Admin not found");
        }

        List<Notification> notifications = admin.getNotifications();
        if (notifications == null || notifications.isEmpty()) {
            return;
        }

        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
        logger.info("Successfully marked all notifications as read for admin: {}", username);
    }

    @Override
    public void createNotificationForApplicant(Applicant applicant, String content) {
        Notification notification = new Notification();
        notification.setTitle("Thông báo tuyển dụng");
        notification.setContent(content);
        notification.setType(ENotiType.INFORM);
        notification.setSendat(new java.sql.Timestamp(System.currentTimeMillis()));
        notification.setRead(false);
        notification.setApplicants(List.of(applicant));
        notificationRepository.save(notification);
    }

    @Override
    public Response getEmployerNotifications(String username) {
        Employer employer = employerRepository.findByAccount_Username(username)
                .orElse(null);
        if (employer == null) {
            return new Response(false, "Không tìm thấy employer", null);
        }
        List<NotificationDTO> dtos = employer.getNotifications()
                .stream()
                .map(this::mapToNotificationDTO)
                .collect(Collectors.toList());
        return new Response(true, "Lấy thông báo thành công", dtos);
    }

    @Override
    @Transactional
    public void markEmployerNotificationAsRead(String username, Long notificationId) {
        Employer employer = employerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // Kiểm tra notification có thuộc employer này không
        if (employer.getNotifications() == null || !employer.getNotifications().contains(notification)) {
            throw new RuntimeException("Notification does not belong to this employer");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationDTO mapToNotificationDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setType(notification.getType().toString());
        dto.setSendAt(notification.getSendat());
        dto.setRead(notification.isRead());
        return dto;
    }
}
