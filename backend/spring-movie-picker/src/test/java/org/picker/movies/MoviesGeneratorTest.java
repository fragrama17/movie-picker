package org.picker.movies;

import com.github.javafaker.Faker;
import org.picker.movies.model.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MoviesGeneratorTest {

    @Autowired
    MoviesGenerator sut;

    @Test
    void faker() {
        Faker faker = new Faker(Locale.ITALY);
        System.out.println(faker.address().fullAddress());
        System.out.println(faker.music().instrument());
        System.out.println(faker.artist().name());
        System.out.println(faker.book().author());
        System.out.println(faker.commerce().department());
        System.out.println(faker.gameOfThrones().quote());
        System.out.println(faker.business().creditCardNumber());
        System.out.println(faker.business().creditCardExpiry());
    }

    @Test
    void generateMovie() {
        System.out.println(sut.generateMovies(1));
    }

    @Test
    void generateMovies() {
        assertThat(sut.generateMovies(100))
                .hasSize(100)
                .hasOnlyElementsOfType(Movie.class);
    }
}
