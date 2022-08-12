package org.picker.movies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Movie {
    private String id;
    private String title;
//    private List<Genre> genres; FIXME
    private List<String> genres;
    private LocalDate releaseDate;
    private int boxOffice;
    private String director;
    private List<String> cast;
}
