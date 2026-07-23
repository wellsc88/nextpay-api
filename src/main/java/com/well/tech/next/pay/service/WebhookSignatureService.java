package com.well.tech.next.pay.service;

import com.well.tech.next.pay.config.WebhookProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookSignatureService {

    private static final long TIMESTAMP_TOLERANCE_SECONDS = 300;
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final WebhookProperties webhookProperties;

    public boolean isTimestampValid(String timestamp) {
        try {
            long webhookTimestamp = Long.parseLong(timestamp);

            long currentTimestamp = Instant.now().getEpochSecond();

            long difference = Math.abs(
                    currentTimestamp - webhookTimestamp
            );

            boolean valid =
                    difference <= TIMESTAMP_TOLERANCE_SECONDS;

            if (!valid) {
                log.warn(
                        "Invalid webhook timestamp. Difference: {} seconds",
                        difference
                );
            }

            return valid;

        } catch (NumberFormatException exception) {

            log.warn(
                    "Invalid webhook timestamp format: {}",
                    timestamp
            );

            return false;
        }
    }

    public boolean isValid(
            String payload,
            String timestamp,
            String signature
    ) {
        String signedPayload =
                timestamp + "." + payload;

        String expectedSignature =
                generateSignature(signedPayload);

        boolean valid = MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                signature.getBytes(StandardCharsets.UTF_8)
        );

        if (valid) {
            log.info(
                    "Webhook signature validated successfully. Timestamp: {}",
                    timestamp
            );
        } else {
            log.warn(
                    "Invalid webhook signature. Timestamp: {}",
                    timestamp
            );
        }

        return valid;
    }

    public String generateSignature(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);

            SecretKeySpec secretKey =
                    new SecretKeySpec(
                            webhookProperties.decodedSecret(),
                            HMAC_ALGORITHM
                    );

            mac.init(secretKey);

            byte[] hash = mac.doFinal(
                    payload.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (GeneralSecurityException exception) {

            log.error(
                    "Failed to generate webhook signature",
                    exception
            );

            throw new IllegalStateException(
                    "Failed to generate webhook signature",
                    exception
            );
        }
    }
}