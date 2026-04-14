package com.payroll.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendPasswordResetEmail(String recipientEmail, String userName, String resetLink) {
        logger.info("Sending password reset email to {} (user {}). Reset link: {}", recipientEmail, userName, resetLink);
        // In a production setup, configure Spring Mail and replace this stub with actual email sending.
    }
}
