package com.jskno.productsapp.rest;

import com.jskno.productsapp.domain.CreateProductRestModel;
import com.jskno.productsapp.service.ProductServiceV3;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v3/products")
@RequiredArgsConstructor
public class ProductControllerV3 {

    private final ProductServiceV3 productService;

    @PostMapping
    ResponseEntity<String> createProduct(@RequestBody CreateProductRestModel product) {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(productService.createProduct(product));
    }

}
