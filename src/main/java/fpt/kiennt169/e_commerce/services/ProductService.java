package fpt.kiennt169.e_commerce.services;

import fpt.kiennt169.e_commerce.dtos.PageResponse;
import fpt.kiennt169.e_commerce.dtos.product.*;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    /**
     * Get product by ID
     */
    ProductDTO getProductById(Long id);

    /**
     * Get all products with pagination
     */
    PageResponse<ProductListDTO> getAllProducts(Pageable pageable);
}
