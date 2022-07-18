package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    // 값 타입은 변경 불가능하게 설계해야 한다. (setter X) => 생성자에서만 값을 초기화 할 수 있도록 함

    // JPA 스펙 상 엔티티나 임베디드 타입은 디폴트 생성자를 public 또는 protected 로 설정해야 한다.
    // public 보다는 protected 가 그나마 안전하다.
    // 이유 : JPA 구현 라이브러리가 객체를 생성할 때 리플랙션 같은 기술을 사용할 수 있도록 해야 하기 때문
}
