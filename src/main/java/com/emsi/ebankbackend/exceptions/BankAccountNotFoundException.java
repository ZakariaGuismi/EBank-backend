package com.emsi.ebankbackend.exceptions;

public class BankAccountNotFoundException extends Exception {
    public BankAccountNotFoundException(String bankAccountNotFound) {
        super(bankAccountNotFound);
    }
}
