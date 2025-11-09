package org.ecommerce.paymentservice.services;

/*
    Interface for Payment Service.
 */
public interface IPaymentService {
    String getPaymentLink(Double amount, String orderId, String phoneNumber, String name, String email);
}
