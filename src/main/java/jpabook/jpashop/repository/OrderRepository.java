package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.QMember;
import jpabook.jpashop.domain.QOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static jpabook.jpashop.domain.QMember.*;
import static jpabook.jpashop.domain.QOrder.*;

@Repository
//@RequiredArgsConstructor
 public class OrderRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public OrderRepository(EntityManager em) {
        this.em = em;
        query = new JPAQueryFactory(em);
    }

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name = :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();

//        return em.createQuery("select o from Order o join o.member m" +
//                        "where o.status = :status and " +
//                        "and m.name like :name", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
////                .setFirstResult(100) // 100 부터 maxResults 만큼 가져온다 (페이징 처리)
//                .setMaxResults(1000) // 조회 개수 제한, 최대 1000건
//                .getResultList();
    }

    /**
     * JPA Criteria
     **/
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(criteria.toArray(new Predicate[criteria.size()]));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        // static import 해버리기!
//        QOrder order = QOrder.order;
//        QMember member = QMember.member;

        // constructor 에서 초기화 해버리기!
//        JPAQueryFactory query = new JPAQueryFactory(em);

        return query
                .select(order)
                .from(order)
                .join(order.member, member)
                .limit(1000)
                .where(statusEq(orderSearch.getOrderStatus()), memberNameLike(orderSearch, member))
                .fetch();
    }

    private BooleanExpression statusEq(OrderStatus statusCond) {
        if (statusCond == null) {
            return null;
        }
        return order.status.eq(statusCond);
    }

    private BooleanExpression memberNameLike(OrderSearch orderSearch, QMember member) {
        if (!StringUtils.hasText(orderSearch.getMemberName())) {
            return null;
        }
        return member.name.like("%"+orderSearch.getMemberName()+"%");
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class)
                .getResultList();
    }

    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o" +
                        // distinct 키워드를 넣으면 JPA 가 SQL 에도 distinct 추가해주고, 알아서 orderId 로 중복제거까지 해줌
                        // 일대일이나 다대일 관계는 중복 엔티티가 조회되지 않으므로 상관없지만, 일대다 일 때 사용하면 됨
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item i", Order.class)
                .setFirstResult(1)
                .setMaxResults(100)
                .getResultList();
        // distinct 사용 시 유의점 : 페이징 처리 안됨
        // 컬렉션 페치조인(일대다 연관관계 있는 조인)을 사용하면서 페이징 처리를 하면
        // 하이버네이트가 전체 조회 후 메모리에서 페이징 처리를 하기 때문에 OOM 발생 가능성이 있다.
        // 그러므로 쓰면 안된다.

        // 컬렉션 둘 이상에 페치조인을 쓰면 안된다. 데이터가 부정합하게 조회될 수 있다.

    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery("select o from Order o" +
                " join fetch o.member m" +
                        // xToOne 관계를 fetch join 하지 않으면
                        // default_batch_fetch_size 의 영향으로 각 연관관계마다 데이터를 가져오는 쿼리를 던진다.
                        // 그러므로 xToOne 은 fetch join 을 걸어주는 편이 더 성능이 좋다.
                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
