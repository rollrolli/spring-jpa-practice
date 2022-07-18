package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.*;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.UpdateItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

//    @ModelAttribute(name = "itemCategories")
//    public List<ItemCategory> itemCategory() {
//        List<ItemCategory> itemCategories = new ArrayList<>();
//
//        for (ItemCategory category : ItemCategory.values()) {
//            itemCategories.add(category);
//        }
//        return itemCategories;
//    }


    @GetMapping("/items/new")
    public String createForm(Model model) {
        ItemForm form = new ItemForm();
        model.addAttribute("form", form);
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(ItemForm form) {

        Item item;

        if (form.getItemCategory() == ItemCategory.BOOK) {
            item = Book.createBook(form.getName()
                    , form.getPrice()
                    , form.getStockQuantity()
                    , form.getAuthor()
                    , form.getIsbn());
            itemService.saveItem(item);
        } else if (form.getItemCategory() == ItemCategory.ALBUM) {
            item = Album.createAlbum(form.getName()
                    , form.getPrice()
                    , form.getStockQuantity()
                    , form.getArtist()
                    , form.getEtc());
            itemService.saveItem(item);
        } else if (form.getItemCategory() == ItemCategory.MOVIE) {
            item = Movie.createMovie(form.getName()
                    , form.getPrice()
                    , form.getStockQuantity()
                    , form.getDirector());
            itemService.saveItem(item);
        }
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model) {

        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);

        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Item item = itemService.findOne(itemId);

        ItemCategory category;
        if (item instanceof Book) {
            category = ItemCategory.BOOK;
        } else if (item instanceof Album) {
            category = ItemCategory.ALBUM;
        } else {
            category = ItemCategory.MOVIE;
        }

        ItemForm form  = new ItemForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setItemCategory(category);
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());

        model.addAttribute("form", form);

        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") ItemForm form) {

//        Item item;
//
//        log.info("itemCategory = {}", form.getItemCategory());
//
//        if (form.getItemCategory() == ItemCategory.ALBUM) {
//            item = new Album();
//        } else if (form.getItemCategory() == ItemCategory.BOOK) {
//            item = new Book();
//        } else {
//            item = new Movie();
//        }
//
//        item.setId(form.getId());
//        item.setName(form.getName());
//        item.setPrice(form.getPrice());
//        item.setStockQuantity(form.getStockQuantity());
//
//        log.info("item = {}", item.getId() );
//        itemService.saveItem(item);

        // 어설프게 컨트롤러 레이어에서 엔티티를 만들지 말자

        UpdateItemDto updateItemDto = new UpdateItemDto(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
        itemService.updateItem(updateItemDto);

        return "redirect:/items";
    }
}
