package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

/**
 * xToOne (manyToOne, oneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order: all) {
            order.getMember().getName(); // LAZY 강제 초기화
            order.getDelivery().getAddress(); // LAZY 강제 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(o -> new SimpleOrderDto(o))
                // .map(SimpleOrderDto::new) 로 대체 가능
                .collect(toList()); // Collectors 를 static import 한 상태
    }

    // 엔티티를 DTO 로 변환하는 방법 - 장점:리포지토리 재사용성 good / 단점:쿼리 조회 시 성능 조금 더 나쁨
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        return orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());
    }

    // DTO 로 바로 조회하는 방법 - 장점:쿼리 조회 시 성능 조금 더 좋음 / 단점:리포지토리 재사용성 bad, api 스펙에 맞춘 코드가 리포지토리 계층으로...
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    /**
     * 쿼리 방식 선택 권장 순서
     * 1. 우선 엔티티를 DTO 로 변환하는 방법을 선택한다.
     * 2. 필요하면 fetch join 으로 성능을 최적화한다. -> 대부분의 성능 이슈가 해결된다.
     * 3. 그래도 안되면 DTO 로 직접 조회하는 방법을 선택한다.
     * 4. 최후의 방법은 JPA 가 제공하는 네이티브 SQL 이나 스프링 JDBC Template 을 사용해서 SQL 을 직접 사용한다.
     */

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName(); // 페치 조인 안했을 때 member 강제 초기화
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress(); // 페치 조인 안했을 때 delivery 강제 초기화
        }
    }
}
