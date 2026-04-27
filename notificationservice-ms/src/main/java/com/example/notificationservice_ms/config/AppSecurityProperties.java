package com.example.notificationservice_ms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param internalToken Boşdursa, {@code /api/auth/token} daxili header tələb etmir (yalnız inkişaf üçün).
 *                      Prod-da mütləq güclü dəyər təyin edin və {@code X-Internal-Token} header göndərin.
 */
@ConfigurationProperties(prefix = "app.security")
public record AppSecurityProperties(
        String internalToken
) {
}
