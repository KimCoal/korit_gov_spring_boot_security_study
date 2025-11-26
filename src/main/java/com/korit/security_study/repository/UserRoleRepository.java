package com.korit.security_study.repository;

import com.korit.security_study.entity.UserRole;
import com.korit.security_study.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRoleRepository {

    @Autowired
    private UserRoleMapper userRoleMapper;

    public void addUserRole(UserRole userRole) {
        userRoleMapper.addUserRole(userRole);
    }
}
