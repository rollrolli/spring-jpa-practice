package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository
@RequiredArgsConstructor // final 선언한 EntityManager 주입받는 생성자
public class MemberRepository {

//    @PersistenceContext // 스프링부트에서는 @Autowired 로도 가져올 수 있음
    private final EntityManager em;

//    @PersistenceUnit // 요렇게 하면 emf 꺼내올 수 있음
//    private EntityManagerFactory emf;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() { // cmd + option + N : inline
        return em.createQuery("select m from Member m", Member.class) // JPQL
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
