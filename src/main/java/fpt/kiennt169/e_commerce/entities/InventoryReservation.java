package fpt.kiennt169.e_commerce.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_reservations", indexes = {
        @Index(name = "idx_reservations_session_status", columnList = "session_id, status"),
        @Index(name = "idx_reservations_status_expiry", columnList = "status, expiry_time"),
        @Index(name = "idx_reservations_variant_status", columnList = "product_variant_id, status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InventoryReservation extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "session_id", nullable = false, length = 255)
    private String sessionId;

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @Column(name = "status", length = 50)
    @Builder.Default
    private String status = "ACTIVE";

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public void complete() {
        this.status = "COMPLETED";
    }

    public void expire() {
        if ("ACTIVE".equals(status) && LocalDateTime.now().isAfter(expiryTime)) {
            this.status = "EXPIRED";
        }
    }
}
