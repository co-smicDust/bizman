package org.bizman.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// 날짜, 시간과 관련된 공통 부분
@MappedSuperclass   // 공통 부분 - 상위 클래스
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)  // 이벤트 감지를 통해
public abstract class Base {
    @CreatedDate
    @Column(updatable = false)  // 최초 등록될 때만 등록, 수정 -> 업데이트 X
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime modifiedAt;

}
