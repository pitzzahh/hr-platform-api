package dev.araopj.hrplatformapi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HrPlatformApiApplication {

    public static void main(String[] args) {
        Dotenv.configure().directory("./").load().entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        SpringApplication.run(HrPlatformApiApplication.class, args);
    }

}
