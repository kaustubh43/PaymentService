package org.ecommerce.paymentservice.configurations;

import com.razorpay.RazorpayException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RazorpayConfiguration Unit Tests")
class RazorpayConfigurationTest {

    private RazorpayConfiguration config;

    @BeforeEach
    void setUp() {
        config = new RazorpayConfiguration();
    }

    @Nested
    @DisplayName("getRazorpayClient")
    class GetRazorpayClient {

        @Test
        @DisplayName("should create RazorpayClient when credentials are valid")
        void shouldCreateClientWithValidCredentials() throws RazorpayException {
            // Arrange
            ReflectionTestUtils.setField(config, "razorPayId", "rzp_test_1234567890");
            ReflectionTestUtils.setField(config, "razorPaySecret", "secret_test_abcdefg");

            // Act
            var client = config.getRazorpayClient();

            // Assert
            assertThat(client).isNotNull();
        }

        @ParameterizedTest(name = "keyId = \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("should throw IllegalStateException when keyId is null, empty, or blank")
        void shouldThrowWhenKeyIdIsInvalid(String keyId) {
            // Arrange
            ReflectionTestUtils.setField(config, "razorPayId", keyId);
            ReflectionTestUtils.setField(config, "razorPaySecret", "valid_secret_value");

            // Act & Assert
            assertThatThrownBy(() -> config.getRazorpayClient())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Razorpay credentials are missing or empty");
        }

        @ParameterizedTest(name = "keySecret = \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("should throw IllegalStateException when keySecret is null, empty, or blank")
        void shouldThrowWhenKeySecretIsInvalid(String keySecret) {
            // Arrange
            ReflectionTestUtils.setField(config, "razorPayId", "rzp_test_1234567890");
            ReflectionTestUtils.setField(config, "razorPaySecret", keySecret);

            // Act & Assert
            assertThatThrownBy(() -> config.getRazorpayClient())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Razorpay credentials are missing or empty");
        }

        @Test
        @DisplayName("should throw IllegalStateException when both credentials are null")
        void shouldThrowWhenBothCredentialsAreNull() {
            // Arrange
            ReflectionTestUtils.setField(config, "razorPayId", null);
            ReflectionTestUtils.setField(config, "razorPaySecret", null);

            // Act & Assert
            assertThatThrownBy(() -> config.getRazorpayClient())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("RAZORPAY_KEY_ID")
                    .hasMessageContaining("RAZORPAY_KEY_SECRET");
        }
    }
}

