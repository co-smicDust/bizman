package org.bizman.api.controllers.members;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.bizman.commons.Utils;
import org.bizman.commons.exceptions.BadRequestException;
import org.bizman.commons.rest.JSONData;
import org.bizman.models.member.MemberSaveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 무조건 JSON 형태로만 응답할 수 있게
@RequestMapping("/api/v1/member")   // v1: version
@RequiredArgsConstructor
public class MemberController {

    private final MemberSaveService saveService;
    @PostMapping
    // @Valid 커맨드 객체 검증 / @RequestBody JSON으로 인식해서 변환
    public ResponseEntity<JSONData> join(@RequestBody @Valid RequestJoin form, Errors errors){
        saveService.save(form, errors);

        if (errors.hasErrors()) {
            throw new BadRequestException(Utils.getMessages(errors));
        }

        JSONData data = new JSONData();
        data.setStatus(HttpStatus.CREATED);  // 응답코드 : 201

        return ResponseEntity.status(data.getStatus()).body(data);
    }
}
