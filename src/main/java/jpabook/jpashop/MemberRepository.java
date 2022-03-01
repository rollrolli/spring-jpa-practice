package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
        // member 객체가 아닌 id 를 리턴하는 이유: 커맨드와 쿼리를 분리하라는 원칙에 따라
        // save 는 저장한다는 커맨드성이므로 사이드 이펙트 일어날 수 있어서 리턴값을 주지 않고 id 정도만 리턴하는 것.
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
