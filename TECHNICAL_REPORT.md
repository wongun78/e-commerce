# TECHNICAL REPORT

**Dự án: E-COMMERCE BACKEND SYSTEM (PHASE 1)**

**Tác giả:** KienNT169 - Backend Developer  
**Ngày:** 09 Jan, 2026  
**Tech Stack:** Spring Boot 4.0.1, Java 21, PostgreSQL 16
**Version:** 1.0.0

---

## ABSTRACT

Hệ thống E-Commerce Backend được phát triển trong **2 tuần** giải quyết vấn đề **overselling** cho Local Brand "Hung Hypebeast". Giải pháp chính: **Inventory Locking** với Pessimistic Lock + Reservation Table. Kết quả: **95% completion**. Tech: Spring Boot 4.0.1, PostgreSQL 16, JWT, Jakarta Validation.

---

## 1. YÊU CẦU & PHẠM VI

### 1.1. Yêu cầu khách hàng

**6 yêu cầu chính:**

1. **Catalog**: Quản lý variants (Size/Màu), phân trang, lọc giá
2. **Cart**: Guest + Customer, check tồn kho sơ bộ
3. **Inventory Locking (CRITICAL)**: Giữ hàng 10-15 phút khi checkout
4. **Payment**: COD + SePay (defer Phase 2)
5. **Order Tracking**: Email link, không cần đăng nhập
6. **Admin**: Xem đơn + đổi status

### 1.2. MoSCoW Prioritization

| Must-Have (100% Done)            | Nice-to-Have (Phase 2) |
| -------------------------------- | ---------------------- |
| Authentication (JWT + Roles)     | SePay Integration      |
| Strong Password Validation       | Email Async Queue      |
| Product Catalog + Variants       |                        |
| Guest/Customer Cart              |                        |
| **Inventory Locking** (Advanced) |                        |
| Order Management                 |                        |
| Public Tracking (UUID)           |                        |
| Email Notifications              |                        |
| Admin API                        |                        |
| Swagger UI                       |                        |

**Completion: 95%** - Defer SePay do time constraint

---

## 2. THIẾT KẾ HỆ THỐNG

### 2.1. Kiến trúc tổng quan

```
┌──────────────────────┐
│  REST API Layer      │ ← AuthController, ProductController, CartController
│  (Controllers)       │   CheckoutController, OrderController
└──────────────────────┘
          ↓
┌──────────────────────┐
│  Business Logic      │ ← AuthService, ProductService, CartService
│  (Services)          │   CheckoutService, OrderService, EmailService
└──────────────────────┘
          ↓
┌──────────────────────┐
│  Data Access (JPA)   │ ← UserRepo, ProductRepo, CartRepo, OrderRepo
│  (Repositories)      │   ReservationRepo
└──────────────────────┘
          ↓
┌──────────────────────┐
│  PostgreSQL DB       │
└──────────────────────┘
```

### 2.2. Entity Relationship Diagram (ERD)

![ERD - Database Schema](./images/ERD_Diagram.png)
_Hình 1: Entity Relationship Diagram - Database Schema_

**Điểm nhấn thiết kế:**

- **Hybrid ID Strategy**: Auto-increment (users, products) vs UUID (orders, carts, reservations)
- **Product Variants**: Separate table → track stock cho từng Size/Màu
- **Inventory Reservations**: Bảng riêng giữ hàng 15 phút, không lock trực tiếp product_variants
- **Nullable user_id**: Hỗ trợ guest checkout (orders.user_id = NULL)

**Key Tables:**

```
users → orders (1:N, nullable user_id)
categories → products → product_variants (1:N:N)
carts (user_id | session_id) → cart_items (1:N)
inventory_reservations (expires_at, status: ACTIVE/CONFIRMED/EXPIRED)
```

**Performance Indexes:**

```sql
idx_reservations_variant_status (variant_id, status)
idx_reservations_expires (expires_at, status)
idx_orders_tracking (tracking_number)
idx_carts_session (session_id, status)
```

### 2.3. API Endpoints

| Category     | Endpoints                                           | Auth             | Key Features               |
| ------------ | --------------------------------------------------- | ---------------- | -------------------------- |
| **Auth**     | POST /auth/register<br>POST /auth/login             | Public           | Strong password, JWT       |
| **Products** | GET /products<br>GET /products/{id}                 | Public           | Pagination, filter, search |
| **Cart**     | GET/POST/PUT/DELETE /cart/items                     | Guest/User       | X-Guest-ID header          |
| **Checkout** | POST /checkout/prepare<br>GET /checkout/verify/{id} | Guest/User       | Reserve 15 min             |
| **Orders**   | POST /orders<br>GET /orders                         | Guest/User/Admin | Create, list, track        |
| **Public**   | GET /public/orders/{id}?email=                      | Public           | HTML/JSON                  |

**Response Format:**

```json
{
  "status": "success|error",
  "data": {...},
  "timestamp": "2026-01-19T10:30:00Z"
}
```

---

## 3. GIẢI PHÁP KỸ THUẬT CHÍNH

### 3.1. Inventory Locking (CRITICAL - Giải quyết Overselling)

**Problem:** Race Condition - 2 users cùng mua "last item"

**Solution: 3-Layer Protection**

```java
// Layer 1: Database Lock
@Lock(LockModeType.PESSIMISTIC_WRITE)
ProductVariant findByIdWithLock(Long id);

// Layer 2: Soft Reservation
int available = stock - SUM(active_reservations);

// Layer 3: Transaction Isolation
@Transactional(isolation = Isolation.READ_COMMITTED)
```

![Sequence Diagram - Last Item Purchase Flow](./images/Sequence_Diagram_Inventory_Locking.png)
_Hình 2: Sequence Diagram - Race Condition Prevention_

**Luồng xử lý:**

1. Customer A → POST /checkout/prepare
2. START TRANSACTION + `SELECT ... FOR UPDATE` (lock row)
3. Calculate: Available = Stock (1) - Active Reservations (0) = 1
4. INSERT reservation (expires_at = NOW + 15m)
5. COMMIT (release lock)
6. Customer B bị block → tính lại Available = 0 → REJECT

**Cleanup:** Scheduled task xóa reservations expired > 15 phút

### 3.2. Strong Password Validation

**Challenge:** Security - user dùng weak passwords

**Solution:** Custom Jakarta Validation annotation

```java
@StrongPassword  // Auto-validate with @Valid
private String password;
```

**Requirements:** Min 8 chars + uppercase + lowercase + digit + special char

### 3.3. Email System

**Challenge:** Order confirmation emails không làm crash order creation

**Solution:** Strategy Pattern + Conditional Bean

```java
// Production
@ConditionalOnProperty(name = "spring.mail.enabled", havingValue = "true")
public class EmailServiceImpl { }

// Development (no-op)
@ConditionalOnProperty(name = "spring.mail.enabled", havingValue = "false")
public class NoOpEmailService { }
```

**Result:**

- Non-blocking (order creation NEVER fails)
- Dev-friendly (no SMTP required locally)
- Professional HTML templates (Thymeleaf)

### 3.4. Public Order Tracking

**Challenge:** Khách track order mà không cần login

**Solution:** UUID + Email Verification + Content Negotiation

```
GET /api/v1/public/orders/{uuid}?email=customer@example.com
Accept: text/html → HTML view
Accept: application/json → JSON API
```

**Security:**

- UUID order ID (không đoán được)
- Email verification (chỉ người có email)
- Rate limiting ready

---

## 5. KẾT LUẬN

### 5.1. Achievements

**Core Features: 100%** - Inventory Locking (zero overselling), Guest Checkout, Public Tracking  
**Security: 100%** - Strong Password, JWT, Role-based, Email verification  
**Testing: 100%** - Postman collection  
**Documentation: 100%** - Swagger UI, README  
**Deferred: 5%** - SePay(Phase 2)

### 5.2. Technical Complexity

- **Inventory Locking**: Advanced (Pessimistic Lock + Reservation Table)
- **Concurrency Control**: Advanced (Race Condition prevention)
- **System Design**: Intermediate (Clean Architecture, SOLID)
- **Security**: Intermediate (JWT, Custom Validation)

### 5.3. Production Readiness: 90%

**Ready:**

- Core business logic
- Security (JWT, BCrypt, Strong Password)
- Email notifications
- Error handling
- API documentation

**Missing:**

- CI/CD pipeline
- Health checks (Actuator available)
- Rate limiting (nginx level)
- Database backups

### 5.4. Phase 2 Roadmap

| Priority | Feature                    | Estimate |
| -------- | -------------------------- | -------- |
| **High** | SePay Integration          | 5 days   |
| **High** | Email Async Queue          | 3 days   |
| Medium   | Redis Caching              | 2 days   |
| Low      | Advanced Filters, Wishlist | TBD      |

---

## PHỤ LỤC

### A. Tech Stack

```yaml
Backend: Spring Boot 4.0.1, Java 21
Database: PostgreSQL 16
Security: Spring Security 6, JWT
Validation: Jakarta Validation 3.0
Email: JavaMailSender, Thymeleaf
Build: Gradle 8.12
Testing: Postman
```

### B. Deployment Checklist

- [ ] PostgreSQL 16+ setup
- [ ] Configure application.properties (DB, JWT, Email)
- [ ] Generate Gmail App Password
- [ ] Build: `./gradlew clean build`
- [ ] Run: `./gradlew bootRun`
- [ ] Access: http://localhost:8080/swagger-ui.html

### C. References

1. Spring Boot Documentation - https://spring.io/projects/spring-boot
2. PostgreSQL 16 Docs - https://www.postgresql.org/docs/16/
3. Jakarta Validation Spec - https://jakarta.ee/specifications/bean-validation/3.0/
4. Assignment Instruction - FPT Software Academy
5. Client Requirements - Email from Anh Hùng (Hung Hypebeast)

---

**Status:** Ready for Submission  
**GitHub:** https://github.com/wongun78/e-commerce  
**Swagger:** http://localhost:8080/swagger-ui.html  
**Contact:** KienNT169 - Backend Developer
