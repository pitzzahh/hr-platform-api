package dev.araopj.hrplatformapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "HR Platform API",
                version = "0.0.1-SNAPSHOT",
                description = "A SpringBoot RESTful API for managing HR data, including employee CRUD, salary tracking, audit logging, and role-based authentication. Features robust exception handling, pagination, and modular structure. See README or controller classes for endpoint details.",
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                ),
                contact = @Contact(
                        name = "Peter John Arao",
                        email = "contact@araopj.dev",
                        url = "https://github.com/pitzzahh"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local server"
                )
        }
)

public class OpenApiConfig {
}
