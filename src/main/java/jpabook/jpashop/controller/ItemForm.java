package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.ItemCategory;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class ItemForm {

    private Long id;

    private ItemCategory itemCategory;

    private String name;
    private int price;
    private int stockQuantity;

    // BOOK
    private String author;
    private String isbn;

    // ALBUM
    private String artist;
    private String etc;

    // MOVIE
    private String director;

}
