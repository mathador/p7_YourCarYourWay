package com.ycyw.payment.service;

import com.ycyw.payment.dto.PaymentRequest;
import com.ycyw.payment.dto.PaymentResponse;

public interface StripeService {
    PaymentResponse processPayment(PaymentRequest request);
}
