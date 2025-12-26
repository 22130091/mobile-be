
package posmobile.service;

import posmobile.entity.Notification;
import posmobile.entity.UserNotification;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    Notification create(Notification notification);
    List<Notification> findAll();
    Optional<Notification> findById(Long id);

    UserNotification sendToUser(Long userId, Long notificationId);
    List<UserNotification> getForUser(Long userId);
    UserNotification markAsRead(Long id);
}
