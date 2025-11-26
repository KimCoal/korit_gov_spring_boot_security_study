package com.korit.security_study.service;

import com.korit.security_study.dto.ApiRespDto;
import com.korit.security_study.dto.SignUpReqDto;
import com.korit.security_study.entity.User;
import com.korit.security_study.entity.UserRole;
import com.korit.security_study.repository.UserRepository;
import com.korit.security_study.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    public ApiRespDto<?> addUser(SignUpReqDto signUpReqDto) {
        // username 중복확인
        Optional<User> foundUser = userRepository.findUserByUsername(signUpReqDto.getUsername());
        if (foundUser.isPresent()) {
            return new ApiRespDto<>("failed", "중복", null);
        }
        // 없으면 유저 추가
        Optional<User> optionalUser = userRepository.addUser(signUpReqDto.toEntity(bCryptPasswordEncoder));

        // 추가 후 userId로 userRole 추가
        UserRole userRole = UserRole.builder()
                .userId(optionalUser.get().getUserId())
                .roleId(3)
                .build();
        userRoleRepository.addUserRole(userRole);
        return new ApiRespDto<>("success" , "가입완료" , optionalUser.get());
    }

    public ApiRespDto<?> getUserByUsername(String username) {
        Optional<User> foundUser = userRepository.findUserByUsername(username);
        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "존재하지않음", null);
        }
        return new ApiRespDto<>("success", "성공", foundUser.get());
    }

}
