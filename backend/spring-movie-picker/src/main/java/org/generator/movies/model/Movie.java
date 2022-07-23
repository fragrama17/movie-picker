package org.generator.movies.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@AllArgsConstructor
public class Movie {
    String id;
    String title;
    List<Genre> genres;
    LocalDate releaseDate;
    int boxOffice;
    String director;
    List<String> cast;
}
