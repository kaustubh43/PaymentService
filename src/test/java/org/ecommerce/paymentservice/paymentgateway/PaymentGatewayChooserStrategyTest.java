package org.ecommerce.paymentservice.paymentgateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("PaymentGatewayChooserStrategy Unit Tests")
class PaymentGatewayChooserStrategyTest {

    @Test
    @DisplayName("getBestPerformingPaymentGateway should return the injected gateway")
    void shouldReturnInjectedGateway() {
        // Arrange
        IPaymentGateway mockGateway = mock(IPaymentGateway.class);
        PaymentGatewayChooserStrategy strategy = new PaymentGatewayChooserStrategy(mockGateway);

        // Act
        IPaymentGateway result = strategy.getBestPerformingPaymentGateway();

        // Assert
        assertThat(result).isSameAs(mockGateway);
    }

    @Test
    @DisplayName("getBestPerformingPaymentGateway should return the same instance on multiple calls")
    void shouldReturnSameInstanceEveryTime() {
        // Arrange
        IPaymentGateway mockGateway = mock(IPaymentGateway.class);
        PaymentGatewayChooserStrategy strategy = new PaymentGatewayChooserStrategy(mockGateway);

        // Act
        IPaymentGateway first = strategy.getBestPerformingPaymentGateway();
        IPaymentGateway second = strategy.getBestPerformingPaymentGateway();

        // Assert
        assertThat(first).isSameAs(second);
    }
}

