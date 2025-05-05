package com.sofrecom.tickit.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // OpenAPI spec version
                .openapi("3.0.1")
                // API meta-info
                .info(new Info()
                        .title("TickIt")
                        .description("Ticketing System API\n\n" +
                                     "Development Team:\n" +
                                     "- Talel Turki (talel@example.com)\n" +
                                     "- Yassine Krichen (yassine.krichen@sofrecom.com)")
                        .version("0.1")
                        .contact(new Contact()
                            .url("https://github.com/yassine-krichen/tickit-ticketing-backend"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                // Declare the security scheme under Components
                .components(new Components()
                    .addSecuritySchemes(SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .in(SecurityScheme.In.HEADER)
                            .name("Authorization")
                    )
                )
                // Apply this scheme globally to all operations
                .addSecurityItem(new SecurityRequirement()
                    .addList(SECURITY_SCHEME_NAME)
                );
    }
}
