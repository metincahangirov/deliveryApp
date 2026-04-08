package com.example.chatservice_ms.order;

import com.example.chatservice_ms.common.ApiException;
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
        try {
            OrderChatContext response = restClient.get()
                    .uri(properties.baseUrl() + "/internal/orders/{orderId}/chat-context", orderId)
                    .header("Authorization", authorizationHeader)
                    .retrieve()
                    .body(OrderChatContext.class);

            if (response == null) {
                throw new ApiException(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "Order not found.");
            }
            return response;
        } catch (ApiException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "ORDER_SERVICE_ERROR", "Failed to fetch order details.");
        }
    }
}

