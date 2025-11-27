package com.korit.security_study_ex01.repository;

import com.korit.security_study_ex01.entity.UserRole;
import com.korit.security_study_ex01.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleRepository {

    @Autowired
    private UserRoleMapper userRoleMapper;

    public void addUserRole(UserRole userRole) {
        userRoleMapper.addUserRole(userRole);
    }
}
