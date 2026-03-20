package com.ycyw.payment.service.impl;

import com.ycyw.payment.dto.PaymentRequest;
import com.ycyw.payment.dto.PaymentResponse;
import com.ycyw.payment.service.StripeService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StripeMockServiceImpl implements StripeService {

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        // Log simulation
        System.out.println("Simulating call to Stripe API with description: " + request.getDescription());
        System.out.println("Processing amount: " + request.getAmount() + " " + request.getCurrency());
        
        // Return a mock response
        return new PaymentResponse(
            "SUCCESS",
            "pi_" + UUID.randomUUID().toString().substring(0, 10),
            "Payment processed successfully through Stripe Mock Service"
        );
    }
}
