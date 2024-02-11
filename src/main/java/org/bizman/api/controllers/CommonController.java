package org.bizman.api.controllers;

import org.bizman.commons.exceptions.CommonException;
import org.bizman.commons.rest.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

// 컨트롤러가 실행될 때 공통으로 처리해야할 것들
@RestControllerAdvice("org.bizman.api.controllers") // 여기 포함한 하위패키지 모두 포함
public class CommonController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JSONData> errorHandler(Exception e){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;   // 기본 상태 코드
        Object message = e.getMessage();

        if (e instanceof CommonException) {
            CommonException commonException = (CommonException) e;
            status = commonException.getStatus();

            if(commonException.getMessages() != null) message = commonException.getMessages();
        } else if (e instanceof BadCredentialsException) {  // BadCredentialsException : 500 -> 401이 나오는 게 더 좋음
            status = HttpStatus.UNAUTHORIZED;   // 401
        } else if (e instanceof AccessDeniedException) {    // AccessDeniedException : 500 -> 403이 나오는 게 더 좋음
            status = HttpStatus.FORBIDDEN;  //403
        }

        JSONData data = new JSONData();
        data.setSuccess(false);
        data.setStatus(status);
        data.setMessage(message);

        e.printStackTrace();

        return ResponseEntity.status(status).body(data);
    }
}
