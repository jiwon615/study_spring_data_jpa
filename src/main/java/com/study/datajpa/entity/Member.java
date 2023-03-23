package com.study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Entity // Entity 사용시 protected 제어레벨 이상의 기본생성자 필수
public class Member {

    @Id
    @GeneratedValue
    private Long id;
    private String username;

    protected Member() {
    }

    public Member(String username) {
        this.username = username;
    }
}
