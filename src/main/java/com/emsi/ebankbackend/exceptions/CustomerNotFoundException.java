package com.emsi.ebankbackend.exceptions;

public class CustomerNotFoundException extends Exception {
    public CustomerNotFoundException(String customerNotFound) {
        super(customerNotFound);
    }
}
