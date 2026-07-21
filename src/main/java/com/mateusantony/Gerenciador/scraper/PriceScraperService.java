package com.mateusantony.Gerenciador.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PriceScraperService {

    private static final Pattern PRICE_PATTERN = Pattern.compile("[0-9.,]+");

    public BigDecimal scrapePrice(String url, String cssSelector) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36")
                    .timeout(15_000)
                    .get();

            Element priceElement = doc.selectFirst(cssSelector);

            if (priceElement == null) {
                throw new PriceNotFoundException(
                        "Seletor '" + cssSelector + "' não encontrou nenhum elemento na página.");
            }

            return extractPrice(priceElement);

        } catch (IOException e) {
            throw new ScrapingException("Erro ao acessar a URL: " + url, e);
        }
    }

    /**
     * Meta tags (ex: Americanas) trazem o preço no atributo "content", já em
     * formato decimal com ponto (ex: "2051.1"). Elementos de texto normal
     * (ex: Kabum) trazem algo como "R$ 4.402,83", em formato brasileiro.
     */
    private BigDecimal extractPrice(Element priceElement) {
        String content = priceElement.attr("content");

        if (!content.isBlank()) {
            return new BigDecimal(content.trim());
        }

        return parseBrazilianPrice(priceElement.text());
    }

    private BigDecimal parseBrazilianPrice(String rawText) {
        Matcher matcher = PRICE_PATTERN.matcher(rawText);

        if (!matcher.find()) {
            throw new PriceNotFoundException("Não foi possível extrair um número de: " + rawText);
        }

        String numeric = matcher.group();

        if (numeric.contains(",")) {
            numeric = numeric.replace(".", "").replace(",", ".");
        }

        return new BigDecimal(numeric);
    }
}