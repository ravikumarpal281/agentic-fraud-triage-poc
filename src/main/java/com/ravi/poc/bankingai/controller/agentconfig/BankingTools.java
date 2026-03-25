package com.ravi.poc.bankingai.controller.agentconfig;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class BankingTools {

    @Tool("Fetches the home city and profile for the current customer.")
    public String getCustomerProfile() {
        String customerId = CustomerContext.getCustomerId();
        System.out.println("🔧 AI TOOL CALLED: Fetching profile for " + customerId);
        // Return clear plaintext, NOT JSON!
        return "FACT: The customer's authorized home city is Leeds, UK.";
    }

    @Tool("Fetches the recent transaction history for the current customer.")
    public String getTransactionHistory() {
        String customerId = CustomerContext.getCustomerId();
        System.out.println("🔧 AI TOOL CALLED: Fetching transactions for " + customerId);
        // Return clear plaintext, NOT JSON!
        return "FACT: The transaction occurred in London, UK at merchant Uber.";
    }
}