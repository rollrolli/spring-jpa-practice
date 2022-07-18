package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // [select m from Member m where m.name = :name] 라는 jpql 을 자동으로 짜줌
    List<Member> findByName(String name);
}
