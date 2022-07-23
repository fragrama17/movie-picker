package org.generator.movies.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class MovieCSV {
    String id;
    String title;
    String genres;
}
