package org.ecommerce.paymentservice.services;

import org.ecommerce.paymentservice.paymentgateway.IPaymentGateway;
import org.ecommerce.paymentservice.paymentgateway.PaymentGatewayChooserStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements IPaymentService {

    @Autowired
    private PaymentGatewayChooserStrategy paymentGatewayChooserStrategy;

    String getPaymentLink() {
        IPaymentGateway paymentGateway = paymentGatewayChooserStrategy.getBestPerformingPaymentGateway();
        return paymentGateway.generatePaymentLink();
    }
}
