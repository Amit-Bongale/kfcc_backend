package com.example.KFCC_Backend.Enum;

public enum PaymentModule {

    MEMBERSHIP(5000),
    TITLE(2000),
    IDCARD(500);

    private final int amount;

    PaymentModule(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

}