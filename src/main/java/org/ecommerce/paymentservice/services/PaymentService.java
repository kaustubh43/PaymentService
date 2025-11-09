package org.ecommerce.paymentservice.services;

import org.ecommerce.paymentservice.paymentgateway.IPaymentGateway;
import org.ecommerce.paymentservice.paymentgateway.PaymentGatewayChooserStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service that provides payment-related operations.
 *
 */
@Service
public class PaymentService implements IPaymentService {

    @Autowired
    private PaymentGatewayChooserStrategy paymentGatewayChooserStrategy;

    /**
     * Generates a payment link using the best performing payment gateway.
     *
     * @return a non-null URL (as String) which can be used to complete the payment flow.
     * @throws RuntimeException if no payment gateway is available or the chosen gateway fails to
     *         generate a link. Implementations may throw more specific runtime exceptions.
     */
    public String getPaymentLink(Double amount,
                                 String orderId,
                                 String phoneNumber,
                                 String name,
                                 String email) {
        IPaymentGateway paymentGateway = paymentGatewayChooserStrategy.getBestPerformingPaymentGateway();
        return paymentGateway.generatePaymentLink(amount, orderId, phoneNumber, name, email);
    }
}
