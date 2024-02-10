package org.bizman.configs.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.bizman.commons.Utils;
import org.bizman.commons.exceptions.BadRequestException;
import org.bizman.models.member.MemberInfo;
import org.bizman.models.member.MemberInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TokenProvider {

    private final String secret;
    private final long tokenValidityInSeconds;
    @Autowired
    private MemberInfoService infoService;

    private Key key;    // 인코딩된 secret을 security 객체로 바꿔서 할당

    // 생성자로 초기화
    public TokenProvider(String secret, Long tokenValidityInSeconds) {    // 디코딩 후 HMAC -> SHA512 + 검증을 위한 Message
        this.secret = secret;
        this.tokenValidityInSeconds = tokenValidityInSeconds;

        byte[] bytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * 토큰 발급
      */

    public String createToken(Authentication authentication) {  // OAuth와 달리 정보 담겨있음. 간편하지만 보안면에서는 떨어짐. 반환값 : Token
        String authories = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)    // map(a -> a.getAuthority())
                .collect(Collectors.joining(","));

        Date expires = new Date((new Date()).getTime() + tokenValidityInSeconds * 1000);    // 현재 시간 ms + 360 * 1000

        return Jwts.builder()
                .setSubject(authentication.getName())    // 아이디
                .claim("auth", authories)             // 접근 권한
                .signWith(key, SignatureAlgorithm.HS512) // HMAC + SHA512
                .setExpiration(expires)                  // 토큰 유효시간
                .compact();                              // 토큰 문자열 생성;
    }

    /**
     * 회원정보 조회
      */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()   // Claims: 토큰 가지고 정보 분해
                .setSigningKey(key)     // 파싱 전 검증
                .build()                // 감싸주기
                .parseClaimsJws(token)  // token에서 회원정보 가져옴
                .getPayload();          // Claims 객체 생성

        String email = claims.getSubject(); // createToken에서 가져옴
        MemberInfo userDetails = (MemberInfo)infoService.loadUserByUsername(email);

        String auth = claims.get("auth").toString();
        List<? extends GrantedAuthority> authorities = Arrays.stream(auth.split(",")).map(SimpleGrantedAuthority::new).toList();
        userDetails.setAuthorities(authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, token, authorities);

        return authentication;
    }

    public void validateToken(String token) {
        try {
            Claims claims = Jwts.parser()   // Claims: 토큰 가지고 정보 분해
                    .setSigningKey(key)     // 파싱 전 검증
                    .build()                // 감싸주기
                    .parseClaimsJws(token)  // token에서 회원정보 가져옴
                    .getPayload();          // Claims 객체 생성

        } catch (ExpiredJwtException e){
            throw new BadRequestException(Utils.getMessage("EXPIRED.JWT_TOKEN", "validation"));
        } catch (UnsupportedJwtException e) {
            throw new  BadRequestException(Utils.getMessage("UNSUPPORTED.JWT_TOKEN", "validation"));
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException e) {
            throw new BadRequestException(Utils.getMessage("INVALID_FORMAT.JWT.TOKEN", "validation"));
        }
    }
}
