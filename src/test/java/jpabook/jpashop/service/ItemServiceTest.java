package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemServiceTest {

    @Autowired ItemService itemService;
    @Autowired ItemRepository itemRepository;
    @Autowired EntityManager em;

    @Test
    public void 상품_저장() throws Exception {
        // given
        Item album = new Album();
        album.setName("album1");
        itemService.saveItem(album);

        // when
        List<Item> items = itemService.findItems();

        // then
        assertThat(album).isIn(items);

    }

    @Test
    public void 상품_하나_꺼내기() throws Exception {
        // given
        Item album = new Album();
        album.setName("album1");
        itemService.saveItem(album);

        // when
        Item savedItem = itemService.findOne(album.getId());

        // then
        assertThat(album).isEqualTo(savedItem);

    }
}