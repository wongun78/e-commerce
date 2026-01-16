package fpt.kiennt169.e_commerce.specifications;

import fpt.kiennt169.e_commerce.entities.Product;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Product> hasPriceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) return null;
            return cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice);
        };
    }

    public static Specification<Product> hasPriceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) return null;
            return cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice);
        };
    }

    public static Specification<Product> nameContains(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) return null;
            return cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
        };
    }

    public static Specification<Product> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) return cb.isTrue(root.get("isActive")); 
            return cb.equal(root.get("isActive"), active);
        };
    }

    public static Specification<Product> fetchCategoryAndImages() {
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("category", JoinType.LEFT);
                root.fetch("images", JoinType.LEFT);
            }
            return cb.conjunction();
        };
    }
}
