package com.myprojects.projects.airbnb.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info= @Info(
                title = "Hotel Booking System API",
                description = "All Hotel Booking System API endpoints",
                version = "1.0",
                contact = @Contact(
                        name="Pranjal Sharma",
                        email = "pranjalsharma243@gmail.com"
                )
        ),
        security = @SecurityRequirement(
                name = "jwtScheme"
        )
)
@SecurityScheme(
        name = "jwtScheme",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

}