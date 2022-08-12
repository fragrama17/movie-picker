package org.picker.movies;

import lombok.Value;
import org.picker.movies.model.Movie;
import org.jeasy.random.EasyRandom;
import org.springframework.stereotype.Component;

import java.util.List;

@Value
@Component
public class MoviesGenerator {
    EasyRandom easyRandom;

    public MoviesGenerator(EasyRandom easyRandom) {
        this.easyRandom = easyRandom;
    }

    public List<Movie> generateMovies(int howMany) {
        return easyRandom.objects(Movie.class, howMany).toList();
    }
}
