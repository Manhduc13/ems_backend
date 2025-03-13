package com.ndm.serve.controllers;

import com.ndm.serve.dtos.resetPassword.ResetPasswordRequestDTO;
import com.ndm.serve.services.resetPassword.ResetPasswordService;
import com.ndm.serve.services.token.TokenService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resetPassword")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ResetPasswordController {
    ResetPasswordService resetPasswordService;
    TokenService tokenService;

    @PostMapping("/verifyEmail")
    public ResponseEntity<?> verifyEmail(
            @Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        try {
            resetPasswordService.sendEmail(resetPasswordRequestDTO);
            return ResponseEntity.ok("Email sent");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verifyToken/{token}")
    public ResponseEntity<?> verifyToken(
            @PathVariable("token") String token) {
        try {
            tokenService.validateToken(token);
            return ResponseEntity.ok("Token verified");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
