package com.client.mobile;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@SpringBootApplication
public class ClientBeMobileApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientBeMobileApplication.class, args);
	}
	@Bean
	CommandLineRunner printDbInfo(DataSource dataSource) {
		return args -> {
			try (Connection conn = dataSource.getConnection()) {
				DatabaseMetaData meta = conn.getMetaData();
				System.out.println(">>> Connected to: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
				System.out.println(">>> Driver: " + meta.getDriverName() + " " + meta.getDriverVersion());
				System.out.println(">>> URL: " + meta.getURL());
				System.out.println(">>> Autocommit: " + conn.getAutoCommit());
			}
		};
	}
}
