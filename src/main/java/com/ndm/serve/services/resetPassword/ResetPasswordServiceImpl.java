package com.ndm.serve.services.resetPassword;

import com.ndm.serve.dtos.email.EmailRequestDTO;
import com.ndm.serve.dtos.resetPassword.ResetPasswordRequestDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import com.ndm.serve.repositories.EmployeeRepository;
import com.ndm.serve.services.mail.EmailService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ResetPasswordServiceImpl implements ResetPasswordService {
    EmailService emailService;
    EmployeeRepository employeeRepository;

    @NonFinal
    @Value("${app.security.access-token-secret-key}")
    private String secretKey;

    @NonFinal
    @Value("${app.security.reset-token-expired-in-second}")
    private Integer expireTime;

    @Value("${ems.email.template.reset-password.name}")
    @NonFinal
    private String resetTemplate;

    @Override
    public void sendEmail(ResetPasswordRequestDTO resetPasswordRequestDTO) throws ResourceNotFoundException {
        if (employeeRepository.findByEmail(resetPasswordRequestDTO.getEmail()).isPresent()) {
            // Send email
            EmailRequestDTO emailRequestDTO = new EmailRequestDTO();
            emailRequestDTO.setTo(resetPasswordRequestDTO.getEmail());
            emailRequestDTO.setSubject("Reset Password");
            emailRequestDTO.setTemplateName(resetTemplate);

            String token = generateResetPasswordToken(resetPasswordRequestDTO);

            Map<String, Object> model = Map.of(
                    "email", resetPasswordRequestDTO.getEmail(),
                    "token", token
            );
            emailRequestDTO.setVariables(model);
            emailService.sendEmailAsync(emailRequestDTO);
        } else {
            throw new ResourceNotFoundException("Employee with this email address does not exist");
        }
    }

    private String generateResetPasswordToken(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        LocalDateTime expiredAt = LocalDateTime.now().plusSeconds(expireTime);

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

        Date expiration = Date.from(expiredAt.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .subject(resetPasswordRequestDTO.getEmail())
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
}
