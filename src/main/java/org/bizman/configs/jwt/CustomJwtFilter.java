package org.bizman.configs.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;


// 토큰이 넘어오면 로그인 유지
@Component
@RequiredArgsConstructor
public class CustomJwtFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;

        /* 요청 헤더 Authorization 항목의 JWT 토큰 추출 S */
        String header = req.getHeader("Authorization");
        String jwt = null;
        if (StringUtils.hasText(header)) {  // == (header != null && !header.isBlank())
            // Bearer .... 배열 뒤 7번째부터
            jwt = header.substring(7);
        }
        /* 요청 헤더 Authorization 항목의 JWT 토큰 추출 E */

        /* 로그인 유지 처리 S */
        if (StringUtils.hasText(jwt)) {
            tokenProvider.validateToken(jwt);   // 토큰에 이상 있을 경우 예외 발생

            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        /* 로그인 유지 처리 E */

        chain.doFilter(request, response);
    }
}
