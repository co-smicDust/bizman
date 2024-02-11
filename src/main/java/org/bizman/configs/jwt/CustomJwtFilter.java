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
import org.springframework.web.filter.GenericFilterBean;    // 필터 정의할 수 있음

import java.io.IOException;


// 토큰이 넘어오면 로그인 유지
@Component
@RequiredArgsConstructor
public class CustomJwtFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;  // 회원정보 가져옴

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;

        /* 요청 헤더 Authorization 항목의 JWT 토큰 추출 S */
        String header = req.getHeader("Authorization");
        String jwt = null;
        if (StringUtils.hasText(header)) {  // == (header != null && !header.isBlank())
            // Bearer : 요청 헤더 Authorization의 방식 중 하나. token을 통해 인증할 때 사용. 그 외에 basic(팝업창에 아이디, 비번 입력)등이 있음. 배열의 7번째부터 토큰.
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
