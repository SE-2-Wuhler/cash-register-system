package de.se.cashregistersystem.util;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cashRegisterOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cash Register System API")
                        .description("API documentation for the Cash Register System")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("team@example.com")));
    }
}