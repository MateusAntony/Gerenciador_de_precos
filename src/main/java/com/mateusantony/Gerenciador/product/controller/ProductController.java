package com.mateusantony.Gerenciador.product.controller;

import com.mateusantony.Gerenciador.product.dto.ProductRequest;
import com.mateusantony.Gerenciador.product.dto.ProductResponse;
import com.mateusantony.Gerenciador.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        ProductResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(service.findByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping("/{id}/check")
    public ResponseEntity<ProductResponse> checkNow(@PathVariable Long id) {
        return ResponseEntity.ok(service.checkPriceNow(id));
    }
}