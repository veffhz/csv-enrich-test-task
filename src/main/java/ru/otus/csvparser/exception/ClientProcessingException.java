package ru.otus.csvparser.exception;

import ru.otus.csvparser.domain.Response;

public class ClientProcessingException extends RuntimeException {

    private static final String MESSAGE = "Feign error!";
    private String description;

    public ClientProcessingException() {
        super(MESSAGE);
    }

    public ClientProcessingException(Response response, String reason) {
        super(String.format("%s %s", MESSAGE, reason));
        this.description = response.getDescription();
    }

    public String getDescription() {
        return description;
    }
}
