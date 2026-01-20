package fpt.kiennt169.e_commerce.entities;

import fpt.kiennt169.e_commerce.enums.OrderStatus;
import fpt.kiennt169.e_commerce.enums.PaymentMethod;
import fpt.kiennt169.e_commerce.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders", 
    indexes = {
        @Index(name = "idx_orders_user", columnList = "user_id"),
        @Index(name = "idx_orders_status", columnList = "status"),
        @Index(name = "idx_orders_user_created", columnList = "user_id, created_at"),
        @Index(name = "idx_orders_status_created", columnList = "status, created_at"),
        @Index(name = "idx_orders_customer_email_created", columnList = "customer_email, created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Order extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "customer_name", length = 255)
    private String customerName;

    @Column(name = "customer_email", length = 255)
    private String customerEmail;

    @Column(name = "customer_phone", length = 50)
    private String customerPhone;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 50)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 50)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public int getTotalItems() {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    public boolean canCancel() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }
}
