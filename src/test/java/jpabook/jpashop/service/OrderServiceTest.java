package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired MemberService memberService;
    @Autowired ItemService itemService;

    public Member createMember() {
        Member member = new Member();
        member.setName("sora");
        member.setAddress(new Address("Seoul", "Anam", "47"));
        memberService.join(member);
        return member;
    }

    public Item createAlbum(String name, int price, int stockQuantity) {
        Item album = new Album();
        album.setName(name);
        album.setPrice(price);
        album.setStockQuantity(stockQuantity);
        itemService.saveItem(album);
        return album;
    }

    @Test
    public void 상품주문() throws Exception {
        // given
        Member member = createMember();
        Item album = createAlbum("aaa", 10000, 100);

        // when
        Long orderId = orderService.order(member.getId(), album.getId(), 10);

        // then
        Order order = orderService.findOne(orderId);
        assertThat(member.getOrders()).contains(orderService.findOne(orderId));
        assertThat(album.getStockQuantity()).isEqualTo(90);
        assertThat(order.getOrderItems().size()).isEqualTo(1);
        assertThat(order.getOrderItems().get(0).getItem()).isEqualTo(album);

        assertEquals("상품 주문 시 상태는 ORDER 이다.", OrderStatus.ORDER, order.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, order.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.", 10000 * 10, order.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.", 100 - 10, album.getStockQuantity());

    }

    @Test
    public void 주문취소() throws Exception {
        // given
        Member member = createMember();
        Item album = createAlbum("aaa", 10000, 100);

        Long orderId = orderService.order(member.getId(), album.getId(), 10);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order order = orderService.findOne(orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertEquals("주문 취소 후 재고가 돌아왔는지 확인한다.", 100, album.getStockQuantity());
    }

    @Test
//    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {
        // given
        Member member = createMember();
        Item album = createAlbum("aaa", 10000, 100);

        // when
//        Long orderId = orderService.order(member.getId(), album.getId(), 110);

        // then
        assertThrows(NotEnoughStockException.class, () -> {
            Long orderId = orderService.order(member.getId(), album.getId(), 110);
        });
    }
}