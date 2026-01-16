package fpt.kiennt169.e_commerce.services.impl;

import fpt.kiennt169.e_commerce.entities.InventoryReservation;
import fpt.kiennt169.e_commerce.entities.ProductVariant;
import fpt.kiennt169.e_commerce.exceptions.InsufficientStockException;
import fpt.kiennt169.e_commerce.repositories.InventoryReservationRepository;
import fpt.kiennt169.e_commerce.repositories.ProductVariantRepository;
import fpt.kiennt169.e_commerce.services.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final ProductVariantRepository variantRepository;
    private final InventoryReservationRepository reservationRepository;

    @Value("${inventory.reservation.expiry-minutes:15}")
    private int reservationExpiryMinutes;

    @Override
    @Transactional
    public void reserveStock(Long variantId, int quantity, String sessionId) {
        log.debug("Reserving stock: variantId={}, quantity={}, sessionId={}", variantId, quantity, sessionId);

        ProductVariant variant = variantRepository.findByIdWithLock(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found: " + variantId));

        int reserved = reservationRepository.getTotalReservedQuantity(variantId);
        int available = variant.getStockQuantity() - reserved;

        if (available < quantity) {
            throw new InsufficientStockException(
                "Insufficient stock", quantity, available);
        }

        InventoryReservation reservation = InventoryReservation.builder()
                .productVariant(variant)
                .quantity(quantity)
                .sessionId(sessionId)
                .expiryTime(LocalDateTime.now().plusMinutes(reservationExpiryMinutes))
                .status("ACTIVE")
                .build();

        reservationRepository.save(reservation);
        log.info("Stock reserved: variantId={}, quantity={}", variantId, quantity);
    }

    @Override
    @Transactional
    public void releaseStock(Long variantId, int quantity, String sessionId) {
        log.debug("Releasing stock: variantId={}, quantity={}, sessionId={}", variantId, quantity, sessionId);

        reservationRepository.cancelReservationsBySessionId(sessionId);
        log.info("Stock released for session: {}", sessionId);
    }

    @Override
    @Transactional
    public void confirmStock(String sessionId) {
        log.debug("Confirming stock for session: {}", sessionId);

        List<InventoryReservation> reservations = reservationRepository
                .findBySessionIdAndStatus(sessionId, "ACTIVE");

        for (InventoryReservation reservation : reservations) {
            ProductVariant variant = variantRepository.findByIdWithLock(reservation.getProductVariant().getId())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            variant.setStockQuantity(variant.getStockQuantity() - reservation.getQuantity());
            variantRepository.save(variant);

            reservation.complete();
            reservationRepository.save(reservation);
        }

        log.info("Stock confirmed for session: {}", sessionId);
    }

    @Override
    @Transactional
    public void cleanupExpiredReservations() {
        log.debug("Cleaning up expired reservations...");

        List<InventoryReservation> expired = reservationRepository
                .findExpiredReservations(LocalDateTime.now());

        for (InventoryReservation reservation : expired) {
            reservation.expire();
            reservationRepository.save(reservation);
        }

        log.info("Cleaned up {} expired reservations", expired.size());
    }

    @Override
    public int getAvailableStock(Long variantId) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found: " + variantId));

        int reserved = reservationRepository.getTotalReservedQuantity(variantId);
        return variant.getStockQuantity() - reserved;
    }
}
