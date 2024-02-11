package org.bizman.models.member;

import lombok.RequiredArgsConstructor;
import org.bizman.api.controllers.members.RequestLogin;
import org.bizman.configs.jwt.TokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberLoginService {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;    // 로그인한 회원정보 객체를 검증하여 만들 수 있는 빌더 형태 클래스

    public String login(RequestLogin form){
        // 이메일과 비밀번호를 이용해 authenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(form.email(), form.password());
        // authenticationToken을 검증하여 통과되면 authentication 빌드
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // authentication으로 JWT 토큰 발급 -> 쿠키에 넣으면 로그인 유지 가능
        String accessToken = tokenProvider.createToken(authentication);

        return accessToken;
    }
}
