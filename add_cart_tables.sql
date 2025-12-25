-- =============================================
-- SCRIPT THÊM BẢNG CARTS VÀ CART_ITEMS
-- Script này chỉ thêm 2 bảng carts và cart_items
-- mà không ảnh hưởng đến dữ liệu hiện có
-- =============================================

USE mobile_pos;

CREATE TABLE IF NOT EXISTS carts (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL UNIQUE COMMENT 'Mã phiên duy nhất cho giỏ hàng',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Ngày tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Ngày cập nhật',
    INDEX idx_session_id (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Giỏ hàng của khách hàng';

CREATE TABLE IF NOT EXISTS cart_items (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    cart_id INT UNSIGNED NOT NULL COMMENT 'FK tới carts',
    dish_id INT UNSIGNED NOT NULL COMMENT 'FK tới dishes',
    quantity INT UNSIGNED NOT NULL DEFAULT 1 COMMENT 'Số lượng món',
    unit_price DECIMAL(10,2) COMMENT 'Đơn giá',
    special_requests TEXT COMMENT 'Yêu cầu đặc biệt',
    INDEX idx_cart_id (cart_id),
    INDEX idx_dish_id (dish_id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_dish FOREIGN KEY (dish_id) REFERENCES dishes(dish_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Chi tiết món ăn trong giỏ hàng';

-- Thêm dữ liệu mẫu cho bảng carts (chỉ thêm nếu bảng trống và có dữ liệu dish)
-- Kiểm tra trước khi insert
SET @cart_count = (SELECT COUNT(*) FROM carts);
SET @dish_count = (SELECT COUNT(*) FROM dishes);

-- Chỉ thêm dữ liệu mẫu nếu bảng carts trống và có ít nhất 10 dishes
INSERT INTO carts (session_id, created_at, updated_at)
SELECT 'session001', '2025-11-08 10:00:00', '2025-11-08 10:30:00'
WHERE @cart_count = 0 AND @dish_count >= 10
UNION ALL
SELECT 'session002', '2025-11-08 11:15:00', '2025-11-08 11:45:00'
WHERE @cart_count = 0 AND @dish_count >= 10
UNION ALL
SELECT 'session003', '2025-11-08 12:30:00', '2025-11-08 13:00:00'
WHERE @cart_count = 0 AND @dish_count >= 10
UNION ALL
SELECT 'session004', '2025-11-08 14:20:00', '2025-11-08 14:50:00'
WHERE @cart_count = 0 AND @dish_count >= 10
UNION ALL
SELECT 'session005', '2025-11-08 15:10:00', '2025-11-08 15:40:00'
WHERE @cart_count = 0 AND @dish_count >= 10;

-- Thêm dữ liệu mẫu cho cart_items nếu có carts và dishes
SET @cart_item_count = (SELECT COUNT(*) FROM cart_items);

INSERT INTO cart_items (cart_id, dish_id, quantity, unit_price, special_requests)
SELECT c.id, d.dish_id, 2, d.price, 'Ít cay'
FROM carts c
CROSS JOIN dishes d
WHERE c.session_id = 'session001'
  AND @cart_item_count = 0
  AND @dish_count >= 1
LIMIT 1;