package com.ravi.poc.bankingai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ravi.poc.bankingai.aiconfig.JsonFormatterAgent;
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
    private final JsonFormatterAgent formatterAgent;

    public DisputeController(CustomerServiceAgent agent, RabbitTemplate rabbitTemplate, JsonFormatterAgent formatterAgent) {
        this.agent = agent;
        this.rabbitTemplate = rabbitTemplate;
        this.formatterAgent = formatterAgent;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping
    public String submitDispute(@RequestParam String customerId, @RequestBody String message) throws Exception {
        System.out.println("📥 Received dispute for customer: " + customerId);

        try {
            CustomerContext.setCustomerId(customerId);

            // 1. Agent 1 runs the tools and writes a text report
            System.out.println("🧠 Step 1: Investigator Agent starting ReAct loop...");
            String investigationReport = agent.investigateDispute(customerId, message);
            System.out.println("📄 Investigation Report Generated.");

            // 2. Agent 2 converts the text report into strict JSON
            System.out.println("⚙️ Step 2: Formatter Agent extracting JSON...");
            DisputeDecision decision = formatterAgent.extractDecision(investigationReport);

            // 3. Publish to RabbitMQ
            String jsonPayload = objectMapper.writeValueAsString(decision);
            rabbitTemplate.convertAndSend("dispute_routing_queue", jsonPayload);
            System.out.println("🐇 Event published to RabbitMQ!");

            return "Dispute received. Current Status: " + decision.status();

        } finally {
            CustomerContext.clear();
        }
    }
}
