package com.es.phoneshop.validator;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class OrderDateValidator {

    private static OrderDateValidator instance;

    private OrderDateValidator() {

    }

    public static synchronized OrderDateValidator getInstance() {
        if (instance == null) {
            instance = new OrderDateValidator();
        }
        return instance;
    }

    public boolean validateDateFormat(String dateInput) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDate = LocalDate.parse(dateInput, formatter);

        return parsedDate.isAfter(currentDate);

    }
}
