package ru.otus.csvparser.domain;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@Setter
public class Response {

    public static final String NOT_FOUND_MSG = "не найден";

    enum Status {COMPLETED, FAILED, NOT_FOUND}

    private Status status;
    private String description;
    private Double scoringValue;

    private Response(Status status, String description) {
        this.status = status;
        this.description = description;
    }

    public static Response failedResponse(String description) {
        return new Response(Status.FAILED, description);
    }

    public String getDescription() {
        switch (status) {
            case NOT_FOUND:
                return NOT_FOUND_MSG;
            case FAILED:
                return Objects.nonNull(description)
                        ? description.toLowerCase() : null;
            default:
                return null;
        }
    }

    public Double getScoringValue() {
        return scoringValue;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", description='" + description + '\'' +
                ", scoringValue=" + scoringValue +
                '}';
    }
}
