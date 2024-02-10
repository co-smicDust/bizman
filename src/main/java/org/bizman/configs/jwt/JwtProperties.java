package org.bizman.configs.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


// jwt 설정 값이 담겨있는 전달 객체
@Data
@ConfigurationProperties(prefix = "jwt")    // "jwt"가 붙어있는 하위 속성을 자동으로 변수에 담아줌
public class JwtProperties {
    // application.yml의 jwt 설정과 이름이 같아야 함.
    private String header;
    private String secret;
    private Long accessTokenValidityInSeconds;
}
