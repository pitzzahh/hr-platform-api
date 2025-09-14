package dev.araopj.hrplatformapi;

import dev.araopj.hrplatformapi.employee.service.SalaryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@DisplayName("HR Platform API Application Tests")
class HrPlatformApiApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private Environment environment;

    @Test
    @DisplayName("Application context loads successfully")
    void contextLoads() {
        assertNotNull(salaryService, "SalaryService bean should be loaded");
    }

    @Test
    @DisplayName("SalaryService bean is of correct type")
    void testSalaryServiceBean() {
        assertInstanceOf(SalaryService.class, salaryService, "SalaryService should be instance of SalaryServiceImp");
    }

    @Test
    @DisplayName("Health endpoint returns UP status")
    void testHealthEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"status\":\"UP\""), "Health endpoint should return UP status");
    }

    @Test
    @DisplayName("Application properties are loaded correctly")
    void testApplicationProperties() {
        String datasourceUrl = environment.getProperty("spring.datasource.url");
        assertNotNull(datasourceUrl, "Datasource URL should be configured");
        assertTrue(datasourceUrl.contains("h2"), "Datasource should be H2 for test profile");
    }

    @Test
    @DisplayName("JPA properties are loaded correctly")
    void testJpaProperties() {
        String ddlAuto = environment.getProperty("spring.jpa.hibernate.ddl-auto");
        assertEquals("create-drop", ddlAuto, "JPA ddl-auto should be create-drop for dev profile");
    }

    @Test
    @DisplayName("REST endpoint for salary grades returns expected response")
    void testSalaryEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/salaries", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Salary endpoint should return 200 OK");
    }
}