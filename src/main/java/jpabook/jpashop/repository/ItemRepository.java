package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        log.info("ItemRepository item.getId() = {}", item.getId());
        if (item.getId() == null) {
            log.info("em.persist");
            em.persist(item);
        } else {
            log.info("em.merge");
            em.merge(item); // update
            // em.merge -> 준영속 상태의 entity 를 영속 상태로 변경할 때 사용
            // 병합 기능을 사용하여 업데이트

            // merge 동작 방식
            // 1. merge 를 실행한다.
            // 2. 파라미터로 넘어온 준영속 엔티티의 식별자 값으로 1차 캐시에서 엔티티를 조회한다.
            //   2-1. 만약 1차 캐시에 엔티티가 없으면 데이터베이스에서 엔티티를 조회하고, 1차 캐시에 저장한다.
            // 3. 조회한 영속 엔티티에 파라미터로 넣은 객체의 값을 채워 넣는다.
            // 4. 영속 상태인 영속 엔티티를 반환한다.

            // 문제점 : 값을 세팅하지 않으면 병합 시 null 로 세팅이 됨.
            //   => 변경 감지 기능은 원하는 속성만 선택해서 변경할 수 있으므로, merge 가 아닌 변경 감지 기능을 사용하자!
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
