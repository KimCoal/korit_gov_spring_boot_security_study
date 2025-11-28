package com.korit.security_study_ex01.mapper;

import com.korit.security_study_ex01.dto.ModifyPasswordReqDto;
import com.korit.security_study_ex01.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findUserByUsername (String username);
    Optional<User> findUserByUserId (Integer userId);
    int addUser(User user);
    int modifyPassword(User user);
}
