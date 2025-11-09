package org.ecommerce.paymentservice.paymentgateway;

import org.springframework.stereotype.Component;

/**
 *    Implementation of IPaymentGateway for Razorpay.
 */
@Component
public class RazorpayPaymentGateway implements IPaymentGateway {
    @Override
    public String generatePaymentLink() {
        return "";
    }
}
