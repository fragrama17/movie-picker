package org.picker.movies.repository;

import org.picker.movies.model.Movie;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoviesRepository extends ReactiveMongoRepository<Movie, String> {
}
