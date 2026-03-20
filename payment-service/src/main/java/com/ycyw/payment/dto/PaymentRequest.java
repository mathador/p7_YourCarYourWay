package com.ycyw.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    @NotBlank(message = "Amount is required")
    private String amount; // In cents for Stripe

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Payment method ID is required")
    private String paymentMethodId;

    // Getters and Setters
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }
}
