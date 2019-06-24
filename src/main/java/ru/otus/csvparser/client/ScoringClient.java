package ru.otus.csvparser.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import ru.otus.csvparser.domain.ClientInfo;
import ru.otus.csvparser.domain.Response;

@FeignClient(name="external")
public interface ScoringClient {
    @PostMapping(value="/score", consumes= MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Response> postClientInfo(ClientInfo info);
}
