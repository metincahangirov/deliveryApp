# Chat Service (Order-Based Messaging)

Bu servis `user` və `courier` arasında yalnız `order` əsasında mesajlaşmanı təmin edən microservice-dir.  
Sərbəst (ümumi) chat yoxdur; hər mesaj mütləq bir sifariş (`order`) kontekstində göndərilir.

## 1) Service Overview

Chat Service-də əsas biznes qaydaları:

- `user` yalnız **öz order-i** üzrə mesaj yaza bilər.
- `courier` yalnız **özünə təyin olunmuş order** üzrə mesaj yaza bilər.
- Mesajlaşma yalnız eyni `order` daxilində `user` və `courier` arasında mümkündür.

## 2) Main Responsibilities

Servis aşağıdakı funksionallıqlara cavabdehdir:

- `order` əsaslı chat/messaging axınını idarə etmək
- `user` və `courier` arasında mesaj göndərmək
- order üzrə mesaj tarixçəsini qaytarmaq
- icazə (authorization) qaydalarını tətbiq etmək
- real-time çatdırılma üçün WebSocket dəstəyi vermək

## 3) Integration-Ready Architecture

Bu servis digər servislərlə inteqrasiya üçün aşağıdakı komponentlərlə işləməlidir:

- **Auth Service**: JWT/token doğrulaması, istifadəçi rolu (`USER`, `COURIER`) və `userId` çıxarışı
- **Order Service**: order mövcudluğu, order sahibi (`userId`) və təyin olunmuş `courierId` məlumatı
- **API Gateway** (opsional): route, auth forwarding, rate limiting
- **Notification Service** (opsional): push notification, unread counter və s.

### Tövsiyə olunan inteqrasiya axını

1. Client `Authorization: Bearer <token>` ilə request göndərir.  
2. Chat Service token-dən `subject(userId)` və `role` alır (və ya Gateway-dən doğrulanmış claim-ləri qəbul edir).  
3. Chat Service Order Service-dən `orderId` üçün order məlumatını alır.  
4. Qaydalar ödənərsə mesaj DB-yə yazılır.  
5. Mesaj WebSocket ilə qarşı tərəfə real-time ötürülür.

## 4) API Endpoints

Base path: `/api/chats`

### 4.1 POST `/api/chats/messages`

Order üzrə yeni mesaj göndərir.

#### Request Body

```json
{
  "orderId": "ORD-1001",
  "content": "Salam, sifariş yoldadır?"
}
```

#### Validation

- `orderId` boş ola bilməz
- `content` boş ola bilməz
- `content` max uzunluq (məs: 2000 simvol) limitini keçməməlidir

#### Business Rules

- Order mövcud olmalıdır
- Request atan şəxs həmin order-in:
  - ya `userId`-si olmalıdır
  - ya da `courierId`-si olmalıdır
- Qarşı tərəf (`receiverId`) order məlumatına əsasən avtomatik təyin olunur

#### Success Response (`201 Created`)

```json
{
  "id": "MSG-90001",
  "orderId": "ORD-1001",
  "senderId": "USR-10",
  "receiverId": "CR-44",
  "content": "Salam, sifariş yoldadır?",
  "createdAt": "2026-04-08T10:15:30Z"
}
```

#### Error Responses

- `400 Bad Request` - validasiya xətası
- `401 Unauthorized` - token yoxdur/yalnışdır
- `403 Forbidden` - order ilə əlaqəsi olmayan istifadəçi
- `404 Not Found` - order tapılmadı

---

### 4.2 GET `/api/chats/orders/{orderId}`

Order üzrə bütün mesaj tarixçəsini qaytarır.

#### Path Param

- `orderId` (string)

#### Business Rules

- Order mövcud olmalıdır
- Request atan istifadəçi həmin order ilə əlaqəli olmalıdır (`user` və ya təyinli `courier`)
- Nəticə `createdAt ASC` (köhnədən yeniyə) qaytarılır

#### Success Response (`200 OK`)

```json
{
  "orderId": "ORD-1001",
  "messages": [
    {
      "id": "MSG-90001",
      "orderId": "ORD-1001",
      "senderId": "USR-10",
      "receiverId": "CR-44",
      "content": "Salam, sifariş yoldadır?",
      "createdAt": "2026-04-08T10:15:30Z"
    },
    {
      "id": "MSG-90002",
      "orderId": "ORD-1001",
      "senderId": "CR-44",
      "receiverId": "USR-10",
      "content": "Bəli, 10 dəqiqəyə çatdırıram.",
      "createdAt": "2026-04-08T10:16:05Z"
    }
  ]
}
```

#### Error Responses

- `401 Unauthorized`
- `403 Forbidden`
- `404 Not Found`

## 5) WebSocket Contract

Real-time mesajlaşma üçün STOMP-over-WebSocket istifadə etmək tövsiyə olunur.

- **Connect endpoint:** `/ws`
- **Client subscribe topic (user scoped):** `/user/queue/messages`
- **Server publish event:** `chat.message.created`

### Event Payload

```json
{
  "eventType": "chat.message.created",
  "data": {
    "id": "MSG-90001",
    "orderId": "ORD-1001",
    "senderId": "USR-10",
    "receiverId": "CR-44",
    "content": "Salam, sifariş yoldadır?",
    "createdAt": "2026-04-08T10:15:30Z"
  }
}
```

## 6) Security & Authorization

- Auth mexanizmi: JWT Bearer Token
- Token claim-ləri minimum:
  - `sub`: istifadəçi ID
  - `role`: `USER` və ya `COURIER`
- Hər request üçün order ownership assignment yoxlanışı məcburidir
- Audit üçün `senderId`, `orderId`, `ip`, `userAgent` loglanması tövsiyə olunur

## 7) Data Model (Suggested)

`chat_messages` cədvəli:

- `id` (PK, UUID/String)
- `order_id` (index)
- `sender_id` (index)
- `receiver_id` (index)
- `content` (text/varchar)
- `created_at` (timestamp, index)

Recommended index:

- `(order_id, created_at)` - history sorğuları üçün
- `(receiver_id, created_at)` - inbox/unread use-case üçün

## 8) Standard Error Format

Servislərarası inteqrasiya üçün vahid error formatı istifadə edin:

```json
{
  "timestamp": "2026-04-08T10:20:10Z",
  "status": 403,
  "error": "Forbidden",
  "code": "CHAT_ACCESS_DENIED",
  "message": "You are not allowed to access this order chat.",
  "path": "/api/chats/orders/ORD-1001"
}
```

## 9) Non-Functional Requirements

- Message write/read əməliyyatları üçün aşağı latency
- İstifadəçi başına rate-limit (spam qoruması)
- Müşahidə (observability): structured logs, metrics, tracing
- Retry/backoff strategiyası (Order Service və digər downstream çağırışlarda)

## 10) Local Run

Bu repo hazırda ilkin Spring Boot skeletidir.

```bash
./gradlew bootRun
```

Windows:

```powershell
.\gradlew.bat bootRun
```

Servis adı: `chatservice-ms`

## 11) Next Implementation Checklist

- Message entity + migration əlavə et
- Auth parsing/filter əlavə et
- Order Service client inteqrasiyası yaz
- POST `/api/chats/messages` endpoint implement et
- GET `/api/chats/orders/{orderId}` endpoint implement et
- WebSocket config + real-time publish implement et
- Integration tests (auth + authorization + order checks) yaz

