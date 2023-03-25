package com.study.datajpa.repository;

import com.study.datajpa.dto.MemberDto;
import com.study.datajpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

/**
 * @Repository 넣지 않아도, interface로 되어만 있으면, 스프링부트가 자동으로 읽고,
 * 알아서 이 인터페이스의 구현첼르 다 만들어줌 (개발자가 구현체 만들지 않아도 됨)
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findHelloBy(); // find...By : 전체 조회

    List<Member> findTop3HelloBy(); // findTop3...By()

    // @Query 방식 1 - 엔티티 타입 조회
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    // @Query 방식 2 - 기본 타입 조회
    @Query("select m.username from Member m")
    List<String> findUsernameList(); // username 전체 조회

    // @Query 방식 2 - 기본 타입 조회  (DTO는 마치 생성해서 반환하는 것처럼 패키지명 다 함께 적어주어야함. 생성자(allArgsCons)필수)
    @Query("select new com.study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto(); // memberDto 전체 조회

    // @Query 방식 2 - 컬렉션 파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    // 다양한 반환타입 (find...By 사이는 이름 아무거나 넣어도 됨)
    List<Member> findListByUsername(String username); // 컬렉션

    Member findMemberByUsername(String username); // 단건

    Optional<Member> findOptionalByUsername(String username); // 단건 optional

    // Page 사용
    Page<Member> findPageByAge(int age, Pageable pageable); // count 쿼리 사용
    Slice<Member> findSliceByAge(int age, Pageable pageable); // count 쿼리 사용 x
    List<Member> findListByAge(int age, Pageable pageable); //count 쿼리 사용 안함

    @Query(value = "select m from Member m left join m.team t")
    Page<Member> findMemberByAge(int age, Pageable pageable);  // select와 count 모두 join되어서 쿼리 날라감

    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findMemberFasterByAge(int age, Pageable pageable); // count쿼리는 조인 안됨

    @Modifying(clearAutomatically = true) // 벌크성 수정, 삭제 쿼리는 @Modifying 사용! 사용 안하면 예외발생
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /**
     *  N+1 문제 해결하는 방법
     */
    @Override
    @EntityGraph(attributePaths = {"team"})  // 방식 1: 공통 메소드 오버라이드 (JPQL 사용 안하는 방식)
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})  // 방식 2: JPQL + EntityGraph (1번과 동일 결과)
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})  // 방식3: 파라미터 넣는 방식
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // JPA Hint
    @QueryHints(value =
        @QueryHint(name = "org.hibernate.readOnly", value = "true")
    )
    Member findReadOnlyByUsername(String username);

    // Lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String name);
}
