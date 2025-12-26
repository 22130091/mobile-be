package posmobile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import posmobile.entity.UserNotification;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    // ✅ Lấy tất cả thông báo của một user (dựa trên khóa ngoại user → userId)
    List<UserNotification> findByUser_UserId(Long userId);
}
