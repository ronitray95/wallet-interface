package com.zolve;

import java.io.Serializable;

public class Transaction implements Serializable {
    String type;
    String timestamp;
    double amount;

    public Transaction(String type, String timestamp, double amount) {
        this.type = type;
        this.timestamp = timestamp;
        this.amount = amount;
    }
}

