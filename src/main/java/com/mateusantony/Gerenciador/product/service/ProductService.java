package com.mateusantony.Gerenciador.product.service;

import com.mateusantony.Gerenciador.notification.EmailNotificationService;
import com.mateusantony.Gerenciador.pricehistory.entity.PriceHistory;
import com.mateusantony.Gerenciador.pricehistory.repository.PriceHistoryRepository;
import com.mateusantony.Gerenciador.product.dto.ProductRequest;
import com.mateusantony.Gerenciador.product.dto.ProductResponse;
import com.mateusantony.Gerenciador.product.entity.Product;
import com.mateusantony.Gerenciador.product.mapper.ProductMapper;
import com.mateusantony.Gerenciador.product.repository.ProductRepository;
import com.mateusantony.Gerenciador.scraper.PriceScraperService;
import com.mateusantony.Gerenciador.user.entity.User;
import com.mateusantony.Gerenciador.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final PriceScraperService scraperService;
    private final PriceHistoryRepository priceHistoryRepository;
    private final UserRepository userRepository;
    private final EmailNotificationService notificationService;

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

        BigDecimal previousPrice = product.getLastPrice();
        BigDecimal newPrice = scraperService.scrapePrice(product.getUrl(), product.getPriceSelector());

        // 1.Salva no histórico, sempre — independente de disparar alerta ou não
        priceHistoryRepository.save(
                PriceHistory.builder()
                        .productId(product.getId())
                        .price(newPrice)
                        .checkedAt(LocalDateTime.now())
                        .build()
        );

        // 2.Atualiza o produto com o preço mais recente
        product.setLastPrice(newPrice);
        product.setLastCheckedAt(LocalDateTime.now());
        product = repository.save(product);

        // 3.Dispara alerta só quando o preço cruza o alvo
        if (shouldTriggerAlert(previousPrice, newPrice, product.getTargetPrice())) {
            log.info("Cruzou");
            notifyUser(product, newPrice);
        }

        return mapper.toResponse(product);
    }

    private boolean shouldTriggerAlert(BigDecimal previousPrice, BigDecimal newPrice, BigDecimal targetPrice) {
        if (targetPrice == null) {
            return false;
        }

        boolean atingiuAlvo = newPrice.compareTo(targetPrice) <= 0;
        boolean estavaAcimaAntes = previousPrice == null || previousPrice.compareTo(targetPrice) > 0;

        return atingiuAlvo && estavaAcimaAntes;
    }

    private void notifyUser(Product product, BigDecimal newPrice) {
        log.info("iniciando notificação");
        try {
            User user = userRepository.findById(product.getUserId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            notificationService.sendPriceDropAlert(
                    user.getEmail(),
                    product.getName(),
                    product.getUrl(),
                    newPrice,
                    product.getTargetPrice()
            );

            log.info(" Alerta de preço disparado com sucesso para o e-mail: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Erro ao enviar notificacao para o produto {}: {}", product.getId(), e.getMessage());
        }
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        product.setName(request.name());
        product.setUrl(request.url());
        product.setPriceSelector(request.priceSelector());
        product.setTargetPrice(request.targetPrice());
        product.setUpdatedAt(LocalDateTime.now());

        product = repository.save(product);
        return mapper.toResponse(product);
    }
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado");
        }

        priceHistoryRepository.deleteByProductId(id);
        repository.deleteById(id);
    }
}