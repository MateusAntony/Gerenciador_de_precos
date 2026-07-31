package com.mateusantony.Gerenciador.pricehistory.repository;

import com.mateusantony.Gerenciador.pricehistory.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    List<PriceHistory> findByProductIdOrderByCheckedAtDesc(Long productId);

    void deleteByProductId(Long productId);

}
