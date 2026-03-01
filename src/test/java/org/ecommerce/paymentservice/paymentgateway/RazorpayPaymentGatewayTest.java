package org.ecommerce.paymentservice.paymentgateway;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RazorpayPaymentGateway Unit Tests")
class RazorpayPaymentGatewayTest {

    @Mock
    private RazorpayClient razorpayClient;

    @InjectMocks
    private RazorpayPaymentGateway razorpayPaymentGateway;

    // -- test fixtures --
    private static final Double AMOUNT = 2000.0;
    private static final String ORDER_ID = "ORD-001";
    private static final String PHONE = "+919876543210";
    private static final String NAME = "Test User";
    private static final String EMAIL = "test@example.com";
    private static final String SHORT_URL = "https://rzp.io/i/testlink";

    @Nested
    @DisplayName("generatePaymentLink")
    class GeneratePaymentLink {

        @BeforeEach
        void setUp() {
            // RazorpayClient has a public field `paymentLink` of type PaymentLinkClient.
            // We need to mock it to intercept the `create` call.
            var paymentLinkClient = mock(com.razorpay.PaymentLinkClient.class);
            ReflectionTestUtils.setField(razorpayClient, "paymentLink", paymentLinkClient);
        }

        @Test
        @DisplayName("should return short_url from the Razorpay response")
        void shouldReturnShortUrl() throws Exception {
            // Arrange
            JSONObject responseJson = new JSONObject();
            responseJson.put("short_url", SHORT_URL);
            PaymentLink paymentLink = mock(PaymentLink.class);
            given(paymentLink.get("short_url")).willReturn(SHORT_URL);
            given(razorpayClient.paymentLink.create(any(JSONObject.class))).willReturn(paymentLink);

            // Act
            String result = razorpayPaymentGateway.generatePaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL);

            // Assert
            assertThat(result).isEqualTo(SHORT_URL);
        }

        @Test
        @DisplayName("should build request with correct amount and currency")
        void shouldBuildRequestWithCorrectAmountAndCurrency() throws Exception {
            // Arrange
            PaymentLink paymentLink = mock(PaymentLink.class);
            given(paymentLink.get("short_url")).willReturn(SHORT_URL);
            ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
            given(razorpayClient.paymentLink.create(captor.capture())).willReturn(paymentLink);

            // Act
            razorpayPaymentGateway.generatePaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL);

            // Assert
            JSONObject captured = captor.getValue();
            assertThat(captured.getDouble("amount")).isEqualTo(AMOUNT);
            assertThat(captured.getString("currency")).isEqualTo("INR");
        }

        @Test
        @DisplayName("should include customer details in the request")
        void shouldIncludeCustomerDetails() throws Exception {
            // Arrange
            PaymentLink paymentLink = mock(PaymentLink.class);
            given(paymentLink.get("short_url")).willReturn(SHORT_URL);
            ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
            given(razorpayClient.paymentLink.create(captor.capture())).willReturn(paymentLink);

            // Act
            razorpayPaymentGateway.generatePaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL);

            // Assert
            JSONObject captured = captor.getValue();
            JSONObject customer = captured.getJSONObject("customer");
            assertThat(customer).isNotNull();
            // Note: source maps phone→"name" and name→"contact" (potential bug in source, but we test actual behavior)
            assertThat(customer.getString("name")).isEqualTo(PHONE);
            assertThat(customer.getString("contact")).isEqualTo(NAME);
            assertThat(customer.getString("email")).isEqualTo(EMAIL);
        }

        @Test
        @DisplayName("should include reference_id and description with orderId")
        void shouldIncludeReferenceIdAndDescription() throws Exception {
            // Arrange
            PaymentLink paymentLink = mock(PaymentLink.class);
            given(paymentLink.get("short_url")).willReturn(SHORT_URL);
            ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
            given(razorpayClient.paymentLink.create(captor.capture())).willReturn(paymentLink);

            // Act
            razorpayPaymentGateway.generatePaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL);

            // Assert
            JSONObject captured = captor.getValue();
            assertThat(captured.getString("reference_id")).isEqualTo(ORDER_ID);
            assertThat(captured.getString("description")).contains(ORDER_ID);
        }

        @Test
        @DisplayName("should include notification and callback settings")
        void shouldIncludeNotificationAndCallbackSettings() throws Exception {
            // Arrange
            PaymentLink paymentLink = mock(PaymentLink.class);
            given(paymentLink.get("short_url")).willReturn(SHORT_URL);
            ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
            given(razorpayClient.paymentLink.create(captor.capture())).willReturn(paymentLink);

            // Act
            razorpayPaymentGateway.generatePaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL);

            // Assert
            JSONObject captured = captor.getValue();
            JSONObject notify = captured.getJSONObject("notify");
            assertThat(notify.getBoolean("sms")).isTrue();
            assertThat(notify.getBoolean("email")).isTrue();
            assertThat(captured.getBoolean("reminder_enable")).isTrue();
            assertThat(captured.getString("callback_url")).isEqualTo("https://example-callback-url.com/");
            assertThat(captured.getString("callback_method")).isEqualTo("get");
        }

        @Test
        @DisplayName("should wrap RazorpayException in RuntimeException")
        void shouldWrapRazorpayExceptionInRuntimeException() throws Exception {
            // Arrange
            given(razorpayClient.paymentLink.create(any(JSONObject.class)))
                    .willThrow(new RazorpayException("API error"));

            // Act & Assert
            assertThatThrownBy(() -> razorpayPaymentGateway.generatePaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Failed to create Razorpay payment link")
                    .hasCauseInstanceOf(RazorpayException.class);
        }

        @Test
        @DisplayName("should invoke razorpayClient.paymentLink.create exactly once")
        void shouldCallCreateExactlyOnce() throws Exception {
            // Arrange
            PaymentLink paymentLink = mock(PaymentLink.class);
            given(paymentLink.get("short_url")).willReturn(SHORT_URL);
            given(razorpayClient.paymentLink.create(any(JSONObject.class))).willReturn(paymentLink);

            // Act
            razorpayPaymentGateway.generatePaymentLink(AMOUNT, ORDER_ID, PHONE, NAME, EMAIL);

            // Assert
            verify(razorpayClient.paymentLink).create(any(JSONObject.class));
        }
    }
}

