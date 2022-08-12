package org.picker.movies;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class MoviePickerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviePickerApplication.class, args);
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Bean
    public EasyRandom easyRandom() {
        EasyRandomParameters parameters = new EasyRandomParameters()
                .collectionSizeRange(1, 20)
                .randomize(Integer.class, new IntegerRangeRandomizer(0, 1000000000));

        return new EasyRandom(parameters);
    }

}
