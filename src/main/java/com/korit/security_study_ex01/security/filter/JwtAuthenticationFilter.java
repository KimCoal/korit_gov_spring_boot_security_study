package com.korit.security_study_ex01.security.filter;

import com.korit.security_study_ex01.entity.User;
import com.korit.security_study_ex01.repository.UserRepository;
import com.korit.security_study_ex01.security.jwt.JwtUtils;
import com.korit.security_study_ex01.security.model.Principal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter implements Filter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // 해당 메소드가 아니면 다음 필터로 넘긴다
        List<String> methods = List.of("POST", "GET", "PUT", "PATCH", "DELETE");
        if (!methods.contains(request.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String authorization = request.getHeader("Authorization");
        System.out.println("Bearer 토큰 : " + authorization);

        // bearer 형식인지 확인
        if (jwtUtils.isBearer(authorization)) {
            String accessToken = jwtUtils.removeBearer(authorization);
            System.out.println("accessToken" + accessToken);
            try {
                Claims claims = jwtUtils.getClaims(accessToken);
                String id = claims.getId();
                System.out.println("id" + id);
                Integer userId = Integer.parseInt(id);
                // userId로 회원 조회 > Principal
                Optional<User> foundUser = userRepository.findUserByUserId(userId);
                System.out.println("user" +foundUser.get());
                foundUser.ifPresentOrElse((user -> {
                    Principal principal = Principal.builder()
                            .userId(user.getUserId())
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .email(user.getEmail())
                            .userRoles(user.getUserRoles())
                            .build();
                    // UsernamePasswordAuthenticationToken 객체 생성
                    Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
                    // spring security의 인증 컨텍스트에 인증 객체 저장 > 이후의 요청은 인증된 사용자로 간주됨
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }), () -> {
                    throw new AuthenticationServiceException("인증 실패");
                });
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
