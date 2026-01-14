package fpt.kiennt169.e_commerce.services.impl;

import fpt.kiennt169.e_commerce.dtos.PageResponse;
import fpt.kiennt169.e_commerce.dtos.product.*;
import fpt.kiennt169.e_commerce.entities.Product;
import fpt.kiennt169.e_commerce.exceptions.ResourceNotFoundException;
import fpt.kiennt169.e_commerce.mappers.ProductMapper;
import fpt.kiennt169.e_commerce.repositories.ProductRepository;
import fpt.kiennt169.e_commerce.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = findProductById(id);
        return productMapper.toDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductListDTO> getAllProducts(Pageable pageable) {
        Page<Product> page = productRepository.findByIsActiveTrue(pageable);
        return PageResponse.from(page.map(productMapper::toListDTO));
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }
}
