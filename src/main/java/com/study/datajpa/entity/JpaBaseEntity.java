package com.study.datajpa.entity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * 순수 jpa로 사용하는 BaseEntity
 */
@MappedSuperclass
@Getter
@Slf4j
public class JpaBaseEntity {

    @Column(updatable = false) // 실수로 값 바꿔도 디비에 update되지 않음
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        log.info("==prePersist(): save 쿼리 전 실행 ==");
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        log.info("==preUpdate(): update 쿼리 전 실행 ==");
        updatedDate = LocalDateTime.now();
    }
}
