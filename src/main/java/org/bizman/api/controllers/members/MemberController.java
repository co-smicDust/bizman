package org.bizman.api.controllers.members;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bizman.commons.Utils;
import org.bizman.commons.exceptions.BadRequestException;
import org.bizman.commons.rest.JSONData;
import org.bizman.entities.Member;
import org.bizman.models.member.MemberInfo;
import org.bizman.models.member.MemberLoginService;
import org.bizman.models.member.MemberSaveService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController // 무조건 JSON 형태로만 응답할 수 있게
@RequestMapping("/api/v1/member")   // v1: version
@RequiredArgsConstructor
public class MemberController {

    private final MemberSaveService saveService;
    private final MemberLoginService loginService;

    @PostMapping
    // @Valid 커맨드 객체 검증 / @RequestBody JSON으로 인식해서 변환
    public ResponseEntity<JSONData> join(@RequestBody @Valid RequestJoin form, Errors errors){
        saveService.save(form, errors);

        errorProcess(errors);

        JSONData data = new JSONData();
        data.setStatus(HttpStatus.CREATED);  // 응답코드 : 201

        return ResponseEntity.status(data.getStatus()).body(data);
    }

    @PostMapping("/token")
    public ResponseEntity<JSONData> token(@RequestBody @Valid RequestLogin form, Errors errors) {
        errorProcess(errors);

        String accessToken = loginService.login(form);

        /**
         * 1. 응답 body - JSONData 형식으로
         * 2. 응답 헤더 - Authorization : Bearer 타입 JWT 토큰
         */

        JSONData data = new JSONData(accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        return ResponseEntity.status(data.getStatus()).headers(headers).body(data);
    }

    // 로그인 유지를 위해 토큰을 통한 회원정보 조회
    @GetMapping("/info")    // 회원 인증을 해야 접근 가능한 페이지
    public JSONData info(@AuthenticationPrincipal MemberInfo memberInfo) {  // 요청 메서드 주입
        Member member = memberInfo.getMember();

        return new JSONData(member);
    }

    /* test용 임시 메서드
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")  // 권한 ADMIN으로 제한
    public String admin() {
        return "관리자 페이지 접속...";
    }
    */

    private void errorProcess(Errors errors) {
        if (errors.hasErrors()) {
            throw new BadRequestException(Utils.getMessages(errors));
        }
    }
}
