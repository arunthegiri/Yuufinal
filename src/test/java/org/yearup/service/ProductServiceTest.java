package org.yearup.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yearup.models.Product;
import org.yearup.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest
{
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void search_noFilters_returnsAllProducts_includingNonFeatured()
    {
        Product featured    = new Product(1, "Headphones", 99.99, 1, "desc", "White",  100, true,  "headphones.jpg");
        Product notFeatured = new Product(2, "Laptop",    899.99, 1, "desc", "Gray",    30, false, "laptop.jpg");
        when(productRepository.findAll()).thenReturn(List.of(featured, notFeatured));

        List<Product> result = productService.search(null, null, null, null);

        assertEquals(2, result.size(), "Both featured and non-featured products should be returned.");
    }

    @Test
    void search_withPriceRange_filtersCorrectly()
    {
        Product cheap     = new Product(1, "T-Shirt", 29.99, 2, "desc", "Charcoal", 50, true,  "tshirt.jpg");
        Product expensive = new Product(2, "Laptop",  899.99, 1, "desc", "Gray",    30, false, "laptop.jpg");
        when(productRepository.findAll()).thenReturn(List.of(cheap, expensive));

        List<Product> result = productService.search(null, 0.0, 100.0, null);

        assertEquals(1, result.size());
        assertEquals("T-Shirt", result.get(0).getName());
    }

    @Test
    void update_persistsStockChange()
    {
        Product existing = new Product(1, "Laptop", 899.99, 1, "desc", "Gray", 30, false, "laptop.jpg");
        Product update   = new Product(1, "Laptop", 899.99, 1, "desc", "Gray", 99, false, "laptop.jpg");
        when(productRepository.findById(1)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.update(1, update);

        assertEquals(99, result.getStock(), "Stock should be updated to the new value.");
    }
}
