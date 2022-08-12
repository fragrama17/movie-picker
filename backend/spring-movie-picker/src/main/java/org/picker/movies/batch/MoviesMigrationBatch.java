package org.picker.movies.batch;

import lombok.extern.slf4j.Slf4j;
import org.picker.movies.model.Genre;
import org.picker.movies.model.Movie;
import org.picker.movies.model.MovieCSV;
import org.picker.movies.repository.MoviesRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
@EnableBatchProcessing
public class MoviesMigrationBatch {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final MoviesRepository moviesRepository;
    private final AtomicInteger counter = new AtomicInteger(0);

    public MoviesMigrationBatch(JobBuilderFactory jobs, StepBuilderFactory steps, MoviesRepository moviesRepository) {
        this.jobs = jobs;
        this.steps = steps;
        this.moviesRepository = moviesRepository;
    }

    @Bean
    public FlatFileItemReader<MovieCSV> itemReader() throws UnexpectedInputException, ParseException {
        FlatFileItemReader<MovieCSV> reader = new FlatFileItemReader<>();
        DefaultLineMapper<MovieCSV> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(",");
        BeanWrapperFieldSetMapper<MovieCSV> fieldSetMapper = new BeanWrapperFieldSetMapper<>();

        lineTokenizer.setNames("movieId", "title", "genres");
        fieldSetMapper.setTargetType(MovieCSV.class);

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setResource(new ClassPathResource("datasets/movies.csv"));
        reader.setLinesToSkip(1);
        reader.setLineMapper(defaultLineMapper);

        return reader;
    }

    @Bean
    public ItemWriter<MovieCSV> itemWriter() {
        // convert in Movie -> migration to db
        return movies -> {
            List<Movie> moviesDb = new ArrayList<>();
            movies.forEach(m -> moviesDb.add(Movie.builder()
                    .id(m.getMovieId())
                    .title(m.getTitle())
                    .genres(Arrays
                            .stream(m.getGenres().split("\\|"))
                            .toList())
                    .build()));

            moviesRepository.saveAll(moviesDb)
                    .subscribe(m -> log.debug("movie {} saved to db", m));

            int currentSize = counter.get();
            counter.set(currentSize + movies.size());
            log.debug("processed {} movies on 62423 total, output may not be ordered", counter.get());
        };
    }

    @Bean
    protected Step migrationStep(ItemReader<MovieCSV> reader, ItemWriter<MovieCSV> writer, TaskExecutor batchExecutor) {
        return steps
                .get("step1")
                .<MovieCSV, MovieCSV>chunk(100)//BEST PERFORMANCE FOR AMD RYZEN 5600G
                .reader(reader)
                .writer(writer)
                .taskExecutor(batchExecutor)
                .build();
    }

    @Bean(name = "moviesBatchJob")
    public Job job(Step migrationStep) {
        return jobs
                .get("job")
                .start(migrationStep)
                .build();
    }

    @Bean
    public TaskExecutor batchExecutor() {
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        poolTaskExecutor.setCorePoolSize(6); //BEST PERFORMANCE FOR AMD RYZEN 5600G
        poolTaskExecutor.setMaxPoolSize(12);

        return poolTaskExecutor;
    }
}
