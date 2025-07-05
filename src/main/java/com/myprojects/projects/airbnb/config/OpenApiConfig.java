package com.myprojects.projects.airbnb.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Hotel Booking System API",
                version = "1.0.0 (2025-07-05)",
                description = "Comprehensive RESTful APIs to manage hotels, bookings, user authentication, and payments for a hotel booking platform.",
                contact = @Contact(
                        name = "Pranjal Sharma",
                        email = "pranjalsharma243@gmail.com",
                        url = "https://www.linkedin.com/in/pranjal-sharma-1bb67b170/"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Project Documentation",
                url = "https://github.com/pranjalsharma243/AirBnbClone"
        ),
        security = {
                @SecurityRequirement(name = "jwtScheme")
        }
)
@SecurityScheme(
        name = "jwtScheme",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\""
)
public class OpenApiConfig {

}
