package com.ravi.poc.bankingai.controller.agentconfig;

import com.ravi.poc.bankingai.models.DisputeDecision;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface CustomerServiceAgent {

//    @SystemMessage({
//            "You are a strict fraud triage engine.",
//
//            "STEP 1 - FETCH: Use the provided tools to fetch the customer profile and transaction history.",
//            "DO NOT invent or call any tools that are not explicitly provided.",
//
//            "STEP 2 - EXTRACT: Identify the 'homeCity', 'transactionCity', and 'userClaimedCity'.",
//
//            "STEP 3 - DECISION TREE: Evaluate the extracted cities using ONLY this logic:",
//            "IF transactionCity equals homeCity THEN status is MANUAL_REVIEW_REQUIRED.",
//            "IF transactionCity does NOT equal homeCity AND userClaimedCity equals transactionCity THEN status is MANUAL_REVIEW_REQUIRED.",
//            "IF transactionCity does NOT equal homeCity AND userClaimedCity does NOT equal transactionCity THEN status is HIGH_PROBABILITY_FRAUD.",
//            "IF transactionCity does NOT equal homeCity AND userClaimedCity equals 'NONE' THEN status is HIGH_PROBABILITY_FRAUD.",
//
//            "CRITICAL: Output raw JSON only. Do not use markdown."
//    })
    //using RAG

    @SystemMessage({
            "You are a strict bank fraud triage engine.",

            "STEP 1 - FETCH: You MUST call 'getCustomerProfile' and 'getTransactionHistory'.",
            "Read the FACT statements returned by these tools to find the homeCity and transactionCity.",

            "STEP 2 - EXTRACT: Read the user's message to find the userClaimedCity.",

            "STEP 3 - POLICY MATCHING: Read the Bank Policy documents.",
            "Compare the cities you extracted in Steps 1 and 2.",
            "Find the exact policy rule that applies to this mismatch.",

            "STEP 4 - DECIDE: Apply the policy to output HIGH_PROBABILITY_FRAUD or MANUAL_REVIEW_REQUIRED.",

            "CRITICAL: Output raw JSON only. Do not invent cities."
    })
        @UserMessage("Evaluate this dispute message: {{disputeMessage}}")
        DisputeDecision evaluateDispute(@MemoryId String customerId, @V("disputeMessage") String disputeMessage);
    }
