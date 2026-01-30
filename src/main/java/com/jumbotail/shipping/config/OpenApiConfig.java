package com.jumbotail.shipping.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) configuration for API documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI shippingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shipping Charge Estimator API")
                        .description(
                                "Production-grade API for calculating shipping charges in a B2B e-commerce marketplace. "
                                        +
                                        "Designed for Kirana stores to estimate shipping costs based on distance, " +
                                        "transport mode, and delivery speed.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Jumbotail Engineering")
                                .email("engineering@jumbotail.com")
                                .url("https://www.jumbotail.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://www.jumbotail.com/terms")))
                .externalDocs(new ExternalDocumentation()
                        .description("API Documentation")
                        .url("https://docs.jumbotail.com/shipping-api"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.jumbotail.com")
                                .description("Production Server")));
    }
}
