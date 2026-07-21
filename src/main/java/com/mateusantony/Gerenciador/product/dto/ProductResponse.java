package com.mateusantony.Gerenciador.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String url,
        BigDecimal targetPrice,
        BigDecimal lastPrice,
        Boolean active,
        LocalDateTime lastCheckedAt
) {
}