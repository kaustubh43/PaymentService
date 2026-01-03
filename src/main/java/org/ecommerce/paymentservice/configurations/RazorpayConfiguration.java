package org.ecommerce.paymentservice.configurations;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class RazorpayConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RazorpayConfiguration.class);

    @Value("${razorpay.key-id}")
    private String razorPayId;

    @Value("${razorpay.key-secret}")
    private String razorPaySecret;

    @Bean
    public RazorpayClient getRazorpayClient() throws RazorpayException {
        validateCredentials();

        log.info("Creating RazorpayClient with key id={}", mask(razorPayId));
        return new RazorpayClient(razorPayId, razorPaySecret);
    }

    /**
     * Method to validate Razorpay credentials.
     */
    private void validateCredentials() {
        // Validate that credentials are present and not empty. If they are missing,
        // fail fast with a clear error message so it's obvious at startup/config time
        // instead of getting an opaque authentication error from the SDK later.
        if (razorPayId == null || razorPayId.isBlank() || razorPaySecret == null || razorPaySecret.isBlank()) {
            String visibleId = razorPayId == null ? "<null>" : mask(razorPayId);
            String msg = "Razorpay credentials are missing or empty. " +
                    "Set environment variables RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET or provide them in application.properties. " +
                    "Current razorpay.key-id=" + visibleId;
            log.error(msg);
            throw new IllegalStateException(msg);
        }
    }

    private String mask(String s) {
        if (s == null) return "";
        int visible = Math.min(4, s.length());
        String start = s.substring(0, visible);
        return start + "****";
    }
}
