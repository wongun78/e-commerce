package fpt.kiennt169.e_commerce;

import fpt.kiennt169.e_commerce.dtos.cart.AddToCartRequest;
import fpt.kiennt169.e_commerce.services.CartService;
import fpt.kiennt169.e_commerce.services.CheckoutService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ECommerceApplicationTests {

	@Autowired
	private CheckoutService checkoutService;
	
	@Autowired
	private CartService cartService;

	@Test
	void contextLoads() {
	}

	/**
	 * TEST CONCURRENCY - Race Condition Prevention
	 * 
	 * Scenario: 2 guests đồng thời checkout cùng 1 product CÓ DUY NHẤT 1 ITEM
	 * Expected: Chỉ 1 guest thành công, 1 guest bị reject (InsufficientStock)
	 * 
	 * Test Data:
	 * - Variant ID 3: AJ1-42-RB (Air Jordan 1, size 42) - stock_quantity = 1
	 * 
	 * Concurrency Control:
	 * - Pessimistic locking với SERIALIZABLE isolation level
	 * - Thread A gets lock first, reserves stock, commits
	 * - Thread B waits for lock, sees reserved=1, throws InsufficientStockException
	 * 
	 * Run: ./mvnw test -Dtest=ECommerceApplicationTests#testConcurrentCheckout
	 */
	@Test
	void testConcurrentCheckout() throws InterruptedException {
		// Setup: Tạo 2 guest sessions và thêm cùng 1 product vào cart
		// Variant ID 3 = AJ1-42-RB (size 42) - LAST ITEM có duy nhất stock = 1
		Long variantId = 3L; // Variant có stock = 1 (LAST ITEM test case)
		String guestA = UUID.randomUUID().toString();
		String guestB = UUID.randomUUID().toString();

		// Thêm product vào cart của cả 2 guests
		AddToCartRequest addToCartRequest = new AddToCartRequest();
		addToCartRequest.setVariantId(variantId);
		addToCartRequest.setQuantity(1);
		
		cartService.addToCart(null, guestA, addToCartRequest);
		cartService.addToCart(null, guestB, addToCartRequest);
		
		System.out.println("✓ Setup completed: Both guests have product in cart");
		System.out.println("Guest A: " + guestA);
		System.out.println("Guest B: " + guestB);

		// Concurrent checkout test
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failCount = new AtomicInteger(0);
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch doneLatch = new CountDownLatch(2);

		// Thread A
		Thread threadA = new Thread(() -> {
			try {
				startLatch.await(); // Đợi signal để start đồng thời
				
				checkoutService.prepareCheckout(null, guestA);
				successCount.incrementAndGet();
				System.out.println("✓ Guest A: SUCCESS");
			} catch (Exception e) {
				failCount.incrementAndGet();
				System.out.println("✗ Guest A: FAILED - " + e.getMessage());
			} finally {
				doneLatch.countDown();
			}
		});

		// Thread B
		Thread threadB = new Thread(() -> {
			try {
				startLatch.await(); // Đợi signal để start đồng thời
				
				checkoutService.prepareCheckout(null, guestB);
				successCount.incrementAndGet();
				System.out.println("✓ Guest B: SUCCESS");
			} catch (Exception e) {
				failCount.incrementAndGet();
				System.out.println("✗ Guest B: FAILED - " + e.getMessage());
			} finally {
				doneLatch.countDown();
			}
		});

		// Start threads
		threadA.start();
		threadB.start();
		
		Thread.sleep(100); // Đảm bảo cả 2 threads đã ready
		startLatch.countDown(); // Signal: GO!
		
		doneLatch.await(); // Đợi cả 2 hoàn thành

		// Verify
		System.out.println("\n========================================");
		System.out.println("  CONCURRENT CHECKOUT RESULT");
		System.out.println("========================================");
		System.out.println("Success: " + successCount.get());
		System.out.println("Failed: " + failCount.get());
		
		assertThat(successCount.get())
			.withFailMessage("❌ Inventory Locking FAILED! Expected 1 success, got " + successCount.get())
			.isEqualTo(1);
		
		assertThat(failCount.get())
			.withFailMessage("❌ Expected 1 failure, got " + failCount.get())
			.isEqualTo(1);
		
		System.out.println("\n✅ TEST PASSED - Inventory Locking works!");
		System.out.println("  - 1 checkout succeeded ✓");
		System.out.println("  - 1 checkout rejected (insufficient stock) ✓");
		System.out.println("  - Race condition prevented by Pessimistic Lock!");
	}
}
