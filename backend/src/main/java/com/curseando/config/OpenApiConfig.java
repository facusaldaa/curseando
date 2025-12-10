package com.curseando.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI curseandoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Curseando API")
                        .description("API documentation for Curseando course platform")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Curseando Team")
                                .email("support@curseando.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Server"),
                        new Server().url("http://backend:8080").description("Docker Server")));
    }
}
