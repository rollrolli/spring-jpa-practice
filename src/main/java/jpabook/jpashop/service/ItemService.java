package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    // 변경 감지를 통해 업데이트 하는 방법
    // => Service 계층에서 영속 상태의 엔티티를 조회하고, 엔티티의 데이터를 직접 변경한다.
    // => 트랜잭션 커밋 시점에 변경 감지가 실행된다.
    @Transactional
    public Item updateItem(UpdateItemDto updateItemDto) {
        Item findItem = itemRepository.findOne(updateItemDto.getItemId()); // 영속 상태로 만듦

//        findItem.setName(param .getName());        setter 쓰지 말자

        findItem.changeName(updateItemDto.getName());
        findItem.changePrice(updateItemDto.getPrice());
        findItem.changeStockQuantity(updateItemDto.getStockQuantity());

        return findItem;
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

}
