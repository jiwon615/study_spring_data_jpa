package com.study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity // Entity 사용시 protected 제어레벨 이상의 기본생성자 필수
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"}) // 연관관계(Team)은 toString() 하지말자 (무한루프 빠질 수 있음)
public class Member extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY) // 연관관계는 무조건 LAZY로 세팅할 것(성능최적화위해 필수)
    @JoinColumn(name = "team_id") // (name = "외래키")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            this.team = team;
        }
    }

    // Member와 Team에 대해 서로 연관관계 세팅해주는 메소드 필요
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
