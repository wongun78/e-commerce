package fpt.kiennt169.e_commerce.controllers;

import fpt.kiennt169.e_commerce.dtos.ApiResponse;
import fpt.kiennt169.e_commerce.dtos.PageResponse;
import fpt.kiennt169.e_commerce.dtos.product.*;
import fpt.kiennt169.e_commerce.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Get paginated list of active products")
    public ResponseEntity<ApiResponse<PageResponse<ProductListDTO>>> getAllProducts(
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(productService.getAllProducts(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Get product details by ID")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }
}
