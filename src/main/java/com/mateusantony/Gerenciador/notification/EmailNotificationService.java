package com.mateusantony.Gerenciador.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    public void sendPriceDropAlert(String toEmail, String productName, String productUrl,
                                   BigDecimal newPrice, BigDecimal targetPrice) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("🔔 Preço caiu: " + productName);
        message.setText(
                "O produto \"" + productName + "\" atingiu o preço que você esperava!\n\n" +
                        "Preço atual: R$ " + newPrice + "\n" +
                        "Seu preço-alvo: R$ " + targetPrice + "\n\n" +
                        "Link: " + productUrl
        );

        mailSender.send(message);
    }
}