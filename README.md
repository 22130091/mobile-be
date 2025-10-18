# mobile-be (Backend for ReserveAndDine Mobile App)

Backend for the mobile app (Spring Boot) handling user-facing features like booking, viewing menus, and user profiles.

## Mục lục

-   [Tính năng chính](#tính-năng-chính)
-   [Các công nghệ sử dụng](#các-công-nghệ-sử-dụng)
-   [Cài đặt và Chạy dự án](#cài-đặt-và-chạy-dự-án)
    -   [Yêu cầu](#yêu-cầu)
    -   [Cài đặt](#cài-đặt)
    -   [Cấu hình](#cấu-hình)
    -   [Chạy ứng dụng](#chạy-ứng-dụng)
-   [Tài liệu API](#tài-liệu-api-endpoints)

---

## Tính năng chính


-   **Xác thực:** Đăng ký, đăng nhập tài khoản người dùng (bao gồm cả đăng nhập qua Google).
-   **Đặt bàn:** Cho phép người dùng tìm kiếm nhà hàng và đặt bàn trước.
-   **Quản lý Đặt bàn:** Người dùng có thể xem lại lịch sử hoặc hủy các đơn đặt bàn của mình.
-   **Xem và chọn Thực đơn:** (Tùy chọn) Cho phép người dùng xem trước và chọn món ăn.
-   **Quản lý Hồ sơ:** Người dùng có thể cập nhật thông tin cá nhân.

---

## Các công nghệ sử dụng

-   **Framework:** [Spring Boot](https://spring.io/projects/spring-boot)
-   **Ngôn ngữ:** [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

-   **Truy cập dữ liệu (Data Access):**
    -   [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
    -   [MySQL Connector/J](https://dev.mysql.com/doc/connector-j/en/): Trình điều khiển (JDBC Driver) kết nối database MySQL.

-   **Bảo mật (Security):**
    -   [Spring Security](https://spring.io/projects/spring-security)
    -   [Spring OAuth2 Client](https://docs.spring.io/spring-security/reference/servlet/oauth2/client/index.html): Hỗ trợ đăng nhập qua các nhà cung cấp bên thứ ba (Google).

-   **Validation:**
    -   [Spring Boot Validation](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io.validation)

-   **Build Tool:**
    -   [Maven](https://maven.apache.org/)

-   **Tiện ích (Utilities):**
    -   [Lombok](https://projectlombok.org/)

---

## Cài đặt và Chạy dự án

### Yêu cầu

Dưới đây là các phần mềm bạn cần cài đặt trước khi chạy dự án:

-   [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
-   [Maven 3.x+](https://maven.apache.org/download.cgi)
-   [MySQL Server](https://www.mysql.com/downloads/)

---

### Cấu hình
    ```properties
    # Cổng chạy của DỊCH VỤ BE (cho Người dùng)
    server.port=9090

    # Cấu hình kết nối Database MySQL
    spring.datasource.url=jdbc:mysql://localhost:3306/web_db
    spring.datasource.username=root
    spring.datasource.password=root
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

    # Cấu hình JPA/Hibernate
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true
    ```

---

### Chạy ứng dụng

Sau khi đã cấu hình xong, bạn có thể chạy dự án bằng lệnh:

```bash
./mvnw spring-boot:run
