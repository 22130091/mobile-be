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

CREATE TABLE IF NOT EXISTS payments (
    payment_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id INT UNSIGNED DEFAULT NULL,
    user_id INT UNSIGNED DEFAULT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status ENUM('pending','success','failed','refunded') NOT NULL DEFAULT 'pending',
    transaction_id VARCHAR(255) UNIQUE,
    vnpay_txn_ref VARCHAR(255) UNIQUE,
    vnpay_transaction_no VARCHAR(255),
    vnpay_response_code VARCHAR(10),
    vnpay_secure_hash VARCHAR(512),
    bank_code VARCHAR(50),
    card_type VARCHAR(50),
    payment_date DATETIME,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_vnpay_txn_ref (vnpay_txn_ref),
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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

-- Thêm dữ liệu mẫu cho cart_items với nhiều món
INSERT INTO cart_items (cart_id, dish_id, quantity, unit_price, special_requests)
VALUES
(1, 1, 2, 35000, 'Ít cay'),
(1, 2, 1, 65000, 'Không rau mùi'),
(2, 3, 1, 85000, 'Bò chín kỹ'),
(2, 4, 2, 25000, NULL),
(3, 5, 3, 30000, 'Thêm đá'),
(3, 7, 1, 150000, 'Ít cay vừa ăn'),
(4, 6, 2, 45000, NULL),
(4, 9, 1, 95000, 'Tôm tươi'),
(5, 8, 1, 120000, 'Cay nồng'),
(5, 10, 2, 40000, 'Chiên giòn');
