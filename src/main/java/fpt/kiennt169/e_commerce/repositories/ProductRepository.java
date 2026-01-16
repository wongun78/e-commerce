package fpt.kiennt169.e_commerce.repositories;

import fpt.kiennt169.e_commerce.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    @EntityGraph(attributePaths = {"category", "images"})
    Optional<Product> findById(Long id);
    
    @EntityGraph(attributePaths = {"category", "images"})
    Page<Product> findByIsActiveTrue(Pageable pageable);
}
