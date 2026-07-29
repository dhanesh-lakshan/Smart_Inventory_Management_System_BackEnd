package com.inventory.service.impl;

import com.inventory.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${app.email.enabled}")
    private boolean emailEnabled;

    @Value("${app.email.from}")
    private String fromAddress;

    @Override
    public void sendEmail(String to, String subject, String body) {
        if (!emailEnabled) {
            // SMTP configure කරන්නම කලින්, console එකේ log කරනවා (local testing පහසුවට)
            log.info("===== [EMAIL DISABLED] Would send to: {} | Subject: {} | Body: {}", to, subject, body);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            // Email fail වුනත්, main business operation එක (sale/purchase) rollback වෙන්න එපා —
            // notification failure එකක් නිසා core transaction එකක් fail වෙන්නේ නෑ.
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}