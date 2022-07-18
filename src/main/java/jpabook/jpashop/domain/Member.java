package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

//    @Column(unique = true)
    @NotEmpty
    private String name;

    @JsonIgnore // 양방향 연관관계는 한쪽에 @JsonIgnore 필수
    @Embedded // 내장 타입
    private Address address;

    @JsonIgnore // 엔티티를 json 응답으로 보낼 때 제외하는 애노테이션. 양방향 연관관계가 걸렸을 때, 한쪽에 JsonIgnore 처리를 해야 무한루프 안 생김
    @OneToMany(mappedBy = "member") // order 엔티티의 Member 변수 필드명
    private List<Order> orders = new ArrayList<>();
}
