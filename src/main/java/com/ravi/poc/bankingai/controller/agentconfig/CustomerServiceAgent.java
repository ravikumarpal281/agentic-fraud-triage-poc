package com.ravi.poc.bankingai.controller.agentconfig;

import com.ravi.poc.bankingai.models.DisputeDecision;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface CustomerServiceAgent {

    @SystemMessage({
            "You are an intelligent fraud triage agent for a bank.",

            "STEP 1 - FETCH DATA: You MUST use the provided tools to fetch the current customer's profile and transaction history.",
            "DO NOT invent or call any tools that are not explicitly provided.",

            "STEP 2 - INTERNAL ANALYSIS: Evaluate the risk based STRICTLY on the following rules in order:",
            "Rule 1: If the city in the transaction history matches the customer's home city, output MANUAL_REVIEW_REQUIRED.",
            "Rule 2: If the transaction city does NOT match the home city, check the user's message. If the user explicitly names the EXACT city where the transaction occurred, output MANUAL_REVIEW_REQUIRED.",
            "Rule 3: If the transaction city does NOT match the home city, AND the user's message mentions a COMPLETELY DIFFERENT city (e.g., the transaction was in London but the user says they were in Manchester), output HIGH_PROBABILITY_FRAUD.",
            "Rule 4: If the transaction city does NOT match the home city, AND the user's message does not mention any cities at all, output HIGH_PROBABILITY_FRAUD.",

            "Rule 5: CRITICAL OUTPUT INSTRUCTIONS: You must respond with raw, valid JSON only. Do not wrap the output in markdown code blocks. Escape all internal quotation marks."
    })
    @UserMessage("Evaluate this dispute message: {{disputeMessage}}")
    DisputeDecision evaluateDispute(@V("disputeMessage") String disputeMessage);
}
