package com.devsuperior.dscatalog;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct() {
        Product product = new Product(1L, "Phone" ,"Good Phone", 800.0, "https://img.com/img.png", Instant.parse("2021-09-27T03:00:00Z"));
        product.getCategories().add(new Category(2L, "Electronics"));
        return product;
    }

    public static ProductDTO createProductDto() {
        return new ProductDTO(createProduct(), createProduct().getCategories());
    }
}
