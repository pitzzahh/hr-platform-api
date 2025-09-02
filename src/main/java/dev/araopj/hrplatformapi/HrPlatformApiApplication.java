package dev.araopj.hrplatformapi;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HrPlatformApiApplication {

    public static void main(String[] args) {
        Dotenv.configure().directory("./").load().entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        SpringApplication.run(HrPlatformApiApplication.class, args);
    }

    @Bean
    public Hibernate6Module hibernate6Module() {
        return new Hibernate6Module();
    }

}
