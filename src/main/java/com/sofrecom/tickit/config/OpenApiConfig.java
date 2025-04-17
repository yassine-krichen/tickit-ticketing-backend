package com.sofrecom.tickit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
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
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}