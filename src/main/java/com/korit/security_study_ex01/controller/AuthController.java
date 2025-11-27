package com.korit.security_study_ex01.controller;

import com.korit.security_study_ex01.dto.SignUpReqDto;
import com.korit.security_study_ex01.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpReqDto signUpReqDto) {
        return ResponseEntity.ok(authService.addUser(signUpReqDto));
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(authService.getUserByUsername(username));
    }
}
