package com.ravi.poc.bankingai.messaging;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class DisputeEventConsumer {
    // Spring AMQP will automatically declare this queue if it doesn't exist
    @RabbitListener(queuesToDeclare = @Queue("dispute_routing_queue"))
    public void handleDisputeDecision(String decisionPayload) {
        System.out.println("\n🐇 RabbitMQ Consumed Event! Routing to fraud department...");
        System.out.println("📝 Payload: " + decisionPayload + "\n");
    }
}
