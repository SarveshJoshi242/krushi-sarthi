package com.krushiadhaar.config;

import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    @PostConstruct
    public void init() {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null && (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://"))) {
            try {
                String cleanUrl = databaseUrl;
                if (cleanUrl.startsWith("postgres://")) {
                    cleanUrl = "postgresql://" + cleanUrl.substring("postgres://".length());
                }
                URI dbUri = new URI(cleanUrl);
                String userInfo = dbUri.getUserInfo();
                String dbUrl;
                if (dbUri.getPort() != -1) {
                    dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath();
                } else {
                    dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":5432" + dbUri.getPath();
                }

                System.setProperty("spring.datasource.url", dbUrl);
                System.setProperty("spring.flyway.url", dbUrl);
                
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    System.setProperty("spring.datasource.username", parts[0]);
                    System.setProperty("spring.datasource.password", parts[1]);
                    System.setProperty("spring.flyway.user", parts[0]);
                    System.setProperty("spring.flyway.password", parts[1]);
                }
            } catch (Exception e) {
                System.err.println("DatabaseConfig: Error parsing DATABASE_URL: " + e.getMessage());
            }
        }
    }
}
