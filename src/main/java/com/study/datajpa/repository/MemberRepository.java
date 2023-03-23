package com.study.datajpa.repository;

import com.study.datajpa.dto.MemberDto;
import com.study.datajpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

}
