package org.ecommerce.paymentservice.paymentgateway;

import org.springframework.stereotype.Component;

@Component
public class RazorpayPaymentGateway implements IPaymentGateway {
    @Override
    public String generatePaymentLink() {
        return "";
    }
}
