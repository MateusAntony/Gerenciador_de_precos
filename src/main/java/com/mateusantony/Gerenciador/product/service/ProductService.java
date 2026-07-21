package com.mateusantony.Gerenciador.product.service;

import com.mateusantony.Gerenciador.product.dto.ProductRequest;
import com.mateusantony.Gerenciador.product.dto.ProductResponse;
import com.mateusantony.Gerenciador.product.entity.Product;
import com.mateusantony.Gerenciador.product.mapper.ProductMapper;
import com.mateusantony.Gerenciador.product.repository.ProductRepository;
import com.mateusantony.Gerenciador.scraper.PriceScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final PriceScraperService scraperService;

    public ProductResponse create(ProductRequest request) {
        Product product = mapper.toEntity(request);
        product = repository.save(product);
        return mapper.toResponse(product);
    }

    public List<ProductResponse> findByUser(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ProductResponse findById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        return mapper.toResponse(product);
    }

    public ProductResponse checkPriceNow(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        BigDecimal newPrice = scraperService.scrapePrice(product.getUrl(), product.getPriceSelector());

        product.setLastPrice(newPrice);
        product.setLastCheckedAt(LocalDateTime.now());
        product = repository.save(product);

        return mapper.toResponse(product);
    }
}