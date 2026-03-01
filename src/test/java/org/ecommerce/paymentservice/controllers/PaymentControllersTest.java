package org.ecommerce.paymentservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ecommerce.paymentservice.dtos.InitiatePaymentRequestDto;
import org.ecommerce.paymentservice.services.IPaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentControllers.class)
@DisplayName("PaymentControllers Integration Tests")
class PaymentControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IPaymentService paymentService;

    // -- test fixtures --
    private static final Double AMOUNT = 5000.0;
    private static final String ORDER_ID = "ORD-TEST-001";
    private static final String PHONE = "+919876543210";
    private static final String NAME = "Test User";
    private static final String EMAIL = "test@example.com";
    private static final String PAYMENT_LINK = "https://rzp.io/i/testpay";

    private InitiatePaymentRequestDto buildRequest() {
        InitiatePaymentRequestDto dto = new InitiatePaymentRequestDto();
        dto.setAmount(AMOUNT);
        dto.setOrderId(ORDER_ID);
        dto.setPhoneNumber(PHONE);
        dto.setName(NAME);
        dto.setEmail(EMAIL);
        return dto;
    }

    @Nested
    @DisplayName("POST /payment")
    class InitiatePayment {

        @Test
        @DisplayName("should return 200 and the payment link on success")
        void shouldReturnPaymentLink() throws Exception {
            // Arrange
            given(paymentService.getPaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL))
                    .willReturn(PAYMENT_LINK);

            // Act & Assert
            mockMvc.perform(post("/payment")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(buildRequest())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(PAYMENT_LINK));
        }

        @Test
        @DisplayName("should delegate to IPaymentService with correct parameters")
        void shouldDelegateToServiceWithCorrectParams() throws Exception {
            // Arrange
            given(paymentService.getPaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL))
                    .willReturn(PAYMENT_LINK);

            // Act
            mockMvc.perform(post("/payment")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(buildRequest())))
                    .andExpect(status().isOk());

            // Assert
            verify(paymentService).getPaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL);
        }

        @Test
        @DisplayName("should propagate RuntimeException when service throws")
        void shouldPropagateExceptionWhenServiceThrows() throws Exception {
            // Arrange
            given(paymentService.getPaymentLink(anyDouble(), anyString(), anyString(), anyString(), anyString()))
                    .willThrow(new RuntimeException("Payment gateway error"));

            // Act & Assert — no @ExceptionHandler, so the RuntimeException propagates out
            assertThatThrownBy(() ->
                    mockMvc.perform(post("/payment")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(buildRequest())))
            ).rootCause().isInstanceOf(RuntimeException.class)
                    .hasMessage("Payment gateway error");
        }

        @Test
        @DisplayName("should return 400 for invalid request body")
        void shouldReturn400ForInvalidBody() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/payment")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 415 for unsupported media type")
        void shouldReturn415ForUnsupportedMediaType() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/payment")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("some text"))
                    .andExpect(status().isUnsupportedMediaType());
        }
    }
}

