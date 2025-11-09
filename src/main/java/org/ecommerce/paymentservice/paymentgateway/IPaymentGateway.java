package org.ecommerce.paymentservice.paymentgateway;

/**
 * Interface representing a payment gateway.
 */
public interface IPaymentGateway {
    String generatePaymentLink();
}
