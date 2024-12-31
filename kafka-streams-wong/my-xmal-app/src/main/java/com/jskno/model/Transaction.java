package com.jskno.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

@Builder
public record Transaction(
        String firstName,
        String lastName,
        String customerId,
        String creditCardNumber,
        String itemPurchased,
        Integer quantity,
        BigDecimal price,
        OffsetDateTime purchasedDate,
        String zipCode,
        String department) {

    private static final String MASKING = "XXXX-XXXX-XXXX-";

    public static Transaction from(Transaction transaction) {
        return new Transaction(transaction.firstName, transaction.lastName(), transaction.customerId(),
                transaction.creditCardNumber(), transaction.itemPurchased(), transaction.quantity(),
                transaction.price(), transaction.purchasedDate(), transaction.zipCode(), transaction.department());
    }


    public static Transaction fromMaskingCreditCard(Transaction transaction) {
        return new Transaction(transaction.firstName, transaction.lastName(), transaction.customerId(),
                maskCreditCardNumber(transaction.creditCardNumber()), transaction.itemPurchased(), transaction.quantity(),
                transaction.price(), transaction.purchasedDate(), transaction.zipCode(), transaction.department());
    }

    public static String maskCreditCardNumber(String creditCardNumber) {
        Objects.requireNonNull(creditCardNumber, "CreditCardNumber must not be null");
        String[] parts = creditCardNumber.split("-");
        if (parts.length < 4) {
            return "xxxx";
        } else {
            String last4Digits = parts[3];
            return MASKING + last4Digits;
        }
    }
}
