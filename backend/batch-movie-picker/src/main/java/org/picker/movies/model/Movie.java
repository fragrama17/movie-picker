package org.picker.movies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("movies")
public class Movie {
    private String id;
    private String title;
    private int year;
    private List<Genre> genres;

//    NO DECENT DATA-SETS WITH THIS INFO :(
//    private int boxOffice;
//    private String director;
//    private List<String> cast;
}
