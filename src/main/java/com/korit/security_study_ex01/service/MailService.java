package com.korit.security_study_ex01.service;

import com.korit.security_study_ex01.dto.ApiRespDto;
import com.korit.security_study_ex01.dto.SendMailReqDto;
import com.korit.security_study_ex01.entity.User;
import com.korit.security_study_ex01.entity.UserRole;
import com.korit.security_study_ex01.repository.UserRepository;
import com.korit.security_study_ex01.repository.UserRoleRepository;
import com.korit.security_study_ex01.security.jwt.JwtUtils;
import com.korit.security_study_ex01.security.model.Principal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MailService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JavaMailSender javaMailSender;

    public ApiRespDto<?> sendMail(SendMailReqDto sendMailReqDto, Principal principal) {
        if (!sendMailReqDto.getEmail().equals(principal.getEmail())) {
            return new ApiRespDto<>("failed", "잘못된 접근입니다", null);
        }

        Optional<User> foundUser = userRepository.findUserByEmail(sendMailReqDto.getEmail());
        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "사용자 정보를 확인해 주세요", null);
        }

        User user = foundUser.get();

        boolean hasTempRole = user.getUserRoles().stream()
                .anyMatch(r -> r.getRoleId() == 3);
        if (!hasTempRole) {
            return new ApiRespDto<>("failed", "인증이 필요한 계정이 아닙니다", null);
        }

        String token = jwtUtils.generateVerifyToken(user.getUserId().toString());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(sendMailReqDto.getEmail());
        message.setSubject("[ 이메일 인증입니다 ]");
        message.setText("링크를 클릭해 인증을 완료해주세요. : " +
                "http://localhost:8080/mail/verify?verifyToken=" + token);
        javaMailSender.send(message);

        return new ApiRespDto<>("success", "인증메일이 전송되었습니다", null);
    }

    public Map<String, Object> verify(String token) {
        Claims claims = null;
        Map<String, Object> resultMap = null;

        try {
            claims = jwtUtils.getClaims(token);
            String subject = claims.getSubject();

            if (!"VerifyToken".equals(subject)) {
                return resultMap = Map.of(
                        "status", "failed",
                        "message", "잘못된 접근입니다"
                );
            }

            Integer userId = Integer.parseInt(claims.getId());
            Optional<User> foundUser = userRepository.findUserByUserId(userId);
            if (foundUser.isEmpty()) {
                return Map.of(
                        "status", "failed",
                        "message", "존재하지 않는 사용자입니다."
                );
            }

            List<UserRole> userRoles = foundUser.get().getUserRoles();
            Optional<UserRole> tempUserRole = userRoles.stream()
                    .filter(r -> r.getRoleId() == 3)
                    .findFirst();
            if (tempUserRole.isEmpty()) {
                return Map.of(
                        "status", "failed",
                        "message", "이미 인증완료된 메일입니다"
                );
            }
            UserRole userRole = tempUserRole.get();
            userRole.setRoleId(2);
            userRoleRepository.updateUserRole(userRole);

            return Map.of(
                    "status", "success",
                    "message", "이메일 인증이 완료되었습니다"
            );
        } catch (ExpiredJwtException e) {
             resultMap = Map.of(
                    "status", "failed",
                    "message", "만료된 인증요청입니다"
            );
        } catch (JwtException e) {
            resultMap = Map.of(
                    "status", "failed",
                    "message", "문제가 발생"
            );
        }
        return resultMap;
    }
}
