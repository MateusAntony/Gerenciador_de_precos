package com.mateusantony.Gerenciador.scheduler;

import com.mateusantony.Gerenciador.product.entity.Product;
import com.mateusantony.Gerenciador.product.repository.ProductRepository;
import com.mateusantony.Gerenciador.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceCheckScheduler {

    private final ProductRepository productRepository;
    private final ProductService productService;

    //Roda todo dia às 9h da manhã
    @Scheduled(cron = "0 */3 * * * *")
    public void checkAllActiveProducts() {
        List<Product> activeProducts = productRepository.findByActiveTrue();

        log.info("Iniciando verificacao de precos para {} produtos ativos", activeProducts.size());

        for (Product product : activeProducts) {
            try {
                productService.checkPriceNow(product.getId());
            } catch (Exception e) {
                log.error("Erro ao verificar produto {}: {}", product.getId(), e.getMessage());
            }
        }

        log.info("Verificacao de precos finalizada");
    }
}