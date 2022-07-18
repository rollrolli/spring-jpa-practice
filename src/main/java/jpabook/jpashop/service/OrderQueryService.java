package jpabook.jpashop.service;

import jpabook.jpashop.api.OrderApiController;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// OrderService : 핵심 비즈니스 로직
// OrderQueryService : 화면이나 API 에 맞춘 서비스 (주로 읽기 전용 트랜잭션 사용)

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    // spring.jpa.open-in-view 옵션을 false 로 할 경우 컨트롤러에서 지연로딩 강제초기화를 할 수 없기 때문에
    // 쿼리를 위한 별도의 서비스를 만들어서 지연로딩 강제초기화 로직을 넣는 것이 좋다.

    // 이렇게 컨트롤러에서 호출할 메소드를 만들어줘야 한다. 옮기는 것은 생략...
    public List<Order> ordersV1() {
        return null;
    }

    public List<OrderDto> ordersV2() {
        return null;
    }

    static class OrderDto {

    }
}