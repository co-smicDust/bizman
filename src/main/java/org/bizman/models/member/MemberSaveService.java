package org.bizman.models.member;

import lombok.RequiredArgsConstructor;
import org.bizman.api.controllers.members.JoinValidator;
import org.bizman.api.controllers.members.RequestJoin;
import org.bizman.commons.constants.MemberType;
import org.bizman.entities.Member;
import org.bizman.repositories.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
@RequiredArgsConstructor
public class MemberSaveService {
    private final MemberRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JoinValidator joinValidator;

    public  void save(RequestJoin form, Errors errors){
        joinValidator.validate(form, errors);

        if(errors.hasErrors()){ // 오류가 있는 경우 실행하지 얺기
            return;
        }

        // 회원 가입 처리
        String hash = passwordEncoder.encode(form.password());
        Member member = Member.builder()
                .email(form.email())
                .name(form.name())
                .password(hash)
                .mobile(form.mobile())
                .type(MemberType.USER)
                .build();

        save(member);
    }


    // 데이터에 따라 회원정보수정 혹은 회원가입
    public void save(Member member) {
        String mobile = member.getMobile();
        if(member != null) {
            mobile = mobile.replaceAll("\\D", ""); // 숫자만. 특수문자 제거
            member.setMobile(mobile);
        }

        repository.saveAndFlush(member);
    }
}
