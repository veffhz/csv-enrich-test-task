package ru.otus.csvparser.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.codec.ErrorDecoder;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.otus.csvparser.client.ScoringClient;
import ru.otus.csvparser.service.CsvParserService;
import ru.otus.csvparser.domain.Response;
import ru.otus.csvparser.exception.ClientProcessingException;
import ru.otus.csvparser.service.impl.CsvParserServiceImpl;

import java.io.IOException;

@Configuration
@Slf4j
public class MainConfiguration {

    @Bean
    public CsvParserService csvParserService(ScoringClient client, @Value("${delimiter:,}") String delimiter) {
        return new CsvParserServiceImpl(client, delimiter);
    }

    @Bean
    public ErrorDecoder errorDecoder(ObjectMapper mapper) {
        return (methodKey, feignResponse) -> {
            if (feignResponse.status() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                try {
                    Response response = mapper.readValue(feignResponse.body().asReader(), Response.class);
                    return new ClientProcessingException(response, feignResponse.reason());
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            return FeignException.errorStatus(methodKey, feignResponse);
        };
    }
}
