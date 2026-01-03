package org.ecommerce.paymentservice.paymentgateway;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.model.Price;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link IPaymentGateway} for Stripe payment Gateway.
 * <p>
 * This component creates a Stripe payment link using the Stripe Java SDK.
 * In case of an error from the SDK, the implementation rethrows an unchecked
 * exception to let higher-level code handle the failure.
 */
@Component
public class StripePaymentGateway implements IPaymentGateway{

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @Override
    public String generatePaymentLink(Double amount, String orderId, String phoneNumber, String name, String email) {
        try {
            Stripe.apiKey = stripeApiKey;

            PaymentLinkCreateParams params =
                    PaymentLinkCreateParams.builder()
                            .addLineItem(
                                    PaymentLinkCreateParams.LineItem.builder()
                                            .setPrice(createPrice(amount.longValue()).getId())
                                            .setQuantity(1L)
                                            .build()
                            )
                            .build();
            PaymentLink paymentLink = PaymentLink.create(params);
            return paymentLink.getUrl();
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create Stripe payment link", e);
        }
    }

    /**
     * Create Price Object in Stripe
     * @param amount in paise
     * @return Price object
     */
    public Price createPrice(Long amount) {
        try {
            PriceCreateParams params = PriceCreateParams.builder()
                    .setCurrency("INR")
                    .setUnitAmount(amount)
                    .setRecurring(
                            PriceCreateParams.Recurring.builder()
                                    .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                    .build()
                    )
                    .setProductData(
                            PriceCreateParams.ProductData.builder().setName("Gold Plan").build()
                    )
                    .build();
            return Price.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create Price: " + e);
        }
    }
}
