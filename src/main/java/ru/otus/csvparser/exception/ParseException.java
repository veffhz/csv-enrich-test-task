package ru.otus.csvparser.exception;

public class ParseException extends RuntimeException {

    private static final String MESSAGE = "Parsing error!";

    public ParseException() {
        super(MESSAGE);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(Throwable cause) {
        super(MESSAGE, cause);
    }

}
