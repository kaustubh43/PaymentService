package org.ecommerce.paymentservice.paymentgateway;

/**
 * Interface representing a payment gateway.
 */
public interface IPaymentGateway {
    /**
     * Generate a payment link.
     * @param amount
     * @param orderId
     * @param phoneNumber
     * @param name
     * @param email
     * @return String URL of the payment link.
     */
    String generatePaymentLink(Double amount, String orderId, String phoneNumber, String name, String email);
}
