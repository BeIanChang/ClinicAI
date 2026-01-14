package com.email.email.adapter;

import com.email.email.ports.EmailServicePort;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmailConsumerAdapter {

    private final EmailServicePort emailServicePort;

    public EmailConsumerAdapter(EmailServicePort emailServicePort) {
        this.emailServicePort = emailServicePort;
    }

    // 使用 Map 接收消息
    @RabbitListener(queues = "email.queue")
    public void receiveMessage(Map<String, Object> message) {
        // 这里可以直接把 Map 传给 service 处理
        emailServicePort.processEmail(message);
    }

}
