package com.study.datajpa.entity;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
@Slf4j
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("Member & Team Entity가 서로 잘 연관관계 맺어졌는지 테스트")
    void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 초기화
        em.flush(); // 강제로 영속성 컨텍스트에 있던 것들을 가지고 디비에 쿼리 날려버림(여기서는 insert 시켜버림)
        em.clear(); // 영속성 컨텍스트에 남아있는 캐시도 다 날려버림

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            log.info("member = {}", member);
            log.info("--> member.team = {}", member.getTeam());
        }
    }

}