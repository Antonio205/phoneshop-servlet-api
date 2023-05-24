package com.es.phoneshop.model.cart;

public class CartQuantityValidator {

    private static CartQuantityValidator instance;

    private CartQuantityValidator() {

    }

    public static synchronized CartQuantityValidator getInstance() {
        if (instance == null) {
            instance = new CartQuantityValidator();
        }
        return instance;
    }

    public boolean validateQuantityFormat(String quantityInput) {
        String regex = "\\d+";

        return quantityInput.matches(regex);
    }
}
