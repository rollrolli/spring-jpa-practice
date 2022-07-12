package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//@AllArgsConstructor // 모든 필드를 주입받는 생성자
@RequiredArgsConstructor // final 이 붙은 필드를 주입받는 생성자
@Transactional(readOnly = true) // public 메소드에 이 애노테이션이 걸림
public class MemberService {
    // field injection
    // 단점 : 테스트 시에 mock 객체 같은 걸로 바꿀 수가 없음
    // field 는 final 로 선언하는 것을 권장함. 주입 안하는 경우 compile 시점에 체크 가능
//    @Autowired
    private final MemberRepository memberRepository;

    // setter injection
    // 단점 : runtime 에 누가 바꿀 수 있음
//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // constructor injection
    // 이걸 권장!
//    @Autowired // 최신 버전 스프링에서는 생략 가능
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원 가입
      */
    @Transactional // 클래스 단위로 건 것보다 우선시 됨
    public Long join(Member member) {

        validateDuplicateMember(member); // 중복 이름을 가진 회원 검증

        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        String name = member.getName();
        List<Member> findMembers = memberRepository.findByName(name);
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 전체 조회
     */
//    @Transactional(readOnly = true)
    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

//    @Transactional(readOnly = true )
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    /**
     * 회원 수정
     */
    @Transactional
    public void update(Long memberId, String name) {
        Member member = memberRepository.findOne(memberId);
        member.setName(name);
    }
}
