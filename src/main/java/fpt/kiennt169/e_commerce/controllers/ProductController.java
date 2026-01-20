package fpt.kiennt169.e_commerce.controllers;

import fpt.kiennt169.e_commerce.constants.Constants;
import fpt.kiennt169.e_commerce.dtos.ApiResponse;
import fpt.kiennt169.e_commerce.dtos.PageResponse;
import fpt.kiennt169.e_commerce.dtos.product.*;
import fpt.kiennt169.e_commerce.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(
        summary = "Get all products with filters", 
        description = "Browse products with optional filters: category, price range, and search. All filters are optional and can be combined."
    )
    public ResponseEntity<ApiResponse<PageResponse<ProductListDTO>>> getAllProducts(
            @Parameter(description = "Category ID to filter by (e.g., 1 for Sneakers)")
            @RequestParam(required = false) Long categoryId,
            
            @Parameter(description = "Minimum price (VND)")
            @RequestParam(required = false) BigDecimal minPrice,
            
            @Parameter(description = "Maximum price (VND)")
            @RequestParam(required = false) BigDecimal maxPrice,
            
            @Parameter(description = "Search keyword in product name (case-insensitive)")
            @RequestParam(required = false) String search,
            
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = {Constants.DEFAULT_SORT_FIELD}, direction = Direction.DESC) Pageable pageable) {
        
        if (categoryId == null && minPrice == null && maxPrice == null && search == null) {
            return ResponseEntity.ok(ApiResponse.success(productService.getAllProducts(pageable)));
        }
        
        return ResponseEntity.ok(ApiResponse.success(
            productService.getProductsWithFilters(categoryId, minPrice, maxPrice, search, pageable)
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Get product details with variants and images")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }
}
