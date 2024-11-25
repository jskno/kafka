package com.jskno.productsapp.rest;

import com.jskno.productsapp.domain.CreateProductRestModel;
import com.jskno.productsapp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    ResponseEntity<String> createProduct(@RequestBody CreateProductRestModel product) {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(productService.createProduct(product));
    }

    @PostMapping("/sync")
    ResponseEntity<String> createProductSync(@RequestBody CreateProductRestModel product) {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(productService.createProductSync2(product));
    }
}
