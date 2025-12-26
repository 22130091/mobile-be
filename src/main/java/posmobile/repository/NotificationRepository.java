
package posmobile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import posmobile.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
