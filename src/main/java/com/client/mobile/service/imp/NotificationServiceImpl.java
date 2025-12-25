package com.client.mobile.service.imp;

import com.client.mobile.entity.Notification;
import com.client.mobile.entity.User;
import com.client.mobile.entity.UserNotification;
import com.client.mobile.repository.NotificationRepository;
import com.client.mobile.repository.UserNotificationRepository;
import com.client.mobile.repository.UserRepository;
import com.client.mobile.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserNotificationRepository userNotificationRepository,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.userRepository = userRepository;
    }

    // 🔹 Tạo một thông báo mới (lưu trong bảng notifications)
    @Override
    public Notification create(Notification notification) {
        return notificationRepository.save(notification);
    }

    // 🔹 Lấy tất cả thông báo (dùng cho admin xem danh sách)
    @Override
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    // 🔹 Tìm thông báo theo ID
    @Override
    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }

    // 🔹 Gửi thông báo cụ thể đến người dùng (thêm bản ghi vào user_notifications)
    @Override
    public UserNotification sendToUser(Long userId, Long notificationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        UserNotification userNotification = new UserNotification();
        userNotification.setUser(user);
        userNotification.setNotification(notification);
        userNotification.setIsRead(false);
        userNotification.setDeliveredAt(LocalDateTime.now());

        return userNotificationRepository.save(userNotification);
    }


    // 🔹 Lấy danh sách thông báo của một user cụ thể
    @Override
    public List<UserNotification> getForUser(Long userId) {
        return userNotificationRepository.findByUser_UserId(userId);
    }

    // 🔹 Đánh dấu thông báo là đã đọc
    @Override
    public UserNotification markAsRead(Long id) {
        UserNotification userNotification = userNotificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserNotification not found"));

        userNotification.setIsRead(true);
        userNotification.setReadAt(LocalDateTime.now());

        return userNotificationRepository.save(userNotification);
    }
}
