package com.mateusantony.Gerenciador.product.repository;

import com.mateusantony.Gerenciador.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByUserId(Long userId);

    List<Product> findByActiveTrue();
}