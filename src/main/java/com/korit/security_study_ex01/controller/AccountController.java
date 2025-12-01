package com.korit.security_study_ex01.controller;

import com.korit.security_study_ex01.dto.ModifyPasswordReqDto;
import com.korit.security_study_ex01.security.model.Principal;
import com.korit.security_study_ex01.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/modify/password")
    public ResponseEntity<?> modifyPassword(@RequestBody ModifyPasswordReqDto modifyPasswordReqDto,
                                            @AuthenticationPrincipal Principal principal) {
        return ResponseEntity.ok(accountService.modifyPassword(modifyPasswordReqDto,principal));
    }

    // 이메일 인증 구현
    // DB verify_tb에 user_id, verify_code추가 varchar
    // 회원가입시 랜덤 수 5자리 verify_code db에 저장
    // 로그인 후 이메일 인증 요청 API로 요청이 오면 db에 있던 코드 응답
    // 해당 코드로 인증 요청, db의 코드와 일치시 role을 일반사용자로 수정
}
