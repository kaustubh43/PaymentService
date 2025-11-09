package org.ecommerce.paymentservice.controllers;

import org.ecommerce.paymentservice.dtos.InitiatePaymentRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentControllers {

    @PostMapping()
    public String initiatePayment(@RequestBody InitiatePaymentRequestDto request) {
        return "Payment initiated successfully.";
    }
}
