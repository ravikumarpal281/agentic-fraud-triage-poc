package com.ravi.poc.bankingai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ravi.poc.bankingai.controller.agentconfig.CustomerContext;
import com.ravi.poc.bankingai.controller.agentconfig.CustomerServiceAgent;
import com.ravi.poc.bankingai.models.DisputeDecision;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/disputes")
public class DisputeController {
    private final CustomerServiceAgent agent;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public DisputeController(CustomerServiceAgent agent, RabbitTemplate rabbitTemplate) {
        this.agent = agent;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping
    public String submitDispute(@RequestParam String customerId, @RequestBody String message) throws JsonProcessingException {
        System.out.println("Recieved dispute for customer" + customerId);
        System.out.println("Msg dispute for customer" + message);

        try {
            // 1. Set the ID in the ThreadLocal context
            CustomerContext.setCustomerId(customerId);
            // 1. The Agent evaluates the dispute (It will pause here to call your @Tools)
            DisputeDecision decision = agent.evaluateDispute(message);

            // Type safety in action: You can now do things like this safely:
            if (decision.status().equals("HIGH_PROBABILITY_FRAUD")) {
                System.out.println("🚨 RED ALERT for " + customerId + ": " + decision.reasoning());
            }

            // 2. Convert the Object back to JSON for the RabbitMQ event payload
            String jsonPayload = objectMapper.writeValueAsString(decision);
            rabbitTemplate.convertAndSend("dispute_routing_queue", jsonPayload);

            return "Dispute recieved and is being processed";
        } finally {
            // 4. ALWAYS clear the ThreadLocal to prevent data bleeding between HTTP requests
            CustomerContext.clear();
        }
    }
}
