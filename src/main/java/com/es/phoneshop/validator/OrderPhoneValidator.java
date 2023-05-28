package com.es.phoneshop.validator;

public class OrderPhoneValidator {

    private static OrderPhoneValidator instance;

    private OrderPhoneValidator() {

    }

    public static synchronized OrderPhoneValidator getInstance() {
        if (instance == null) {
            instance = new OrderPhoneValidator();
        }
        return instance;
    }

    public boolean validatePhoneFormat(String phoneInput) {
        String regex = "^(\\+?375\\d{9}|80\\d{9})$";

        return phoneInput.matches(regex);
    }
}
