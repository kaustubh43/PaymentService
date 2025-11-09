package org.ecommerce.paymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitiatePaymentRequestDto {
    Double amount;
    String orderId;
    String phoneNumber;
    String name;
    String email;
}
