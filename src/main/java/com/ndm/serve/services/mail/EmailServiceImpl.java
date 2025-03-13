package com.ndm.serve.services.mail;

import com.ndm.serve.dtos.email.EmailRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Value("${spring.mail.username}")
    private String from;

    @Override
    @Async
    public void sendEmailAsync(EmailRequestDTO emailRequestDTO) {
        if (emailRequestDTO.getTo() == null || emailRequestDTO.getTo().isEmpty()) {
            throw new RuntimeException("Email recipient is required");
        }

        if (emailRequestDTO.getSubject() == null || emailRequestDTO.getSubject().isEmpty()) {
            throw new RuntimeException("Email subject is required");
        }

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(emailRequestDTO.getTo());
            helper.setSubject(emailRequestDTO.getSubject());
            helper.setText(generateEmailBody(emailRequestDTO.getTemplateName(), emailRequestDTO), true);

            if (emailRequestDTO.getCc() != null && !emailRequestDTO.getCc().isEmpty()) {
                helper.setCc(emailRequestDTO.getCc());
            }

            if (emailRequestDTO.getBcc() != null && !emailRequestDTO.getBcc().isEmpty()) {
                helper.setBcc(emailRequestDTO.getBcc());
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String generateEmailBody(String templateName, EmailRequestDTO request) {
        Context context = new Context();

        context.setVariables(request.getVariables());

        return templateEngine.process(templateName, context);
    }
}
