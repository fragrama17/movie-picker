package org.picker.movies.batch;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.picker.movies.model.Genre;
import org.picker.movies.model.Movie;
import org.picker.movies.model.MovieCSV;
import org.picker.movies.repository.MoviesRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class MoviesMigrationBatch {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final MoviesRepository moviesRepository;
    private final AtomicInteger counter = new AtomicInteger(0);

    @Value("${batch.chunk-size}")
    private int chunkSize;
    @Value("${batch.thread-pool-core}")
    private int threadPoolCore;
    @Value("${batch.thread-pool-size}")
    private int threadPoolSize;

    public MoviesMigrationBatch(JobBuilderFactory jobs, StepBuilderFactory steps, MoviesRepository moviesRepository) {
        this.jobs = jobs;
        this.steps = steps;
        this.moviesRepository = moviesRepository;
    }

    @Bean
    public FlatFileItemReader<MovieCSV> reader() throws UnexpectedInputException, ParseException {
        FlatFileItemReader<MovieCSV> reader = new FlatFileItemReader<>();
        DefaultLineMapper<MovieCSV> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(",");
        BeanWrapperFieldSetMapper<MovieCSV> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        // Line Mapper configuration
        lineTokenizer.setNames("movieId", "title", "genres");
        fieldSetMapper.setTargetType(MovieCSV.class);
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        // Reader configuration
        reader.setLineMapper(defaultLineMapper);
        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource("datasets/movies.csv"));

        return reader;
    }

    @Bean
    public ItemWriter<MovieCSV> writer() {
        // convert from MovieCSV to Movie -> migration to db
        return movieCSVS -> {
            List<Movie> moviesDb = new ArrayList<>();
            movieCSVS.forEach(m ->
                    moviesDb.add(Movie.builder()
                            .id(Long.parseLong(m.getMovieId()))
                            .title(fixTitleBug(m.getTitle().split(YEAR_REGEX)[0].trim()))
                            .year(extractYear(m.getTitle()))
                            .genres(Arrays
                                    .stream(m.getGenres().split("\\|"))
                                    .map(this::extractGenres)
                                    .map(Genre::valueOf)
                                    .collect(Collectors.toList()))
                            .build()));

            moviesRepository.saveAll(moviesDb)
                    .subscribe();

            int currentSize = counter.get();
            counter.set(currentSize + movieCSVS.size());
            log.debug("processed {} movies on 62423 total, output may not be ordered", counter.get());
        };
    }

    @Bean
    protected Step migrationStep(ItemReader<MovieCSV> reader, ItemWriter<MovieCSV> writer, TaskExecutor batchExecutor) {
        return steps
                .get("migration-step")
                .<MovieCSV, MovieCSV>chunk(chunkSize)
                .reader(reader)
                .writer(writer)
                .taskExecutor(batchExecutor)
                .build();
    }

    @Bean(name = "moviesBatchJob")
    public Job job(Step migrationStep) {
        return jobs
                .get("movies-migration-job")
                .start(migrationStep)
                .build();
    }

    @Bean
    public TaskExecutor batchExecutor() {
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        poolTaskExecutor.setCorePoolSize(threadPoolCore);
        poolTaskExecutor.setMaxPoolSize(threadPoolSize);

        return poolTaskExecutor;
    }

    private static final String YEAR_REGEX = "(\\(\\d{4}\\))+";

    private Integer extractYear(String yearInTitle) {
        String year = "";
        if (yearInTitle.length() > 5)
            year = yearInTitle.substring(yearInTitle.length() - 5, yearInTitle.length() - 1);
        if (StringUtils.isNumeric(year))
            return Integer.parseInt(year);
        return null;
    }

    private String fixTitleBug(String title) {
        StringBuilder sb = new StringBuilder();
        if (title.endsWith(", The"))
            return sb.append("The ").append(title, 0, title.length() - 5).toString();
        return title;
    }

    private String extractGenres(String genres) {
        return genres
                .replace("-", "_")
                .replace("(", "")
                .replace(")", "")
                .replace(" ", "_")
                .toUpperCase();
    }
}
