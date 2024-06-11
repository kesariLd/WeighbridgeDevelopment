package com.weighbridge.admin.services.impls;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set From: header field
            helper.setFrom(new InternetAddress(properties.getProperty("spring.mail.username")));

            // Set To: header field
            helper.setTo(userEmail);


            // Set Subject: header field
            helper.setSubject("Your Credentials");
            String content="<p>Dear user,</p>"+"<p>You have successfully resgistered.Please use your userId and password to reset password</p>"+"<p>userId: <strong>"+username+"</strong></p>"+"<p>password: <strong>"+password+"</strong></p>";
            // Set the message content
            helper.setText(content,true);
            // Send the message
          javaMailSender.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException|MailException e) {
            // Log the exception details
            log.error("Failed to send email: " + e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }

    }

    public boolean sendPasswordResetEmail(String toEmail, String resetToken) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {

            String fromEmail = "spring.mail.username";
            helper.setFrom(new InternetAddress(fromEmail));
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");
            String content="<p>Dear user,</p>" +
                    "<p>Please use the following otp to reset your password to RegeneratePassword</p>"+"<p>OTP :<strong>"+resetToken+"</strong></p>";
            helper.setText(content, true);
            javaMailSender.send(message);
            return true;
        } catch (MessagingException | MailException e) {
            e.printStackTrace();
            return false;
        }
    }
}

