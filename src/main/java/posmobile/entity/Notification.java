package posmobile.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String type;        // Ví dụ: "System", "Promotion", "Order"
    private String priority;    // Ví dụ: "Low", "Medium", "High"

    private LocalDateTime createdAt;

    // 🔹 Một thông báo có thể được gửi đến nhiều người dùng
//    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<UserNotification> userNotifications;

    // 🔹 Gán thời gian tạo trước khi insert
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    // ✅ Bạn có thể giữ lại getter/setter thủ công nếu muốn override
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
