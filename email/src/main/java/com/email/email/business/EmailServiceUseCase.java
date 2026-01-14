package com.email.email.business;

import com.email.email.ports.EmailServicePort;
import com.email.email.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceUseCase implements EmailServicePort {

    private final RabbitTemplate tpl;

    public EmailServiceUseCase(RabbitTemplate tpl) {
        this.tpl = tpl;
    }

    @Override
    public void processEmail(Map<String, Object> message) {
        try {
            String email = (String) message.get("to");
            String subject = (String) message.get("subject");
            String body = (String) message.get("body");

            if (email == null || subject == null || body == null) {
                publishError("missing to/subject/body", message);
                return;
            }

            // Mock email sending logic
            System.out.println("[EmailServiceUseCase] Sending email:");
            System.out.println("  → To: " + email);
            System.out.println("  → Subject: " + subject);
            System.out.println("  → Body: " + body);

            System.out.println("[EmailServiceUseCase] Email sent successfully → " + email);

        } catch (Exception e) {
            System.err.println("[EmailServiceUseCase] Failed to process email payload: " + message);
            e.printStackTrace();
            publishError(e.getMessage(), message);
        }
    }

    private void publishError(String reason, Map<String,Object> message) {
        var payload = new HashMap<String,Object>();
        payload.put("reason", reason);
        payload.put("payload", message);
        payload.put("service", "email");
        tpl.convertAndSend(RabbitConfig.EXCHANGE, "error.email.send", payload);
    }
}
