package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional // 이게 있어야 롤백됨
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    // JPA 의 영속성 컨텍스트는 commit 될 때 insert 문을 만들어서 DB 에 던진다.
    // 하지만 Test 는 기본적으로 commit 하지 않고 rollback 하기 때문에 insert 를 아예 하지 않는다.
    // ([영속성 컨텍스트 flush] == [영속성 컨텍스트의 변경 내용을 DB 에 반영하는 것] 을 안함)
    // commit 하게 만들려면 @Rollback(value = false) 를 사용하자.
    // rollback 이지만 insert 쿼리를 확인하고 싶으면 검증 전 em.flush(); 로 flush 를 강제하자.

    @Test
    @Rollback(value = false)
    public void 회원가입() throws Exception {
        // given
        Member memberA = new Member();
        memberA.setName("memberA");

        // when
        Long savedId = memberService.join(memberA);

        // then
        em.flush();
        assertEquals(memberA, memberService.findOne(savedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        // given
        Member memberA = new Member();
        memberA.setName("memberA");
        memberService.join(memberA);

        // when
        Member memberA2 = new Member();
        memberA2.setName("memberA");

        // then
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(memberA2);
        });
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외2() throws Exception {
        // given
        Member memberA = new Member();
        memberA.setName("memberA");
        memberService.join(memberA);

        // when
        Member memberA2 = new Member();
        memberA2.setName("memberA");
        memberService.join(memberA2);

        // then
//        fail("여기 도달하면 테스트가 실패했다는 뜻이다.");
        // junit4, 5 에서 사용 가능
    }
}