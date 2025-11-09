package org.ecommerce.paymentservice.controllers;

import org.ecommerce.paymentservice.dtos.InitiatePaymentRequestDto;
import org.ecommerce.paymentservice.services.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentControllers {

    private final IPaymentService paymentService;

    @Autowired
    public PaymentControllers(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping()
    public String initiatePayment(@RequestBody InitiatePaymentRequestDto request) {
        return "Payment initiated successfully.";
    }
}
