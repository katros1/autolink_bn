package com.katros.autolinkbn.services;

import com.katros.autolinkbn.entities.User;
import com.katros.autolinkbn.exceptions.EmailSendingException;
import com.katros.autolinkbn.exceptions.NotFoundException;
import com.katros.autolinkbn.repositories.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailTemplateService emailTemplateService;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${frontend.allowed-origin}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String adminEmail;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    public EmailService(JavaMailSender javaMailSender, EmailTemplateService emailTemplateService, UserRepository userRepository) {
        this.javaMailSender = javaMailSender;
        this.emailTemplateService = emailTemplateService;
        this.userRepository = userRepository;
    }


    public void sendOtpEmail(String recipient, String otp) {
        try {
            User userInfo = userRepository.findByEmail(recipient).orElseThrow(()-> new IllegalArgumentException("User not found"));

            Map<String, Object> variables = new HashMap<>();
            variables.put("name", userInfo.getLastName());
            variables.put("otp", otp);
            variables.put("email", userInfo.getEmail());
            variables.put("frontendUrl", frontendUrl);

            String emailContent = emailTemplateService.renderTemplate("reset-password-template", variables);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mailMessage = new MimeMessageHelper(mimeMessage, true);
            mailMessage.setFrom(sender);
            mailMessage.setTo(recipient);
            mailMessage.setSubject("Your OTP Code");
            mailMessage.setText(emailContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            logger.error("Failed to send OTP email to {}: {}", recipient, e.getMessage(), e);

            throw new EmailSendingException("Failed to send OTP email to " + recipient, e);
        }
    }

    public void sendWelcomingEmail(String recipient, String otp) {
        try {
            User user = userRepository.findByEmail(recipient)
                    .orElseThrow(() -> new NotFoundException("User not found"));

            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getLastName());
            variables.put("otp", otp);

            String emailContent = emailTemplateService.renderTemplate("welcome-email-template", variables);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mailMessage = new MimeMessageHelper(mimeMessage, true);
            mailMessage.setFrom(sender);
            mailMessage.setTo(recipient);
            mailMessage.setSubject("Your OTP for Email Verification");
            mailMessage.setText(emailContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new EmailSendingException("Failed to send OTP to " + recipient, e);
        }
    }


    public void sendBookingTicketEmail(String bookerEmail, String eventTitle, LocalDateTime eventTime, List<MultipartFile> documents) {
        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy - h:mm a");

            // Format the date-time
            String formattedDateTime = eventTime.format(formatter);

            Map<String, Object> variables = new HashMap<>();
//            variables.put("userName", user.getLastName());
            variables.put("eventTitle", eventTitle);

            String emailContent = emailTemplateService.renderTemplate("booking-ticket", variables);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mailMessage = new MimeMessageHelper(mimeMessage, true);
            mailMessage.setFrom(sender);
            mailMessage.setTo(bookerEmail);
            mailMessage.setSubject("Booking Confirmation: " + eventTitle +  " - " + formattedDateTime);
            mailMessage.setText(emailContent, true);

            for (MultipartFile document : documents) {
                mailMessage.addAttachment(Objects.requireNonNull(document.getOriginalFilename()), new ByteArrayResource(document.getBytes()));
            }

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", bookerEmail, e.getMessage(), e);

            throw new EmailSendingException("Failed to send email to " + bookerEmail, e);
        }
    }
}
