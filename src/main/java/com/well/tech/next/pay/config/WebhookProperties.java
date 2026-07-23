package com.well.tech.next.pay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;

@ConfigurationProperties(prefix = "payment.webhook")
public record WebhookProperties(
        String secret
) {

    public byte[] decodedSecret() {
        return Base64.getDecoder().decode(secret);
    }
}