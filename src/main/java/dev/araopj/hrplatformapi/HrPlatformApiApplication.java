package dev.araopj.hrplatformapi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HrPlatformApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrPlatformApiApplication.class, args);
    }

    @Bean
    public Dotenv dotenv() {
        return Dotenv.load();
    }
}
