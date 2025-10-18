# mobile-be
Backend for Mobile App (Spring Boot)
## Các công nghệ sử dụng

- **Framework:** [Spring Boot](https://spring.io/projects/spring-boot)
- **Ngôn ngữ:** [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) (hoặc phiên bản bạn dùng)

- **Truy cập dữ liệu (Data Access):**
  - [Spring Data JPA](https://spring.io/projects/spring-data-jpa): Đơn giản hóa việc truy vấn cơ sở dữ liệu.
  - [MySQL Connector/J](https://dev.mysql.com/doc/connector-j/en/): Trình điều khiển (JDBC Driver) kết nối database MySQL.

- **Bảo mật (Security):**
  - [Spring Security](https://spring.io/projects/spring-security): Xử lý xác thực và phân quyền cho các API.
  - [Spring OAuth2 Client](https://docs.spring.io/spring-security/reference/servlet/oauth2/client/index.html): Hỗ trợ đăng nhập qua các nhà cung cấp bên thứ ba (ví dụ: Google, GitHub).

- **Validation:**
  - [Spring Boot Validation](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io.validation): Dùng để kiểm tra tính hợp lệ của dữ liệu đầu vào (ví dụ: DTOs).

- **Build Tool:**
  - [Maven](https://maven.apache.org/) (hoặc Gradle)

- **Tiện ích (Utilities):**
  - [Lombok](https://projectlombok.org/): Giúp giảm thiểu code soạn sẵn (boilerplate code) như getters, setters, constructors.
