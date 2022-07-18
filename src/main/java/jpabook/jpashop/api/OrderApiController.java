package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    // V1 : 엔티티를 조회해서 그대로 반환 (엔티티 변경 시 api 스펙까지 변해버리므로 절대 사용금지)
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) { // LAZY 로딩이니까 강제초기화 처리 필요
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    // V2 : 엔티티 조회 후 DTO 로 변환 (DTO 에서 지연로딩인 연관관계 모두 가져오므로 쿼리 다건 실행)
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o))
                .collect(toList());
        return collect;
    }

    // V3 : 페치 조인으로 쿼리 수 최적화 (모든 연관관계에 페치 조인을 걸어 한방 쿼리 실행. 하지만 중복 데이터 너무 많고 페이징 불가능)
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o))
                .collect(toList());
        return collect;
    }

    /**
     * hibernate.default_batch_fetch_size 옵션 사용의 장점
     *  - 쿼리 호출 수가 1 + N 에서 1 + 1 로 최적화 된다.
     *  - 조인보다 DB 데이터 전송량이 최적화된다.
     *    (Order 와 OrderItem 을 조인하면 Order 가 OrderItem 만큼 중복해서 조회된다. 이 방법은 각각 조회하므로 전송해야 할 중복 데이터가 없다.)
     *  - 페치 조인 방식과 비교해서 쿼리 호출 수가 약간 증가하지만, DB 데이터 전송량이 감소한다.
     *  - 컬렉션 페치 조인은 페이징이 불가능하지만 이 방법은 페이징이 가능하다.
     * => 결론 : ToOne 관계는 페치 조인해도 페이징에 영향을 주지 않는다. 따라서 ToOne 관계는 페치 조인으로 쿼리 수를 줄여서 해결하고,
     *          나머지는 hibernate.default_batch_fetch_size 로 최적화 하자.
     */
    // V3.1 : 컬렉션 페이징과 한계 돌파 (ToOne 관계만 페치 조인하고 ToMany(컬렉션) 는 지연로딩 유지하면서 batch size 값 설정으로 최적화)
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); // ToOne 관계는 fetch join 으로 가져오자.
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o))
                .collect(toList());
        return collect;
    }

    // V4 : JPA 에서 DTO 를 직접 조회
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    // V5 : 컬렉션 조회 최적화 - 일대다 관계인 컬렉션은 IN 절을 활용해서 메모리에 미리 조회해서 최적화
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    // V6 : 플랫 데이터 최적화 - JOIN 결과를 그대로 조회 후 애플리케이션에서 원하는 모양으로 직접 변환
    @GetMapping("/api/v6/orders")
    public List<OrderFlatDto> ordersV6() {
        return orderQueryRepository.findAllByDto_flat();
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
//        private List<OrderItem> orderItems; // 이렇게 DTO 안에 엔티티 있으면 안됨!!!!!
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
//            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
//            this.orderItemDtos = order.getOrderItems();
            this.orderItems = order.getOrderItems().stream() // 페치 조인 안했을 때 orderItem 강제 초기화 (지연로딩이므로)
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }

    @Data
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName(); // 페치 조인 안했을 때 item 강제 초기화
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}

/*
권장 순서
1. 엔티티 조회 방식으로 우선 접근
    1. 페치조인으로 쿼리 수를 최적화 (xToOne)
    2. 컬렉션 최적화 (xToMany)
        1. 페이징 필요 -> hibernate.default_batch_fetch_size , @BatchSize 로 최적화
        2. 페이징 필요X -> 페치 조인 사용
2. 엔티티 조회 방식으로 해결이 안되면 DTO 조회 방식 사용
3. DTO 조회 방식으로 해결이 안되면 NativeSQL or 스프링 JdbcTemplate
*/
