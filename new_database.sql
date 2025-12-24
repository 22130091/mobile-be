-- =============================================
-- TẠO CƠ SỞ DỮ LIỆU
-- =============================================
CREATE DATABASE IF NOT EXISTS mobile_pos;
USE mobile_pos;

-- =============================================
-- XÓA TOÀN BỘ BẢNG NẾU ĐÃ TỒN TẠI
-- =============================================

SET FOREIGN_KEY_CHECKS = 0; -- Tạm tắt kiểm tra khóa ngoại
DROP TABLE IF EXISTS 
    user_notifications,
    notifications,
    invoices,
    order_items,
    orders,
    dish_ingredients,
    ingredients,
    dish_allergens,
    allergens,
    dishes,
    categories,
    reservation_tables,
    reservations,
    dining_tables,
    user_role,
    role_permission,
    permissions,
    roles,
    users;
SET FOREIGN_KEY_CHECKS = 1; -- Bật lại kiểm tra khóa ngoại


-- ==========================================================
-- BẢNG NGƯỜI DÙNG (USERS) - ĐÃ SỬA
-- ==========================================================
CREATE TABLE users (
    user_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL COMMENT 'Email đăng nhập',
    password_hash VARCHAR(255) NOT NULL COMMENT 'Mật khẩu đã mã hóa',
    full_name VARCHAR(150) COMMENT 'Họ tên người dùng',
    phone VARCHAR(30) UNIQUE COMMENT 'Số điện thoại duy nhất',
    gender ENUM('male','female','other') DEFAULT 'other' COMMENT 'Giới tính',
    dob DATE COMMENT 'Ngày sinh',
    avatar VARCHAR(255) DEFAULT NULL COMMENT 'Đường dẫn ảnh đại diện', -- <--- ĐÃ SỬA LỖI CÚ PHÁP
    -- --- ĐÃ XÓA CỘT "role ENUM" GÂY XUNG ĐỘT TẠI ĐÂY ---
    status ENUM('active','inactive','banned') DEFAULT 'active' COMMENT 'Trạng thái tài khoản',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Ngày tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Ngày cập nhật',
    INDEX (email),
    INDEX (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Người dùng (khách hàng, nhân viên, quản lý, admin)';


-- ==========================================================
-- BẢNG BÀN ĂN (DINING TABLES)
-- ==========================================================
CREATE TABLE dining_tables (
    table_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    table_code VARCHAR(30) NOT NULL UNIQUE COMMENT 'Mã bàn, ví dụ A1, B2',
    seating_capacity TINYINT UNSIGNED NOT NULL COMMENT 'Sức chứa (số ghế)',
    area VARCHAR(100) COMMENT 'Khu vực bàn',
    status ENUM('available','occupied','reserved','outofservice') DEFAULT 'available' COMMENT 'Trạng thái bàn',
    notes VARCHAR(255) COMMENT 'Ghi chú thêm',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Ngày tạo'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bàn ăn trong nhà hàng';


-- ==========================================================
-- BẢNG ĐẶT BÀN (RESERVATIONS)
-- ==========================================================
CREATE TABLE reservations (
    reservation_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED COMMENT 'Người đặt bàn (FK tới users)',
    booking_code VARCHAR(60) NOT NULL UNIQUE COMMENT 'Mã đặt bàn duy nhất',
    contact_name VARCHAR(150) COMMENT 'Tên người liên hệ',
    contact_phone VARCHAR(30) COMMENT 'Số điện thoại liên hệ',
    contact_email VARCHAR(255) COMMENT 'Email liên hệ',
    total_guests SMALLINT UNSIGNED NOT NULL COMMENT 'Tổng số khách',
    reservation_status ENUM('booked','checked-in','completed','cancelled') DEFAULT 'booked' COMMENT 'Trạng thái đặt bàn',
    otp_code VARCHAR(10) COMMENT 'Mã OTP xác nhận',
    otp_verified BOOLEAN DEFAULT FALSE COMMENT 'Đã xác thực OTP chưa',
    otp_expires_at DATETIME COMMENT 'Thời hạn OTP',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Đặt bàn của khách hàng';


-- ==========================================================
-- BẢNG LIÊN KẾT ĐẶT BÀN VÀ BÀN ĂN (RESERVATION_TABLES)
-- ==========================================================
CREATE TABLE reservation_tables (
    reservation_table_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT UNSIGNED NOT NULL COMMENT 'FK tới reservations',
    table_id INT UNSIGNED NOT NULL COMMENT 'FK tới dining_tables',
    start_time DATETIME NOT NULL COMMENT 'Thời gian bắt đầu giữ bàn',
    end_time DATETIME NOT NULL COMMENT 'Thời gian kết thúc giữ bàn',
    guests_at_table SMALLINT UNSIGNED DEFAULT NULL COMMENT 'Số khách ngồi bàn này',
    reservation_table_status ENUM('reserved','occupied','finished','cancelled') DEFAULT 'reserved' COMMENT 'Trạng thái bàn trong đặt bàn',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id) ON DELETE CASCADE,
    FOREIGN KEY (table_id) REFERENCES dining_tables(table_id) ON DELETE RESTRICT,
    INDEX (table_id),
    INDEX (reservation_id),
    INDEX (start_time),
    INDEX (end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Liên kết giữa đặt bàn và bàn thực tế';


-- ==========================================================
-- DANH MỤC MÓN ĂN (CATEGORIES)
-- ==========================================================
CREATE TABLE categories (
    category_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Tên danh mục món ăn',
    description TEXT COMMENT 'Mô tả danh mục',
    image_url VARCHAR(255) COMMENT 'Ảnh của món ăn',
    active BOOLEAN DEFAULT TRUE COMMENT 'Còn hoạt động không'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh mục món ăn';


-- ==========================================================
-- DỊ ỨNG (ALLERGENS)
-- ==========================================================
CREATE TABLE allergens (
    allergen_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Tên dị ứng (ví dụ: đậu phộng)',
    description TEXT COMMENT 'Mô tả chi tiết'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Dị ứng thực phẩm';


-- ==========================================================
-- MÓN ĂN (DISHES)
-- ==========================================================
CREATE TABLE dishes (
    dish_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_id INT UNSIGNED NULL COMMENT 'Danh mục món ăn (FK)',
    image_url VARCHAR(255) COMMENT 'Ảnh của món ăn',
    name VARCHAR(200) NOT NULL COMMENT 'Tên món ăn',
    description TEXT COMMENT 'Mô tả chi tiết món',
    price DECIMAL(10,2) NOT NULL COMMENT 'Giá bán',
    preparation_time SMALLINT UNSIGNED DEFAULT NULL COMMENT 'Thời gian chuẩn bị (phút)',
    is_vegetarian BOOLEAN DEFAULT FALSE COMMENT 'Món chay',
    is_vegan BOOLEAN DEFAULT FALSE COMMENT 'Thuần chay',
    is_spicy BOOLEAN DEFAULT FALSE COMMENT 'Món cay',
    active BOOLEAN DEFAULT TRUE COMMENT 'Trạng thái món',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    INDEX (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh sách món ăn';


-- ==========================================================
-- LIÊN KẾT MÓN VÀ DỊ ỨNG (DISH_ALLERGENS)
-- ==========================================================
CREATE TABLE dish_allergens (
    dish_id INT UNSIGNED NOT NULL,
    allergen_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (dish_id, allergen_id),
    FOREIGN KEY (dish_id) REFERENCES dishes(dish_id) ON DELETE CASCADE,
    FOREIGN KEY (allergen_id) REFERENCES allergens(allergen_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Liên kết món ăn và dị ứng';


-- ==========================================================
-- NGUYÊN LIỆU (INGREDIENTS) - ĐÃ SỬA
-- ==========================================================
CREATE TABLE ingredients (
    ingredient_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE COMMENT 'Tên nguyên liệu',
    description TEXT COMMENT 'Mô tả nguyên liệu' -- <-- ĐÃ XÓA DẤU PHẨY THỪA
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Nguyên liệu nấu ăn';


-- ==========================================================
-- MÓN - NGUYÊN LIỆU (DISH_INGREDIENTS)
-- ==========================================================
CREATE TABLE dish_ingredients (
    dish_id INT UNSIGNED NOT NULL,
    ingredient_id INT UNSIGNED NOT NULL,
    quantity VARCHAR(60) COMMENT 'Định lượng',
    PRIMARY KEY (dish_id, ingredient_id),
    FOREIGN KEY (dish_id) REFERENCES dishes(dish_id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Liên kết món ăn và nguyên liệu';


-- ==========================================================
-- ĐƠN HÀNG (ORDERS)
-- ==========================================================
CREATE TABLE orders (
    order_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    reservation_table_id INT UNSIGNED NOT NULL COMMENT 'FK tới reservation_tables',
    user_id INT UNSIGNED NULL COMMENT 'Người tạo đơn (có thể là khách hoặc nhân viên)',
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian đặt hàng',
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT 'Tổng tiền',
    status ENUM('pending','confirmed','preparing','ready','served','cancelled') DEFAULT 'pending' COMMENT 'Trạng thái đơn hàng',
    payment_status ENUM('unpaid','paid','refunded') DEFAULT 'unpaid' COMMENT 'Trạng thái thanh toán',
    payment_method VARCHAR(50) COMMENT 'Phương thức thanh toán',
    notes TEXT COMMENT 'Ghi chú đơn hàng',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_table_id) REFERENCES reservation_tables(reservation_table_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX (reservation_table_id),
    INDEX (user_id),
    INDEX (order_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Đơn hàng gắn với bàn ăn';


-- ==========================================================
-- CHI TIẾT ĐƠN HÀNG (ORDER_ITEMS)
-- ==========================================================
CREATE TABLE order_items (
    order_item_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id INT UNSIGNED NOT NULL,
    dish_id INT UNSIGNED NULL,
    quantity SMALLINT UNSIGNED NOT NULL DEFAULT 1 COMMENT 'Số lượng món',
    unit_price DECIMAL(10,2) NOT NULL COMMENT 'Đơn giá',
    subtotal DECIMAL(12,2) GENERATED ALWAYS AS (quantity * unit_price) STORED COMMENT 'Thành tiền',
    special_requests TEXT COMMENT 'Yêu cầu đặc biệt',
    status ENUM('pending','preparing','ready','served','cancelled') DEFAULT 'pending' COMMENT 'Trạng thái món',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (dish_id) REFERENCES dishes(dish_id) ON DELETE SET NULL,
    INDEX (order_id),
    INDEX (dish_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Chi tiết món trong đơn hàng';


-- ==========================================================
-- HÓA ĐƠN (INVOICES)
-- ==========================================================
CREATE TABLE invoices (
    invoice_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id INT UNSIGNED NOT NULL COMMENT 'FK tới orders',
    user_id INT UNSIGNED NULL COMMENT 'Người tạo hóa đơn (nhân viên)',
    total_amount DECIMAL(12,2) NOT NULL COMMENT 'Tổng tiền hóa đơn',
    discount DECIMAL(12,2) DEFAULT 0 COMMENT 'Giảm giá',
    final_amount DECIMAL(12,2) GENERATED ALWAYS AS (total_amount - discount) STORED COMMENT 'Thành tiền cuối cùng',
    payment_method VARCHAR(50) NOT NULL COMMENT 'Phương thức thanh toán',
    status ENUM('pending','paid','cancelled','refunded') DEFAULT 'pending' COMMENT 'Trạng thái hóa đơn',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Hóa đơn thanh toán';


-- ==========================================================
-- THÔNG BÁO (NOTIFICATIONS)
-- ==========================================================
CREATE TABLE notifications (
    notification_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT 'Tiêu đề thông báo',
    message TEXT NOT NULL COMMENT 'Nội dung thông báo',
    type ENUM('system','order','promotion','reservation') DEFAULT 'system' COMMENT 'Loại thông báo',
    priority ENUM('low','medium','high') DEFAULT 'medium' COMMENT 'Mức độ ưu tiên',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông báo hệ thống';


-- ==========================================================
-- THÔNG BÁO NGƯỜI DÙNG (USER_NOTIFICATIONS)
-- ==========================================================
CREATE TABLE user_notifications (
    user_notification_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL COMMENT 'Người nhận thông báo',
    notification_id INT UNSIGNED NOT NULL COMMENT 'Thông báo nào',
    is_read BOOLEAN DEFAULT FALSE COMMENT 'Đã đọc chưa',
    read_at TIMESTAMP NULL COMMENT 'Thời điểm đọc',
    delivered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm gửi',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (notification_id) REFERENCES notifications(notification_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông báo gửi cho người dùng';


-- ==========================================================
-- PHÂN QUYỀN (ROLES, PERMISSIONS)
-- ==========================================================
CREATE TABLE roles (
    role_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Tên vai trò (admin, staff, ...)',
    description VARCHAR(255) COMMENT 'Mô tả vai trò'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Vai trò người dùng';

CREATE TABLE permissions (
    permission_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    permission_name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Tên quyền (vd: view_orders)',
    description VARCHAR(255) COMMENT 'Mô tả quyền'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Các quyền trong hệ thống';

CREATE TABLE role_permission (
    role_id INT UNSIGNED NOT NULL,
    permission_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Liên kết vai trò và quyền';

CREATE TABLE user_role (
    user_id INT UNSIGNED NOT NULL,
    role_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Liên kết người dùng và vai trò';


-- ==========================================================
-- ⚙️ THÊM DỮ LIỆU MẪU CHO HỆ THỐNG MOBILE_POS
-- ==========================================================
USE mobile_pos;

SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================================
-- USERS (Người dùng) - ĐÃ SỬA
-- ==========================================================
-- (Đã xóa cột 'role' ra khỏi danh sách chèn cho khớp với CREATE TABLE)
INSERT INTO users (email, password_hash, full_name, phone, gender, dob, status)
VALUES
('customer1@gmail.com', 'hash1', 'Nguyễn Văn A', '0901111111', 'male', '1990-01-10', 'active'),
('customer2@gmail.com', 'hash2', 'Trần Thị B', '0902222222', 'female', '1993-02-12', 'active'),
('staff1@gmail.com', 'hash3', 'Lê Văn C', '0903333333', 'male', '1995-03-05', 'active'),
('staff2@gmail.com', 'hash4', 'Phạm Thị D', '0904444444', 'female', '1998-04-22', 'active'),
('manager1@gmail.com', 'hash5', 'Đỗ Văn E', '0905555555', 'male', '1987-05-18', 'active'),
('admin@gmail.com', 'hash6', 'Super Admin', '090666666OS', 'other', '1980-06-06', 'active'),
('customer3@gmail.com', 'hash7', 'Bùi Thị F', '0907777777', 'female', '1992-07-07', 'inactive'),
('customer4@gmail.com', 'hash8', 'Hoàng Văn G', '0908888888', 'male', '1991-08-08', 'active'),
('customer5@gmail.com', 'hash9', 'Phan Thị H', '0909999999', 'female', '1994-09-09', 'active'),
('staff3@gmail.com', 'hash10', 'Nguyễn Văn I', '0910000000', 'male', '1996-10-10', 'active');

-- ==========================================================
-- DINING_TABLES (Bàn ăn)
-- ==========================================================
INSERT INTO dining_tables (table_code, seating_capacity, area, status)
VALUES
('A1', 4, 'Khu A', 'available'),
('A2', 4, 'Khu A', 'available'),
('A3', 6, 'Khu A', 'occupied'),
('B1', 4, 'Khu B', 'reserved'),
('B2', 6, 'Khu B', 'available'),
('C1', 8, 'Khu C', 'outofservice'),
('C2', 4, 'Khu C', 'available'),
('D1', 10, 'VIP', 'available'),
('D2', 8, 'VIP', 'reserved'),
('E1', 2, 'Sân vườn', 'available');
-- ==========================================================
-- RESERVATIONS (Đặt bàn)
-- ==========================================================
INSERT INTO reservations (user_id, booking_code, contact_name, contact_phone, contact_email, total_guests, reservation_status)
VALUES
(1, 'RES001', 'Nguyễn Văn A', '0901111111', 'customer1@gmail.com', 4, 'booked'),
(2, 'RES002', 'Trần Thị B', '0902222222', 'customer2@gmail.com', 2, 'completed'),
(1, 'RES003', 'Nguyễn Văn A', '0901111111', 'customer1@gmail.com', 6, 'cancelled'),
(3, 'RES004', 'Lê Văn C', '0903333333', 'staff1@gmail.com', 5, 'checked-in'),
(4, 'RES005', 'Phạm Thị D', '0904444444', 'staff2@gmail.com', 3, 'booked'),
(8, 'RES006', 'Hoàng Văn G', '0908888888', 'customer4@gmail.com', 4, 'booked'),
(9, 'RES007', 'Phan Thị H', '0909999999', 'customer5@gmail.com', 8, 'booked'),
(2, 'RES008', 'Trần Thị B', '0902222222', 'customer2@gmail.com', 10, 'completed'),
(5, 'RES009', 'Đỗ Văn E', '0905555555', 'manager1@gmail.com', 4, 'booked'),
(7, 'RES010', 'Bùi Thị F', '0907777777', 'customer3@gmail.com', 6, 'cancelled');
-- ==========================================================
-- RESERVATION_TABLES (Liên kết đặt bàn & bàn)
-- ==========================================================
INSERT INTO reservation_tables (reservation_id, table_id, start_time, end_time, guests_at_table)
VALUES
(1, 1, '2025-10-25 18:00', '2025-10-25 20:00', 4),
(2, 2, '2025-10-24 19:00', '2025-10-24 21:00', 2),
(3, 3, '2025-10-20 12:00', '2025-10-20 14:00', 6),
(4, 4, '2025-10-26 11:30', '2025-10-26 13:00', 5),
(5, 5, '2025-10-27 18:00', '2025-10-27 19:30', 3),
(6, 6, '2025-10-25 12:00', '2025-10-25 13:00', 4),
(7, 7, '2025-10-28 19:00', '2025-10-28 21:00', 8),
(8, 8, '2025-10-29 18:30', '2025-10-29 20:30', 10),
(9, 9, '2025-10-25 20:00', '2025-10-25 22:00', 4),
(10, 10, '2025-10-23 12:00', '2025-10-23 14:00', 6);

-- ==========================================================
-- CATEGORIES (Danh mục món ăn)
-- ==========================================================
INSERT INTO categories (name, description, image_url)
VALUES
('Món khai vị', 'Các món ăn nhẹ dùng trước bữa chính', 'images/appetizers.jpg'),
('Món chính', 'Các món ăn chính trong bữa ăn', 'images/main_courses.jpg'),
('Món tráng miệng', 'Các món ngọt dùng sau bữa ăn', 'images/desserts.jpg'),
('Đồ uống', 'Các loại nước giải khát, sinh tố, cà phê', 'images/drinks.jpg');

-- ==========================================================
-- ALLERGENS (Dị ứng)
-- ==========================================================
INSERT INTO allergens (name, description)
VALUES
('Đậu phộng', 'Có chứa lạc, gây dị ứng'),
('Sữa', 'Chứa lactose'),
('Trứng', 'Có thể gây dị ứng'),
('Hải sản', 'Dị ứng với tôm, cua...'),
('Đậu nành', 'Có chứa protein đậu nành'),
('Lúa mì', 'Có gluten'),
('Mè', 'Có thể gây dị ứng nhẹ'),
('Hạnh nhân', 'Gây dị ứng hạt');

-- ==========================================================
-- DISHES (Món ăn)
-- ==========================================================
INSERT INTO dishes (category_id, image_url, name, description, price, preparation_time, is_vegetarian, is_vegan, is_spicy)
VALUES
(1, '/images/goi_cuon.jpg', 'Gỏi cuốn tôm thịt', 'Khai vị truyền thống, ăn kèm nước chấm.', 35000, 15, FALSE, FALSE, FALSE),
(2, '/images/com_chien.jpg', 'Cơm chiên hải sản', 'Món chính phổ biến, hạt cơm tơi xốp, thơm ngon.', 65000, 20, FALSE, FALSE, FALSE),
(2, '/images/bo_luc_lac.jpg', 'Bò lúc lắc', 'Bò lúc lắc khoai tây chiên, thịt bò mềm.', 85000, 25, FALSE, FALSE, FALSE),
(4, '/images/nuoc_cam.jpg', 'Nước cam ép', 'Cam tươi nguyên chất 100%.', 25000, 5, TRUE, TRUE, FALSE),
(3, '/images/che_ba_mau.jpg', 'Chè ba màu', 'Món tráng miệng mát lạnh từ đậu, cốt dừa.', 30000, 10, TRUE, TRUE, FALSE),
(1, '/images/salad_ca_ngu.jpg', 'Salad cá ngừ', 'Salad cá ngừ và rau củ tươi, sốt mayonaise.', 45000, 15, FALSE, FALSE, FALSE),
(2, '/images/lau_thai.jpg', 'Lẩu thái', 'Lẩu chua cay kiểu Thái, phục vụ cho 2-3 người.', 150000, 30, FALSE, FALSE, TRUE),
(2, '/images/ga_nuong.jpg', 'Gà nướng muối ớt', 'Gà nướng da giòn, ướp muối ớt cay nồng.', 120000, 45, FALSE, FALSE, TRUE),
(1, '/images/tom_hap_bia.jpg', 'Tôm hấp bia', 'Hải sản tươi sống, tôm hấp với bia và sả.', 95000, 20, FALSE, FALSE, FALSE),
(3, '/images/dau_hu.jpg', 'Đậu hũ chiên giòn', 'Đậu hũ chiên giòn, ăn kèm nước tương ớt.', 40000, 15, TRUE, TRUE, FALSE),
(3, '/images/banh_flan.jpg', 'Bánh Flan Trứng', 'Món tráng miệng truyền thống từ trứng và sữa.', 25000, 20, TRUE, FALSE, FALSE);

-- ==========================================================
-- INGREDIENTS (Nguyên liệu)
-- ==========================================================
INSERT INTO ingredients (ingredient_id, name, description)
VALUES
(1, 'Bún/Phở', 'Nguyên liệu chính cho món cuốn/phở'),
(2, 'Tôm', 'Tôm biển tươi'),
(3, 'Thịt ba chỉ', 'Thịt heo ba chỉ, dùng cho nhiều món'),
(4, 'Cơm trắng', 'Gạo trắng đã nấu chín'),
(5, 'Thịt bò', 'Thịt bò tươi'),
(6, 'Khoai tây', 'Củ khoai tây, thường dùng chiên/xào'),
(7, 'Trứng gà', 'Nguyên liệu món ăn'),
(8, 'Bột mì', 'Nguyên liệu làm bánh/bột'),
(9, 'Nước cốt dừa', 'Dùng làm nước sốt, chè'),
(10, 'Đậu hũ', 'Đậu hũ tươi'),
(11, 'Rau xà lách', 'Dùng cho salad'),
(12, 'Sốt Mayonnaise', 'Sốt kem trứng và dầu ăn'),
(13, 'Nước dùng gà/xương', 'Nước dùng từ xương gà hoặc xương heo');

INSERT INTO dish_allergens (dish_id, allergen_id)
VALUES
(1, 4), (1, 5), (1, 1),
(2, 4), (2, 3),
(3, 5),
(5, 5), (5, 2),
(6, 4), (6, 3),
(7, 4), (7, 5),
(9, 4),
(10, 5),
(11, 3), (11, 2);

INSERT INTO dish_ingredients (dish_id, ingredient_id, quantity)
VALUES
(1, 2, '50g'), (1, 3, '50g'), (1, 1, '100g'), (1, 11, '3 lá'),
(2, 4, '200g'), (2, 2, '50g'), (2, 3, '50g'), (2, 7, '1 quả'),
(3, 5, '150g'), (3, 6, '150g'),
(5, 9, '100ml'), (5, 5, '50g'),
(6, 11, '150g'), (6, 12, '50g'),
(7, 13, '1.5L'), (7, 2, '100g'), (7, 4, '500g'),
(8, 13, '500g'),
(9, 2, '300g'),
(10, 10, '200g'),
(11, 7, '2 quả'), (11, 2, '100ml');

-- ==========================================================
-- ORDERS (Đơn hàng)
-- ==========================================================
INSERT INTO orders (reservation_table_id, user_id, total_amount, status, payment_status, payment_method)
VALUES
(1, 1, 150000, 'served', 'paid', 'cash'),
(2, 2, 120000, 'served', 'paid', 'card'),
(3, 3, 80000, 'preparing', 'unpaid', 'cash'),
(4, 4, 95000, 'confirmed', 'unpaid', 'cash'),
(5, 5, 60000, 'pending', 'unpaid', 'cash'),
(6, 6, 130000, 'served', 'paid', 'card'),
(7, 7, 175000, 'served', 'paid', 'cash'),
(8, 8, 210000, 'served', 'paid', 'transfer'),
(9, 9, 145000, 'preparing', 'unpaid', 'cash'),
(10, 10, 95000, 'served', 'paid', 'cash');
-- ==========================================================
-- ORDER_ITEMS (Chi tiết đơn hàng)
-- ==========================================================
INSERT INTO order_items (order_id, dish_id, quantity, unit_price)
VALUES
(1, 1, 2, 35000),
(1, 2, 1, 65000),
(2, 4, 2, 25000),
(3, 3, 1, 85000),
(4, 5, 2, 30000),
(5, 9, 1, 95000),
(6, 7, 1, 150000),
(7, 6, 2, 45000),
(8, 8, 1, 95000),
(9, 10, 3, 40000);

-- ==========================================================
-- NOTIFICATIONS & USER_NOTIFICATIONS
-- ==========================================================
INSERT INTO notifications (title, message, type)
VALUES
('Khuyến mãi 10%', 'Giảm 10% cho đơn trên 500k', 'promotion'),
('Đặt bàn thành công', 'Cảm ơn bạn đã đặt bàn tại nhà hàng', 'reservation'),
('Đơn hàng đã sẵn sàng', 'Món của bạn đã sẵn sàng', 'order'),
('Lẩu mới ra mắt', 'Thưởng thức lẩu Thái cực ngon', 'promotion'),
('Hệ thống bảo trì', 'Nhà hàng sẽ bảo trì lúc 2h sáng', 'system'),
('Cảm ơn bạn', 'Hãy đánh giá trải nghiệm của bạn', 'system'),
('Món mới', 'Thêm món Gà nướng muối ớt cực ngon', 'promotion'),
('Đặt bàn bị hủy', 'Đặt bàn của bạn đã bị hủy', 'reservation'),
('Thanh toán thành công', 'Cảm ơn bạn đã thanh toán', 'order'),
('Voucher 20%', 'Nhập mã SAVE20 để giảm giá', 'promotion');

INSERT INTO user_notifications (user_id, notification_id, is_read)
VALUES
(1, 1, FALSE),
(2, 2, TRUE),
(3, 3, FALSE),
(4, 4, TRUE),
(5, 5, FALSE),
(6, 6, FALSE),
(7, 7, TRUE),
(8, 8, FALSE),
(9, 9, TRUE),
(10, 10, FALSE);

-- ==========================================================
-- ROLES, PERMISSIONS, USER_ROLE, ROLE_PERMISSION - ĐÃ SỬA
-- ==========================================================
-- (Đổi tên 'role_name' thành chữ hoa để khớp với code Java, ví dụ: "USER")
INSERT INTO roles (role_name, description)
VALUES
('ADMIN', 'Quản trị viên'),
('MANAGER', 'Quản lý'),
('STAFF', 'Nhân viên phục vụ'),
('USER', 'Khách hàng'),
('CHEF', 'Đầu bếp'),
('CASHIER', 'Thu ngân'),
('SUPERVISOR', 'Giám sát'),
('GUEST', 'Khách vãng lai'),
('SUPPORT', 'Hỗ trợ kỹ thuật'),
('DEVELOPER', 'Nhà phát triển');

INSERT INTO permissions (permission_name, description)
VALUES
('view_orders', 'Xem danh sách đơn hàng'),
('edit_orders', 'Chỉnh sửa đơn hàng'),
('delete_orders', 'Xóa đơn hàng'),
('manage_users', 'Quản lý người dùng'),
('manage_roles', 'Phân quyền'),
('view_reports', 'Xem báo cáo'),
('manage_menu', 'Quản lý món ăn'),
('manage_tables', 'Quản lý bàn ăn'),
('process_payment', 'Xử lý thanh toán'),
('view_dashboard', 'Xem bảng điều khiển');

-- Liên kết vai trò & quyền (mẫu) - ĐÃ SỬA LỖI GÕ SAI
INSERT INTO role_permission (role_id, permission_id)
VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10), -- Sửa 1M -> 1
(2,1),(2,6),(2,7),(2,8),
(3,1),(3,2),(3,9),
(4,1),(4,10),
(5,7),(5,8),
(6,9),(6,10),
(7,6),(7,8),
(8,10),(9,6),(10,10);

-- Gán vai trò cho user
INSERT INTO user_role (user_id, role_id)
VALUES
(1,4),(2,4),(3,3),(4,3),(5,2),(6,1),(7,4),(8,4),(9,4),(10,3);

SET FOREIGN_KEY_CHECKS = 1;