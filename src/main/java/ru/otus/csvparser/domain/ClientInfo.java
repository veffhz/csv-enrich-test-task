package ru.otus.csvparser.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@RequiredArgsConstructor
public class ClientInfo {

    private final String firstName;
    private final String lastName;
    private final String middleName;

    @Getter
    private final LocalDate contractDate;

    private Double scoringValue;
    private String description;

    @JsonProperty("clientName")
    public String getFullName() {
        return String.format("%s %s %s", firstName.toUpperCase(),
                middleName.toUpperCase(), lastName.toUpperCase());
    }

    public String contractDate() {
        return String.valueOf(contractDate);
    }

    public void setScoringValue(Double scoringValue) {
        this.scoringValue = scoringValue;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] toArray() {
        return new String[] { getFullName(), contractDate(),
                Objects.nonNull(scoringValue) ? String.valueOf(scoringValue) : description };
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", contractDate=" + contractDate +
                '}';
    }
}
