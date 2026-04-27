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
  "orderId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "content": "Salam, sifariş yoldadır?"
}
```

#### Validation

- `orderId` boş ola bilməz və **RFC 4122 UUID** formatında olmalıdır
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
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "orderId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "senderId": "550e8400-e29b-41d4-a716-446655440001",
  "receiverId": "550e8400-e29b-41d4-a716-446655440002",
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

- `orderId` — **UUID** string (məs: `f47ac10b-58cc-4372-a567-0e02b2c3d479`)

#### Business Rules

- Order mövcud olmalıdır
- Request atan istifadəçi həmin order ilə əlaqəli olmalıdır (`user` və ya təyinli `courier`)
- Nəticə `createdAt ASC` (köhnədən yeniyə) qaytarılır

#### Success Response (`200 OK`)

```json
{
  "orderId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "messages": [
    {
      "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "orderId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
      "senderId": "550e8400-e29b-41d4-a716-446655440001",
      "receiverId": "550e8400-e29b-41d4-a716-446655440002",
      "content": "Salam, sifariş yoldadır?",
      "createdAt": "2026-04-08T10:15:30Z"
    },
    {
      "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
      "orderId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
      "senderId": "550e8400-e29b-41d4-a716-446655440002",
      "receiverId": "550e8400-e29b-41d4-a716-446655440001",
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
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "orderId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "senderId": "550e8400-e29b-41d4-a716-446655440001",
    "receiverId": "550e8400-e29b-41d4-a716-446655440002",
    "content": "Salam, sifariş yoldadır?",
    "createdAt": "2026-04-08T10:15:30Z"
  }
}
```

## 6) Security & Authorization

- Auth mexanizmi: JWT Bearer Token (`Authorization: Bearer <token>`)
- İmza: **HS256** (header-da `alg` yalnız `HS256` qəbul edilir; başqa alqoritmlər rədd edilir)
- Konfiqurasiya: `app.security.jwt-secret` və ya `app.security.internal-token` — ən azı biri güclü bir açar ilə doldurulmalıdır (məs: mühitdə `JWT_SECRET`)

### JWT claim-ləri (minimum)

| Claim | Tələb | İzah |
|--------|--------|------|
| `sub` | bəli | İstifadəçi identifikatoru **RFC 4122 UUID** formatında olmalıdır (məs: `550e8400-e29b-41d4-a716-446655440000`). Servis token-i parse edəndə `sub`-u UUID kimi doğrulayır və kanonik stringə normalizə edir. |
| `role` | bəli | `USER` və ya `COURIER` (JWT-də mətn; böyük/kiçik hərflərə dözümlü) |
| `exp` | tövsiyə | Unix epoch (saniyə); keçmişdirsə token rədd edilir |

### Nümunə payload (decode olunmuş)

```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "role": "USER",
  "exp": 1735689600
}
```

- Hər request üçün order ownership assignment yoxlanışı məcburidir
- Audit üçün `senderId`, `orderId`, `ip`, `userAgent` loglanması tövsiyə olunur

**Ümumi ID qaydası:** `orderId`, mesaj `id`, `senderId`, `receiverId`, JWT `sub`, Order Service-dən gələn `userId` / `courierId` hamısı **UUID** (RFC 4122 string) kimi qəbul və saxlanılır.

## 7) Data Model (Suggested)

`chat_messages` cədvəli:

- `id` (PK, UUID string — persist zamanı avtomatik generasiya)
- `order_id` (UUID string, index)
- `sender_id` (UUID string, index)
- `receiver_id` (UUID string, index)
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
  "path": "/api/chats/orders/f47ac10b-58cc-4372-a567-0e02b2c3d479"
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

- Integration tests (auth + authorization + order checks, WebSocket connect)
- Rate limiting və observability genişləndirməsi

