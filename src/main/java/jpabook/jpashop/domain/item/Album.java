package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("A")
@Getter @Setter
public class Album extends Item {

    private String artist;
    private String etc;

    public static Album createAlbum(String name, int price, int stockQuantity, String artist, String etc) {

        Album album = new Album();

        album.setName(name);
        album.setPrice(price);
        album.setStockQuantity(stockQuantity);
        album.setArtist(artist);
        album.setEtc(etc);

        return album;
    }

}
