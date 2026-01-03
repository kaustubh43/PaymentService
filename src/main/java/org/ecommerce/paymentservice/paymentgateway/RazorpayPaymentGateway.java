package org.ecommerce.paymentservice.paymentgateway;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link IPaymentGateway} for Razorpay.
 * <p>
 * This component creates a Razorpay payment link using the Razorpay Java SDK.
 * In case of an error from the SDK, the implementation rethrows an unchecked
 * exception to let higher-level code handle the failure.
 */
@Component
public class RazorpayPaymentGateway implements IPaymentGateway {
    @Autowired
    private RazorpayClient razorpayClient;

    @Override
    public String generatePaymentLink(Double amount, String orderId, String phoneNumber, String name, String email) {
        try {
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", amount);
            paymentLinkRequest.put("currency", "INR");
            paymentLinkRequest.put("accept_partial", true);
            paymentLinkRequest.put("first_min_partial_amount", 100);
            paymentLinkRequest.put("expire_by", 1767225599); // Hard coded to 17 Nov 2025. Todo: Make dynamic.
            paymentLinkRequest.put("reference_id", orderId);
            paymentLinkRequest.put("description", "Payment for policy no #" + orderId);
            JSONObject customer = new JSONObject();
            customer.put("name", phoneNumber);
            customer.put("contact", name);
            customer.put("email", email);
            paymentLinkRequest.put("customer", customer);
            JSONObject notify = new JSONObject();
            notify.put("sms", true);
            notify.put("email", true);
            paymentLinkRequest.put("notify", notify);
            paymentLinkRequest.put("reminder_enable", true);
            JSONObject notes = new JSONObject();
            notes.put("policy_name", "Life Insurance Policy");
            paymentLinkRequest.put("notes", notes);
            paymentLinkRequest.put("callback_url", "https://example-callback-url.com/");
            paymentLinkRequest.put("callback_method", "get");

            PaymentLink payment = razorpayClient.paymentLink.create(paymentLinkRequest);
            return payment.get("short_url").toString();
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create Razorpay payment link", e);
        }
    }
}
