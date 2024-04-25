package com.weighbridge.admin.configs;

import com.weighbridge.admin.services.impls.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
public class EmailConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public EmailService emailService() {
        Properties props = new Properties();
        props.put("spring.mail.host", env.getProperty("spring.mail.host"));
        props.put("spring.mail.port", env.getProperty("spring.mail.port"));
        props.put("spring.mail.username", env.getProperty("spring.mail.username"));
        props.put("spring.mail.password", env.getProperty("spring.mail.password"));
        props.put("spring.mail.properties.mail.smtp.auth", env.getProperty("spring.mail.properties.mail.smtp.auth"));
        props.put("spring.mail.properties.mail.smtp.starttls.enable", env.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));

        return new EmailService(props);
    }
}
