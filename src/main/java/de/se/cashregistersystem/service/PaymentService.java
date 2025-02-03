// PaymentService.java
package de.se.cashregistersystem.service;

import java.util.UUID;

public abstract class PaymentService {

    public UUID processPayment(String orderId) {
        String accessToken = authenticate();
        return verifyPayment(orderId, accessToken);
    }

    protected abstract String authenticate();

    protected abstract UUID verifyPayment(String orderId, String accessToken);
}