package com.emsi.ebankbackend.exceptions;

public class BalanceNotSufficientException extends Exception {
    public BalanceNotSufficientException(String balanceNotSufficient) {
        super(balanceNotSufficient);
    }
}
