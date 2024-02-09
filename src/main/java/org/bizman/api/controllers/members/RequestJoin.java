package org.bizman.api.controllers.members;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

// 회원가입 데이터 받아보기
@Builder
public record RequestJoin(
        @NotBlank @Email    // 이메일 형식 검증
        String email,
        @NotBlank @Size(min = 8)    // 자리수 제한 조건
        String password,
        @NotBlank
        String confirmPassword,
        @NotBlank
        String name,
        String mobile,
        @AssertTrue // 값이 참인 것만 검증
        Boolean agree
) {
}
