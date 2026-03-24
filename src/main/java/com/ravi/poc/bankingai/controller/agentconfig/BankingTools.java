package com.ravi.poc.bankingai.controller.agentconfig;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class BankingTools {

    @Tool("Fetches the home city and profile for the current customer.")
    public String getCustomerProfile() { // NO PARAMETERS NEEDED!
        String customerId = CustomerContext.getCustomerId();
        System.out.println("🔧 AI TOOL CALLED: Fetching profile for " + customerId);
        return "{ \"customerId\": \"" + customerId + "\", \"homeCity\": \"Leeds, UK\" }";
    }

    @Tool("Fetches the recent transaction history for the current customer.")
    public String getTransactionHistory() { // NO PARAMETERS NEEDED!
        String customerId = CustomerContext.getCustomerId();
        System.out.println("🔧 AI TOOL CALLED: Fetching transactions for " + customerId);
        return "[{ \"date\": \"2023-10-25\", \"merchant\": \"Uber\", \"amount\": 45.00, \"location\": \"London, UK\" }]";
    }
}
