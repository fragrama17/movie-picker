package org.generator.movies.batch;

import lombok.extern.slf4j.Slf4j;
import org.generator.movies.model.MovieCSV;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

@Slf4j
public class MoviesRetrieverBatch {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private JobLauncher jobLauncher;

    @Bean
    public ItemReader<MovieCSV> itemReader() throws UnexpectedInputException, ParseException {
        FlatFileItemReader<MovieCSV> reader = new FlatFileItemReader<>();
        DefaultLineMapper<MovieCSV> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(",");
        BeanWrapperFieldSetMapper<MovieCSV> fieldSetMapper = new BeanWrapperFieldSetMapper<>();

        reader.setResource(new FileSystemResource("datasets/movies.csv"));
        reader.setLinesToSkip(1);

        lineTokenizer.setNames("movieId", "title", "genres");
        fieldSetMapper.setTargetType(MovieCSV.class);

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return reader;
    }

    @Bean
    public ItemWriter<List<MovieCSV>> itemWriter() {
        return movies -> movies.forEach(m -> log.debug(m.toString()));
    }

    @Bean
    protected Step step1(ItemReader<MovieCSV> reader, ItemWriter<List<MovieCSV>> writer) {
        return steps
                .get("step1")
                .<MovieCSV, List<MovieCSV>>chunk(10)
                .reader(reader)
                .writer(writer)
                .taskExecutor(new ThreadPoolTaskExecutor())
                .build();
    }

    @Bean(name = "firstBatchJob")
    public Job job(@Qualifier("step1") Step step1) {
        return jobs
                .get("firstBatchJob")
                .start(step1)
                .build();
    }
}
