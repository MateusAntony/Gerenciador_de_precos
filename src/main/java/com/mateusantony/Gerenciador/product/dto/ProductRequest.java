package com.mateusantony.Gerenciador.product.dto;
import java.math.BigDecimal;

public record ProductRequest(
        Long userId,
        String name,
        String url,
        String priceSelector,
        BigDecimal targetPrice
) {
}