package org.ecommerce.paymentservice.controllers;

import org.ecommerce.paymentservice.dtos.InitiatePaymentRequestDto;
import org.ecommerce.paymentservice.services.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Payments
 */
@RestController
@RequestMapping("/payment")
public class PaymentControllers {

    private final IPaymentService paymentService;

    @Autowired
    public PaymentControllers(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Method for initiating payments
     * @param request : Dto for Initiating Payments
     * @return String : URL for the payment
     */
    @PostMapping
    public String initiatePayment(@RequestBody InitiatePaymentRequestDto request) {
        return paymentService.getPaymentLink(
                request.getAmount(),
                request.getOrderId(),
                request.getPhoneNumber(),
                request.getName(),
                request.getEmail()
        );
    }
}
