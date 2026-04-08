package com.example.notificationservice_ms.notification;

final class NotificationMessageBuilder {

    private NotificationMessageBuilder() {
    }

    static String resolveTitle(NotificationType type) {
        return switch (type) {
            case ORDER_CREATED -> "Sifaris yaradildi";
            case ORDER_PREPARING -> "Sifaris hazirlanir";
            case ORDER_READY -> "Sifaris hazirdir";
            case ORDER_PICKED_UP -> "Sifaris goturuldu";
            case ORDER_DELIVERED -> "Sifaris catdirildi";
            case ORDER_CANCELLED -> "Sifaris legv edildi";
            case PAYMENT_SUCCESS -> "Odenis ugurludur";
            case PAYMENT_FAILED -> "Odenis ugursuzdur";
        };
    }

    static String resolveBody(NotificationType type) {
        return switch (type) {
            case ORDER_CREATED -> "Sizin sifarisiniz ugurla yaradildi.";
            case ORDER_PREPARING -> "Sifarisiniz hazirlanma merhelesindedir.";
            case ORDER_READY -> "Sifarisiniz teslim ucun hazirdir.";
            case ORDER_PICKED_UP -> "Sifarisiniz kuryer terefinden goturulub.";
            case ORDER_DELIVERED -> "Sifarisiniz size catdirildi.";
            case ORDER_CANCELLED -> "Sifarisiniz legv edildi.";
            case PAYMENT_SUCCESS -> "Odenis emeliyyati ugurla tamamlandi.";
            case PAYMENT_FAILED -> "Odenis emeliyyati ugursuz oldu.";
        };
    }
}
