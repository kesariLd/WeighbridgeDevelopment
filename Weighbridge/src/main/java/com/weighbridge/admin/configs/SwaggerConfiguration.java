package com.weighbridge.admin.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI myOpenAPI() {
        Contact contact = new Contact();
        contact.setEmail("hr@ldtech.in");
        contact.setName("Live Digital Technologies Pvt Ltd.");
        contact.setUrl("https://ldtech.in/");

        Info info = new Info()
                .title("Weighbridge")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage Weighbridge application.");

        return new OpenAPI().info(info);
    }
}
