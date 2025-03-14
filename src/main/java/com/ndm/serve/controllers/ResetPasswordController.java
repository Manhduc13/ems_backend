package com.ndm.serve.controllers;

import com.ndm.serve.dtos.resetPassword.NewPasswordDTO;
import com.ndm.serve.dtos.resetPassword.ResetPasswordRequestDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resetPassword")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ResetPasswordController {
    ResetPasswordService resetPasswordService;
    TokenService tokenService;

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(
            @Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        try {
            resetPasswordService.sendEmail(resetPasswordRequestDTO);
            Map<String, Boolean> response = new HashMap<>();
            response.put("verify", Boolean.TRUE);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verify-token/{token}")
    public ResponseEntity<?> verifyToken(
            @PathVariable("token") String token) {
        try {
            tokenService.validateToken(token);
            Map<String, Boolean> response = new HashMap<>();
            response.put("verify", Boolean.TRUE);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid NewPasswordDTO request, BindingResult bindingResult) throws ResourceNotFoundException {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(resetPasswordService.resetPassword(request));
    }
}
