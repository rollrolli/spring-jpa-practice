package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("M")
@Getter @Setter
public class Movie extends Item {

    private String director;

    public static Movie createMovie(String name, int price, int stockQuantity, String director) {

        Movie movie = new Movie();

        movie.setName(name);
        movie.setPrice(price);
        movie.setStockQuantity(stockQuantity);
        movie.setDirector(director);

        return movie;
    }

}
