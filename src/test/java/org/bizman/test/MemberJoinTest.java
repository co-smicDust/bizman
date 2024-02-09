package org.bizman.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.bizman.api.controllers.members.RequestJoin;
import org.bizman.commons.constants.MemberType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@SpringBootTest
@Transactional      // test 후 DB 지우기
@AutoConfigureMockMvc   // 세팅 편리, controll 단 테스트도 가능
public class MemberJoinTest {

    @Autowired
    private MockMvc mockMvc;    // 서버 켜지 않아도 테스트 가능

    @Test
    @DisplayName("회원 가입 테스트")
    void joinTest() throws Exception {
        // 객체를 자신이 직접 JSON data로 볼 수 있게 함
        RequestJoin form = RequestJoin.builder()
                //.email("user01@test.org")
                //.password("_aA123456")
                //.confirmPassword("_aA123456")
                //.name("사용자01")
                .mobile("010-0000-0000")
                //.agree(true)
                .build();

        ObjectMapper om = new ObjectMapper();   // Java 객체를 JSON 문자열로 변환
        String params = om.writeValueAsString(form);

        mockMvc.perform(
                post("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)  // 요청 데이터에 대한 타입 - 직접 입력 : "application/json"
                        .content(params)    // body data
                        .characterEncoding("UTF-8")
                        .with(csrf().asHeader())
        ).andDo(print());   // 요청과 응답이 잘 이루어졌는지 확인
    }

}
