package com.mateusantony.Gerenciador.product.mapper;

import com.mateusantony.Gerenciador.product.dto.ProductRequest;
import com.mateusantony.Gerenciador.product.dto.ProductResponse;
import com.mateusantony.Gerenciador.product.entity.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        return Product.builder()
                .userId(request.userId())
                .name(request.name())
                .url(request.url())
                .priceSelector(request.priceSelector())
                .targetPrice(request.targetPrice())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getUrl(),
                product.getTargetPrice(),
                product.getLastPrice(),
                product.getActive(),
                product.getLastCheckedAt()
        );
    }
}