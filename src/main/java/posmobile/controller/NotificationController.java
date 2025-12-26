package posmobile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import posmobile.entity.Notification;
import posmobile.entity.UserNotification;
import posmobile.service.NotificationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // 🔹 Tạo một thông báo mới (dành cho admin)
    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody Notification notification) {
        Notification created = notificationService.create(notification);
        return ResponseEntity
                .created(URI.create("/api/notifications/" + created.getId()))
                .body(created);
    }

    // 🔹 Gửi thông báo cho người dùng cụ thể
    @PostMapping("/send")
    public ResponseEntity<UserNotification> sendToUser(
            @RequestParam Long userId,
            @RequestParam Long notificationId
    ) {
        UserNotification userNotification = notificationService.sendToUser(userId, notificationId);
        return ResponseEntity.ok(userNotification);
    }

    // 🔹 Lấy danh sách thông báo của một người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserNotification>> getForUser(@PathVariable Long userId) {
        List<UserNotification> notifications = notificationService.getForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    // 🔹 Đánh dấu thông báo là đã đọc
    @PutMapping("/read/{id}")
    public ResponseEntity<UserNotification> markAsRead(@PathVariable Long id) {
        UserNotification updated = notificationService.markAsRead(id);
        return ResponseEntity.ok(updated);
    }
}
