package com.jskno.productsapp.rest;

import com.jskno.productsapp.domain.CreateProductRestModel;
import com.jskno.productsapp.service.ProductServiceV2;
import com.jskno.productsapp.service.ProductServiceV3HeaderId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v3/products/headers")
@RequiredArgsConstructor
public class ProductControllerV3Header {

    private final ProductServiceV3HeaderId productService;

    @PostMapping
    ResponseEntity<String> createProduct(@RequestBody CreateProductRestModel product) {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(productService.createProduct(product));
    }

}
