package jpabook.jpashop.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class UpdateItemDto {

    private Long itemId; // Dto 는 id 가져도 되니까
    private String name;
    private int price;
    private int stockQuantity;

}
