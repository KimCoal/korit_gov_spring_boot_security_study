package com.korit.security_study_ex01.repository;

import com.korit.security_study_ex01.dto.ModifyPasswordReqDto;
import com.korit.security_study_ex01.entity.User;
import com.korit.security_study_ex01.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    @Autowired
    private UserMapper userMapper;

    public Optional<User> findUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }
    public Optional<User> findUserByUserId(Integer userId) {
        return userMapper.findUserByUserId(userId);
    }
    public Optional<User> findUserByEmail(String email) {
        return userMapper.findUserByEmail(email);
    }

    public Optional<User> addUser (User user) {
        try {
            userMapper.addUser(user);
        } catch (DuplicateKeyException e) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    public int modifyPassword(User user) {
        return userMapper.modifyPassword(user);
    }
}
