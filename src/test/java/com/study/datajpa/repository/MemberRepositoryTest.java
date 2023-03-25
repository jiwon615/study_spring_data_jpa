package com.study.datajpa.repository;

import com.study.datajpa.dto.MemberDto;
import com.study.datajpa.entity.Member;
import com.study.datajpa.entity.Team;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional // jpa의 모든 변경은 트랜잭션 안에서 이루어져야 하므로, Transactional 필요
@Rollback(false) // transaction 끝나면 결과 롤백해버리므로, 디비에 값을 확인해볼 수 없어서 테스트에서는 공부를 위해 rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); // 같은 트랜잭션 안에서는 영속성 컨텍스트 안에서의 인스턴스는 동일성을 보장
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    @DisplayName("쿼리 메소드 테스트 - 메소드 이름(1)")
    void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("쿼리 메소드 테스트2 - 메소드 이름(2)")
    void findBy() {
        List<Member> helloBy = memberRepository.findHelloBy();
        log.info("=======");
        List<Member> findTop3HelloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    @DisplayName("쿼리 메소드 테스트3- @Query 애노테이션 사용해서 레파지토리 인터페이스에 쿼리 직접 정의(1)")
    void findUser() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    @DisplayName("쿼리 메소드 테스트3- @Query 애노테이션 사용해서 레파지토리 인터페이스에 쿼리 직접 정의(2)")
    void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<String> result = memberRepository.findUsernameList();
        for (String name : result) {
            log.info("name = {}", name);
        }
    }

    @Test
    @DisplayName("쿼리 메소드 테스트3- @Query 애노테이션 사용해서 레파지토리 인터페이스에 쿼리 직접 정의(3)")
    void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> result = memberRepository.findMemberDto();
        for (MemberDto dto : result) {
            log.info("dto = {}", dto);
        }
    }

    @Test
    @DisplayName("쿼리 메소드 테스트3- 컬렉션 파라미터 바인딩")
    void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            log.info("member = {}", member);
        }
    }

    @Test
    @DisplayName("다양한 반환 타입 테스트")
    void returnTypes() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // 리스트 : 값이 없는 경우 빈 컬랙션 반환 ( [])
        List<Member> result = memberRepository.findListByUsername("AAA");
        log.info(String.valueOf(result));
        for (Member member : result) {
            log.info("result = {}", member);
            log.info("=======");
        }

        // 단건 : 값이 없는 경우 null 반환
        Member result2 = memberRepository.findMemberByUsername("AAA");
        log.info("result2 = {}", result2);
        log.info("=======");

        // Optional: 값이 없는 경우 Optional.empty 반환
        Optional<Member> result3 = memberRepository.findOptionalByUsername("AAA");
        log.info("result3 = {}", result3);
    }

    @Test
    @DisplayName("페이징 테스트 - Page 반환타입 (count 쿼리 O)")
    void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0; // jpa는 0부터 페이징 시작한다
        int limit = 3;

        // Pageable의 구현체
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findPageByAge(age, pageRequest);

        // then
        List<Member> content = page.getContent();
        for (Member member : content) {
            log.info("member = {}", member);
        }

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5); // totalCount
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2); // 총 2 페이지
        assertThat(page.isFirst()).isTrue(); // 첫 페이지 여부
        assertThat(page.hasNext()).isTrue(); // 다음 페이지 존재 여부
    }

    @Test
    @DisplayName("페이징 테스트 - Slice 반환타입 (count 쿼리 X)")
    void paging2() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0; // jpa는 0부터 페이징 시작한다
        int limit = 3;

        // Pageable의 구현체
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);
//        List<Member> page = memberRepository.findListByAge(age, pageRequest);

        // then
        List<Member> content = page.getContent();
        for (Member member : content) {
            log.info("member = {}", member);
        }

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue(); // 첫 페이지 여부
        assertThat(page.hasNext()).isTrue(); // 다음 페이지 존재 여부
    }

    @Test
    @DisplayName("페이징 테스트 - Page 반환타입 & JPQL 사용해서 join")
    void paging3() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0; // jpa는 0부터 페이징 시작한다
        int limit = 3;

        // Pageable의 구현체
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "username"));

        // when
//        Page<Member> page = memberRepository.findMemberByAge(age, pageRequest); // countQuery도 JOIN
        Page<Member> page = memberRepository.findMemberFasterByAge(age, pageRequest); // countQuery는 별도 분리
        // Page를 유지하면서 ENTITY -> DTO 로 변환
        Page<MemberDto> dtoPage = page.map(m -> new MemberDto());
        // then
        List<Member> content = page.getContent();
        for (Member member : content) {
            log.info("member = {}", member);
        }

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5); // totalCount
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2); // 총 2 페이지
        assertThat(page.isFirst()).isTrue(); // 첫 페이지 여부
        assertThat(page.hasNext()).isTrue(); // 다음 페이지 존재 여부
    }

    @Test
    @DisplayName("벌크성 수정 쿼리 테스트 - 20살 이상인 경우는 모두 1살씩 증가 시키기")
    void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 24));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);

        /**
         * 벌크수정의 경우, 영속성 컨텍스트 거치지 않고 바로 디비 update 쿼리 문 날리므로,
         * 영속성을 flush, clear 해줘야 영속성 컨텍스트의 엔티티 상태와 DB의 엔티티 상태 같아질 수 있음
         * 즉, flush, clear 안하고 member5 조회시, 나이는 그대로 40
         *
         * 방법1 : 아래 처럼 em.flush(), em.clear() 해주기
         * 방법2 : clearAutomatically = true 옵션 추가  ex) @Modifying(clearAutomatically = true)
         */
//        em.flush();
//        em.clear();

        Member member5 = memberRepository.findMemberByUsername("member5");
        log.info("member5={}", member5);

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    @DisplayName("지연로딩 관계에서 N+1 문제 및 해결")
    void findMemberLazy() {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        Team teamC = new Team("teamC");
        Team teamD = new Team("teamD");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        teamRepository.save(teamC);
        teamRepository.save(teamD);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));
        memberRepository.save(new Member("member3", 30, teamC));
        memberRepository.save(new Member("member4", 40, teamD));
        memberRepository.save(new Member("member4", 50, teamD));

        em.flush();
        em.clear();

        // when
//        List<Member> members = memberRepository.findAll(); // N+1 문제 (override하여 EntityGraph 적용 전에는 N+1 터짐)
//        List<Member> members = memberRepository.findMemberEntityGraph(); // N+1 문제 해결 (JPQL + EntityGraph)
        List<Member> members = memberRepository.findEntityGraphByUsername("member1"); // N+1 문제 (파라미터 넣는 방식)
        for (Member member : members) {
            log.info("member.getTeam()은 가짜 프록시 객체={}", member.getTeam().getClass());
            log.info("getTeam().getName()={}", member.getTeam().getName());
        }
    }

    @Test
    @DisplayName("조회용으로 사용하기 위한 쿼리 힌트")
    void queryHint() {
        // given
        memberRepository.save(new Member("member1", 10));
        em.flush(); // 디비에 쿼리날림 (아직 영속성 컨텍스트에는 member 남아있음)
        em.clear(); // 1차 캐쉬 지움으로써 영속성 컨텍스트 깨끗히 비움

        // when
        Member member = memberRepository.findReadOnlyByUsername("member1");
        member.setUsername("member2");

        // then
        em.flush(); // hint 적용 안하면, 이때 update 쿼리문 날라가지만, hint문 적용시 update 안됨
    }
}