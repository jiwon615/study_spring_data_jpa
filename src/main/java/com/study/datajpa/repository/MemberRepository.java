package com.study.datajpa.repository;

import com.study.datajpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Repository 넣지 않아도, interface로 되어만 있으면, 스프링부트가 자동으로 읽고,
 * 알아서 이 인터페이스의 구현첼르 다 만들어줌 (개발자가 구현체 만들지 않아도 됨)
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

   List<Member> findByUsername(String username); // 쿼리 메소드 기능
}
