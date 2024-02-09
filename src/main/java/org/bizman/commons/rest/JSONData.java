package org.bizman.commons.rest;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor @RequiredArgsConstructor
public class JSONData {
    private boolean success = true;
    private HttpStatus status = HttpStatus.OK;  // 응답코드 - OK: 성공
    @NonNull    // 수정 가능하도록 상수보다는 NonNull 사용하여 데이터 설정
    private Object data;    // JSON -> toString만 하면 되기 때문에 Object로 설정
    private Object message; // message가 단일일수도, 여러개일수도 있으므로 String보단 Object가 적합

}
