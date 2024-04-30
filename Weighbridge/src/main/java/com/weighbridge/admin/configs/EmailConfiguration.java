package com.weighbridge.admin.configs;

import com.weighbridge.admin.services.impls.EmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
public class EmailConfiguration {

    private final Environment environment;

    public EmailConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public EmailService emailService() {
        Properties props = new Properties();
        props.put("spring.mail.host", environment.getProperty("spring.mail.host"));
        props.put("spring.mail.port", environment.getProperty("spring.mail.port"));
        props.put("spring.mail.username", environment.getProperty("spring.mail.username"));
        props.put("spring.mail.password", environment.getProperty("spring.mail.password"));
        props.put("spring.mail.properties.mail.smtp.auth", environment.getProperty("spring.mail.properties.mail.smtp.auth"));
        props.put("spring.mail.properties.mail.smtp.starttls.enable", environment.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));

        return new EmailService(props);
    }
}
