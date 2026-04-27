package com.example.chatservice_ms.order;

import com.example.chatservice_ms.common.ApiException;
import com.example.chatservice_ms.common.UuidStrings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OrderServiceClient {

    private final RestClient restClient;
    private final OrderServiceProperties properties;

    public OrderServiceClient(RestClient restClient, OrderServiceProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public OrderChatContext getOrderChatContext(String orderId, String authorizationHeader) {
        String base = properties.baseUrl();
        if (base == null || base.isBlank()) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "ORDER_SERVICE_NOT_CONFIGURED",
                    "Configure chat.order-service.base-url.");
        }
        try {
            OrderChatContext response = restClient.get()
                    .uri(base + "/internal/orders/{orderId}/chat-context", orderId)
                    .header("Authorization", authorizationHeader)
                    .retrieve()
                    .body(OrderChatContext.class);

            if (response == null) {
                throw new ApiException(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "Order not found.");
            }
            return UuidStrings.normalizeOrderChatContext(response);
        } catch (ApiException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "ORDER_SERVICE_ERROR", "Failed to fetch order details.");
        }
    }
}

