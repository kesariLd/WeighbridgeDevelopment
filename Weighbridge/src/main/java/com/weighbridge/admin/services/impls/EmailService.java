package com.weighbridge.admin.services.impls;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Properties;
@Slf4j
@Component
public class EmailService {

    private static Properties properties;

    @Autowired
    JavaMailSender javaMailSender;

    // Constructor to pass properties
    public EmailService(Properties props) {
        properties = props;
    }

    public void sendCredentials(String userEmail, String username, String password) {

        log.info("mail"+username+" "+userEmail);
        log.info("host"+ properties.getProperty("spring.mail.host"));
        log.info("port"+ properties.getProperty("spring.mail.port"));
        // Get the Session object using the loaded properties
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String username = properties.getProperty("spring.mail.username");
                String password = properties.getProperty("spring.mail.password");

                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a default MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress(properties.getProperty("spring.mail.username")));

            // Set To: header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));

            // Set Subject: header field
            message.setSubject("Your Credentials");

            // Set the message content
            message.setText("Your userId: " + username + "\nYour password: " + password);

            // Send the message
          javaMailSender.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            // Log the exception details
            log.error("Failed to send email: " + e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }

    }
}
