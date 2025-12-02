package com.korit.security_study_ex01.service;

import com.korit.security_study_ex01.dto.ApiRespDto;
import com.korit.security_study_ex01.dto.OAuth2MergeReqDto;
import com.korit.security_study_ex01.dto.OAuth2SignupReqDto;
import com.korit.security_study_ex01.entity.User;
import com.korit.security_study_ex01.entity.UserRole;
import com.korit.security_study_ex01.repository.OAuth2UserRepository;
import com.korit.security_study_ex01.repository.UserRepository;
import com.korit.security_study_ex01.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

// oauth2로 회원가입 또는 연동
@Service
public class OAuth2AuthService {

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApiRespDto<?> signup (OAuth2SignupReqDto oAuth2SignupReqDto) {
        Optional<User> foundUser = userRepository.findUserByEmail(oAuth2SignupReqDto.getEmail());

        if (foundUser.isPresent()) {
            return new ApiRespDto<>("failed", "이미 존재하는 이메일입니다", null);
        }
        Optional<User> foundUserByUsername = userRepository.findUserByUsername(oAuth2SignupReqDto.getUsername());
        if (foundUserByUsername.isPresent()) {
            return new ApiRespDto<>("failed", "이미 존재하는 사용자이름입니다", null);
        }

        Optional<User> optionalUser = userRepository.addUser(oAuth2SignupReqDto.toUserEntity(bCryptPasswordEncoder));
        UserRole userRole = UserRole.builder()
                .userId(optionalUser.get().getUserId())
                .roleId(3)
                .build();
        userRoleRepository.addUserRole(userRole);
        oAuth2UserRepository.addOAuth2User(oAuth2SignupReqDto.toOAuth2UserEntity(optionalUser.get().getUserId()));

        return new ApiRespDto<>("success", oAuth2SignupReqDto.getProvider() + "로 회원가입 완료", null);

    }
    public ApiRespDto<?> merge(OAuth2MergeReqDto oAuth2MergeReqDto) {
        Optional<User> foundUser = userRepository.findUserByUsername(oAuth2MergeReqDto.getUsername());
        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "사용자 정보가 일치하지 않습니다", null);
        }
        if (!bCryptPasswordEncoder.matches(oAuth2MergeReqDto.getPassword(), foundUser.get().getPassword())) {
            return new ApiRespDto<>("failed", "사용자 정보가 일치하지 않습니다", null);
        }

        oAuth2UserRepository.addOAuth2User(oAuth2MergeReqDto.toEntity(foundUser.get().getUserId()));
        return new ApiRespDto<>("success", "회원가입 성공", null);
    }
}
