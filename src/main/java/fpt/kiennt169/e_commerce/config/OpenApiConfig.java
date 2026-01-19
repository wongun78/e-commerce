package fpt.kiennt169.e_commerce.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";
    private static final String API_TITLE = "E-Commerce REST API";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = """
            E-Commerce platform REST API with JWT authentication.
            
            Features:
            - User authentication & authorization
            - Product management
            - Order processing
            - Role-based access control (ADMIN, CUSTOMER)
            
            Use /api/v1/auth/login to obtain JWT token.
            """;

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title(API_TITLE)
                .version(API_VERSION)
                .description(API_DESCRIPTION)
                .contact(new Contact()
                        .name("Developer")
                        .email("kiennt169@gmail.com"));
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .description("JWT token in format: Bearer {token}");
    }
}
