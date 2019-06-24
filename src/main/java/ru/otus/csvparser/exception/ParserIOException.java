package ru.otus.csvparser.exception;

public class ParserIOException extends RuntimeException {

    private static final String MESSAGE = "IO error!";

    public ParserIOException() {
        super(MESSAGE);
    }

    public ParserIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserIOException(Throwable cause) {
        super(MESSAGE, cause);
    }

}
