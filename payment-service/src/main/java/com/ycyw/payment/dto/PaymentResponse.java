package com.ycyw.payment.dto;

public class PaymentResponse {
    private String status;
    private String paymentId;
    private String message;

    public PaymentResponse(String status, String paymentId, String message) {
        this.status = status;
        this.paymentId = paymentId;
        this.message = message;
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
