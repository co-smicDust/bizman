package org.bizman.configs;

import jakarta.servlet.http.HttpServletResponse;
import org.bizman.configs.jwt.CustomJwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity  // 기본 웹 보안 활성화
@EnableMethodSecurity   // @PreAuthorize("hasAuthority('ADMIN')) : 관리자만 허용 가능한 메서드 -> 요청 메서드 간 권한 부여, 특정 권한 설정
public class SecurityConfig {

    @Autowired
    private CustomJwtFilter customJwtFilter;
    @Autowired
    private CorsFilter corsFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable()) // jwt 토큰 방식으로 구현하기 위해 csrf 비활성화 필수
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)    // 사용자 로그인 처리 핉터 전에 데이터 공유를 다른 서버측에서도 가능하게
                .addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class)   // 토큰이 요청 헤더를 통해 넘어오면 로그인
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));  // STATELESS : 세션 사용 X

        http.exceptionHandling(c -> {   // 인증, 인가 실패시
            /*
            c.authenticationEntryPoint(new AuthenticationEntryPoint() {
                @Override
                public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                }
            })      -> 너무 기므로 아래 람다식으로 대체
            */
            c.authenticationEntryPoint((req, res, e) -> {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED);   // 401
            });

            /*
            c.accessDeniedHandler(new AccessDeniedHandler() {
                @Override
                public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                }
            })      -> 너무 기므로 아래 람다식으로 대체
            */
            c.accessDeniedHandler((req, res, e) -> {
               res.sendError(HttpServletResponse.SC_FORBIDDEN);     //403
            });
        });

        http.authorizeHttpRequests(c -> {   // 전체 가능한 페이지 및 로그인시 가능한 페이지 설정
           c.requestMatchers(   // 인증이 필요한 페이지 설정
                   "/api/v1/member",    // 회원가입
                   "/api/v1/member/token",     // 로그인
                   "/api/v1/member/exists/**").permitAll()  // 아이디, 이메일 중복 여부 체크
                   .anyRequest().authenticated();   // 나머지 URL은 모두 회원 인증 (토큰 인증) 필요
        });

        // 일단 활성화, 설정 무력화만. 점점 설정 추가해나감.
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
