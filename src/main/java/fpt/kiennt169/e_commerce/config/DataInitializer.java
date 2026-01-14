package fpt.kiennt169.e_commerce.config;

import fpt.kiennt169.e_commerce.entities.*;
import fpt.kiennt169.e_commerce.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

/**
 * Data initialization for development environment.
 * Creates admin user, customer, categories, products with variants.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("!test")
    @ConditionalOnProperty(name = "data.init.enabled", havingValue = "true", matchIfMissing = true)
    public CommandLineRunner initData() {
        return args -> {
            log.info("=== Starting Data Initialization ===");
            initUsers();
            initCategories();
            initProducts();
            log.info("=== Data Initialization Complete ===");
        };
    }

    private void initUsers() {
        log.info("Initializing users...");

        // Admin user
        String adminEmail = "admin@hunghypebeast.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .fullName("System Administrator")
                    .role("ROLE_ADMIN")
                    .build();
            userRepository.save(admin);
            log.info("✓ Created admin user: {}", adminEmail);
        }

        // Customer user for testing
        String customerEmail = "customer@example.com";
        if (userRepository.findByEmail(customerEmail).isEmpty()) {
            User customer = User.builder()
                    .email(customerEmail)
                    .passwordHash(passwordEncoder.encode("Customer@123"))
                    .fullName("Test Customer")
                    .role("ROLE_CUSTOMER")
                    .build();
            userRepository.save(customer);
            log.info("✓ Created customer user: {}", customerEmail);
        }
    }

    private void initCategories() {
        log.info("Initializing categories...");

        if (categoryRepository.count() == 0) {
            // Sneakers Category
            Category sneakers = Category.builder()
                    .name("Sneakers")
                    .description("Premium sneakers collection")
                    .build();
            categoryRepository.save(sneakers);
            log.info("✓ Created category: Sneakers");

            // Streetwear Category
            Category streetwear = Category.builder()
                    .name("Streetwear")
                    .description("Limited edition streetwear")
                    .build();
            categoryRepository.save(streetwear);
            log.info("✓ Created category: Streetwear");

            // Accessories Category
            Category accessories = Category.builder()
                    .name("Accessories")
                    .description("Premium accessories")
                    .build();
            categoryRepository.save(accessories);
            log.info("✓ Created category: Accessories");
        }
    }

    private void initProducts() {
        log.info("Initializing products...");

        if (productRepository.count() == 0) {
            Category sneakersCategory = categoryRepository.findByName("Sneakers").orElseThrow();
            Category streetwearCategory = categoryRepository.findByName("Streetwear").orElseThrow();
            Category accessoriesCategory = categoryRepository.findByName("Accessories").orElseThrow();

            // Product 1: Air Jordan 1 High (LAST ITEM TEST - Only 1 left in size 42)
            Product airJordan = Product.builder()
                    .name("Air Jordan 1 High 'Chicago'")
                    .basePrice(new BigDecimal("4500000"))
                    .category(sneakersCategory)
                    .isActive(true)
                    .build();
            productRepository.save(airJordan);
            
            createVariant(airJordan, "AJ1-40-RB", "40", "RED_BLACK", new BigDecimal("4500000"), 5);
            createVariant(airJordan, "AJ1-41-RB", "41", "RED_BLACK", new BigDecimal("4500000"), 3);
            createVariant(airJordan, "AJ1-42-RB", "42", "RED_BLACK", new BigDecimal("4500000"), 1); // LAST ITEM!
            createVariant(airJordan, "AJ1-43-RB", "43", "RED_BLACK", new BigDecimal("4500000"), 0); // OUT OF STOCK
            log.info("✓ Created product: Air Jordan 1 High with 4 variants (includes LAST ITEM test case)");

            // Product 2: Yeezy Boost 350 (NORMAL STOCK)
            Product yeezy = Product.builder()
                    .name("Yeezy Boost 350 V2 'Zebra'")
                    .basePrice(new BigDecimal("5200000"))
                    .category(sneakersCategory)
                    .isActive(true)
                    .build();
            productRepository.save(yeezy);
            
            createVariant(yeezy, "YZY-40-WB", "40", "WHITE_BLACK", new BigDecimal("5200000"), 10);
            createVariant(yeezy, "YZY-41-WB", "41", "WHITE_BLACK", new BigDecimal("5200000"), 8);
            createVariant(yeezy, "YZY-42-WB", "42", "WHITE_BLACK", new BigDecimal("5200000"), 12);
            createVariant(yeezy, "YZY-43-WB", "43", "WHITE_BLACK", new BigDecimal("5200000"), 15);
            log.info("✓ Created product: Yeezy Boost 350 with 4 variants (normal stock)");

            // Product 3: Supreme Box Logo Hoodie (LIMITED STOCK - 2-3 items)
            Product supremeHoodie = Product.builder()
                    .name("Supreme Box Logo Hoodie")
                    .basePrice(new BigDecimal("8500000"))
                    .category(streetwearCategory)
                    .isActive(true)
                    .build();
            productRepository.save(supremeHoodie);
            
            createVariant(supremeHoodie, "SPR-M-BLK", "M", "BLACK", new BigDecimal("8500000"), 2);
            createVariant(supremeHoodie, "SPR-L-BLK", "L", "BLACK", new BigDecimal("8500000"), 3);
            createVariant(supremeHoodie, "SPR-XL-BLK", "XL", "BLACK", new BigDecimal("8500000"), 1);
            createVariant(supremeHoodie, "SPR-M-GRY", "M", "GREY", new BigDecimal("8500000"), 2);
            createVariant(supremeHoodie, "SPR-L-GRY", "L", "GREY", new BigDecimal("8500000"), 0); // OUT OF STOCK
            log.info("✓ Created product: Supreme Box Logo Hoodie with 5 variants (limited stock)");

            // Product 4: Nike Dunk Low (HIGH STOCK - good for multi-item cart)
            Product dunk = Product.builder()
                    .name("Nike Dunk Low 'Panda'")
                    .basePrice(new BigDecimal("3200000"))
                    .category(sneakersCategory)
                    .isActive(true)
                    .build();
            productRepository.save(dunk);
            
            createVariant(dunk, "DNK-40-WB", "40", "WHITE_BLACK", new BigDecimal("3200000"), 25);
            createVariant(dunk, "DNK-41-WB", "41", "WHITE_BLACK", new BigDecimal("3200000"), 30);
            createVariant(dunk, "DNK-42-WB", "42", "WHITE_BLACK", new BigDecimal("3200000"), 35);
            createVariant(dunk, "DNK-43-WB", "43", "WHITE_BLACK", new BigDecimal("3200000"), 20);
            log.info("✓ Created product: Nike Dunk Low with 4 variants (high stock)");

            // Product 5: Off-White Belt (MEDIUM STOCK)
            Product belt = Product.builder()
                    .name("Off-White Industrial Belt")
                    .basePrice(new BigDecimal("2800000"))
                    .category(accessoriesCategory)
                    .isActive(true)
                    .build();
            productRepository.save(belt);
            
            createVariant(belt, "OW-OS-YEL", "ONE_SIZE", "YELLOW", new BigDecimal("2800000"), 7);
            createVariant(belt, "OW-OS-BLK", "ONE_SIZE", "BLACK", new BigDecimal("2800000"), 5);
            log.info("✓ Created product: Off-White Belt with 2 variants (medium stock)");

            log.info("✓ Created 5 products with 19 total variants");
            log.info("✓ Test scenarios: LAST ITEM (AJ1-42), OUT OF STOCK (AJ1-43, SPR-L-GRY), LIMITED (Supreme), HIGH (Dunk)");
        }
    }

    private void createVariant(Product product, String sku, String size, String color, BigDecimal price, int quantity) {
        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(sku)
                .size(size)
                .color(color)
                .price(price)
                .stockQuantity(quantity)
                .build();
        variantRepository.save(variant);
    }
}
