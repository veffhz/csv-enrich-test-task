package ru.otus.csvparser.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import ru.otus.csvparser.client.ScoringClient;
import ru.otus.csvparser.exception.ClientProcessingException;
import ru.otus.csvparser.exception.ParseException;
import ru.otus.csvparser.exception.ParserIOException;
import ru.otus.csvparser.service.CsvParserService;
import ru.otus.csvparser.domain.ClientInfo;
import ru.otus.csvparser.domain.Response;

import javax.annotation.PostConstruct;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class CsvParserServiceImpl implements CsvParserService {

    private static final String[] HEADERS = {"CLIENT_NAME", "CONTRACT_DATE", "SCORING"};
    private static final String BASE_OUTPUT_CSV = "./build/outputCsv";
    private Path DIR = Paths.get(BASE_OUTPUT_CSV);

    private final ScoringClient scoringClient;
    private final String delimiter;
    private final AtomicInteger counter = new AtomicInteger(1);

    @Autowired
    public CsvParserServiceImpl(ScoringClient scoringClient, String delimiter) {
        this.scoringClient = scoringClient;
        this.delimiter = delimiter;
    }

    @PostConstruct
    void init() {
        try {
            if (Files.notExists(DIR)) {
                Files.createDirectory(DIR);
            }
            DIR = Files.createTempDirectory(DIR, "output_");
        } catch (IOException e) {
            throw new ParserIOException(e);
        }
    }

    @Override
    public Path processCsv(Path source) {
        List<ClientInfo> inputData = parse(source);
        Path outputData = createFile();

        append(convertToCSV(HEADERS), outputData);

        inputData.parallelStream().forEach(
                clientInfo -> {
                    Response response = postClientInfo(clientInfo);
                    log.info("received {}", response);
                    clientInfo.setScoringValue(response.getScoringValue());
                    clientInfo.setDescription(response.getDescription());
                }
        );

        for (ClientInfo clientInfo : inputData) {
            append(convertToCSV(clientInfo.toArray()), outputData);
        }

        return outputData;
    }

    private Response postClientInfo(ClientInfo clientInfo) {
        try {
            log.info("sending {}", clientInfo);
            ResponseEntity<Response> responseEntity = scoringClient.postClientInfo(clientInfo);
            return responseEntity.getBody();
        } catch (ClientProcessingException e) {
            log.warn(e.getMessage());
            return Response.failedResponse(e.getDescription());
        }
    }

    private void append(byte[] data, Path file) {
        try {
            Files.write(file, data, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new ParserIOException(e);
        }
    }

    private List<ClientInfo> parse(Path source) {
        List<ClientInfo> result = new ArrayList<>();
        List<String> dataSet;

        try {
            dataSet = Files.readAllLines(source);

            //skip first line
            for (int i = 1; i < dataSet.size(); i++) {
                String line = dataSet.get(i);

                if (log.isDebugEnabled()) {
                    log.debug("try to parse: {}", line);
                }

                String[] splitedText = line.split(delimiter);

                ClientInfo info = mapRow(splitedText);
                result.add(info);
            }
        } catch (IndexOutOfBoundsException e) {
            log.warn(e.getMessage());
            throw new ParseException(e);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ParserIOException(e);
        }
        return result;
    }

    private ClientInfo mapRow(String[] row) {
        String firstName = row[0];
        String middleName = row[1];
        String lastName = row[2];
        LocalDate contractDate = LocalDate.parse(row[3]);
        return new ClientInfo(firstName, middleName, lastName, contractDate);
    }

    private Path createFile() {
        try {
            Path file = DIR.resolve(generateCsvName());
            return Files.createFile(file);
        } catch (IOException e) {
            throw new ParserIOException(e);
        }
    }

    private String generateCsvName() {
        return String.format("output_%d.csv", getNewSuffix());
    }

    private byte[] convertToCSV(String... data) {
        return (String.join(delimiter, data) + "\n").getBytes(UTF_8);
    }

    private int getNewSuffix() {
        return counter.getAndIncrement();
    }
}
