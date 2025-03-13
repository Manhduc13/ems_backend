package com.ndm.serve.services.mail;

import com.ndm.serve.dtos.email.EmailRequestDTO;

public interface EmailService {
    void sendEmailAsync(EmailRequestDTO emailRequestDTO);
}
