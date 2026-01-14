package com.clinicai.account.clinicai_account.business;

import com.clinicai.account.clinicai_account.ports.required.EmailServicePort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.clinicai.account.clinicai_account.config.RabbitMQConfig;

import java.util.Map;

@Component
public class EmailServiceImpl implements EmailServicePort {

    private final RabbitTemplate rabbitTemplate;

    public EmailServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendWelcomeEmail(String email) {
        Map<String, Object> event = Map.of(
                "to", email,
                "subject", "Welcome!",
                "body", "Welcome to our platform!"
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                event
        );

        System.out.println("[EmailService] Sent email event to queue → " + email);
    }
}
