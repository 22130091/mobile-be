# mobile-be
Backend for Mobile App (Spring Boot)
## Các công nghệ sử dụng

- **Framework:** [Spring Boot](https://spring.io/projects/spring-boot)
- **Ngôn ngữ:** [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

- **Truy cập dữ liệu (Data Access):**
  - [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
  - [MySQL Connector/J](https://dev.mysql.com/doc/connector-j/en/): Trình điều khiển (JDBC Driver) kết nối database MySQL.

- **Bảo mật (Security):**
  - [Spring Security](https://spring.io/projects/spring-security)
  - [Spring OAuth2 Client](https://docs.spring.io/spring-security/reference/servlet/oauth2/client/index.html): Hỗ trợ đăng nhập qua các nhà cung cấp bên thứ ba (Google).

- **Validation:**
  - [Spring Boot Validation](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io.validation)

- **Build Tool:**
  - [Maven](https://maven.apache.org/) 

- **Tiện ích (Utilities):**
  - [Lombok](https://projectlombok.org/)
 
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
