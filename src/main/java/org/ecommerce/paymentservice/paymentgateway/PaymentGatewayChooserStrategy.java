package org.ecommerce.paymentservice.paymentgateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Strategy class to choose the best performing payment gateway.
 * Strategy pattern can be applied here to select different algorithms for choosing the best gateway based on various criteria.
 * Currently, it always selects Razorpay as the best performing payment gateway.
 */
@Component
public class PaymentGatewayChooserStrategy {

    private IPaymentGateway paymentGateway;

    @Autowired
    public PaymentGatewayChooserStrategy(@Qualifier("razorpayPaymentGateway") IPaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    /**
        Returns the best performing payment gateway.
        Currently, it always returns Razorpay as the best performing payment gateway.
     */
    public IPaymentGateway getBestPerformingPaymentGateway() {
        return paymentGateway;
    }
}
