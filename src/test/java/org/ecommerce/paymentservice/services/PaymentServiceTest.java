package org.ecommerce.paymentservice.services;

import org.ecommerce.paymentservice.paymentgateway.IPaymentGateway;
import org.ecommerce.paymentservice.paymentgateway.PaymentGatewayChooserStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
class PaymentServiceTest {

    @Mock
    private PaymentGatewayChooserStrategy paymentGatewayChooserStrategy;

    @Mock
    private IPaymentGateway paymentGateway;

    @InjectMocks
    private PaymentService paymentService;

    // -- test fixtures --
    private static final Double AMOUNT = 1500.0;
    private static final String ORDER_ID = "ORD-12345";
    private static final String PHONE = "+919876543210";
    private static final String NAME = "John Doe";
    private static final String EMAIL = "john@example.com";
    private static final String EXPECTED_LINK = "https://rzp.io/i/abc123";

    @Nested
    @DisplayName("getPaymentLink")
    class GetPaymentLink {

        @Test
        @DisplayName("should delegate to the gateway returned by the strategy and return the payment link")
        void shouldDelegateToGatewayAndReturnLink() {
            // Arrange
            given(paymentGatewayChooserStrategy.getBestPerformingPaymentGateway())
                    .willReturn(paymentGateway);
            given(paymentGateway.generatePaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL))
                    .willReturn(EXPECTED_LINK);

            // Act
            String result = paymentService.getPaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL);

            // Assert
            assertThat(result).isEqualTo(EXPECTED_LINK);
            verify(paymentGatewayChooserStrategy).getBestPerformingPaymentGateway();
            verify(paymentGateway).generatePaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL);
            verifyNoMoreInteractions(paymentGatewayChooserStrategy, paymentGateway);
        }

        @Test
        @DisplayName("should propagate exception when gateway throws RuntimeException")
        void shouldPropagateExceptionFromGateway() {
            // Arrange
            given(paymentGatewayChooserStrategy.getBestPerformingPaymentGateway())
                    .willReturn(paymentGateway);
            given(paymentGateway.generatePaymentLink(anyDouble(), anyString(), anyString(), anyString(), anyString()))
                    .willThrow(new RuntimeException("Gateway failure"));

            // Act & Assert
            assertThatThrownBy(() -> paymentService.getPaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Gateway failure");
        }

        @Test
        @DisplayName("should propagate exception when strategy throws")
        void shouldPropagateExceptionFromStrategy() {
            // Arrange
            given(paymentGatewayChooserStrategy.getBestPerformingPaymentGateway())
                    .willThrow(new RuntimeException("No gateway available"));

            // Act & Assert
            assertThatThrownBy(() -> paymentService.getPaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("No gateway available");
        }

        @Test
        @DisplayName("should pass exact parameters through to the gateway")
        void shouldPassExactParametersToGateway() {
            // Arrange
            Double specificAmount = 9999.99;
            String specificOrderId = "UNIQUE-ORDER-999";
            String specificPhone = "+1234567890";
            String specificName = "Jane Smith";
            String specificEmail = "jane@test.org";

            given(paymentGatewayChooserStrategy.getBestPerformingPaymentGateway())
                    .willReturn(paymentGateway);
            given(paymentGateway.generatePaymentLink(specificAmount, specificOrderId, specificPhone, specificName, specificEmail))
                    .willReturn("https://pay.example.com/unique");

            // Act
            String result = paymentService.getPaymentLink(specificAmount, specificOrderId, specificPhone, specificName, specificEmail);

            // Assert
            assertThat(result).isEqualTo("https://pay.example.com/unique");
            verify(paymentGateway).generatePaymentLink(specificAmount, specificOrderId, specificPhone, specificName, specificEmail);
        }
    }
}

