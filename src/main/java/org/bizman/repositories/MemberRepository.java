package org.bizman.repositories;

import org.bizman.entities.Member;
import org.bizman.entities.QMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member> {

    Optional<Member> findByEmail(String email); // 이메일로 회원정보 조회

    default boolean exists(String email){   // 바로 쓸 수 있는 메서드라 default
        return exists(QMember.member.email.eq(email));  // 이메일 검증
    }
}
