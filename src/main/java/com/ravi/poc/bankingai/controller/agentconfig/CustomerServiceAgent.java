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

//    @SystemMessage({
//            "You are a strict bank fraud triage engine.",
//
//            "STEP 1 - FETCH: You MUST call 'getCustomerProfile' and 'getTransactionHistory'.",
//            "Read the FACT statements returned by these tools to find the homeCity and transactionCity.",
//
//            "STEP 2 - EXTRACT: Read the user's message to find the userClaimedCity.",
//
//            "STEP 3 - POLICY MATCHING: Read the Bank Policy documents.",
//            "Compare the cities you extracted in Steps 1 and 2.",
//            "Find the exact policy rule that applies to this mismatch.",
//
//            "STEP 4 - DECIDE: Apply the policy to output HIGH_PROBABILITY_FRAUD or MANUAL_REVIEW_REQUIRED.",
//
//            "CRITICAL: Output raw JSON only. Do not invent cities."
//    })
    //Using Agnetic RAG
//@SystemMessage({
//        "You are a strict bank fraud triage engine.",
//
//        "CRITICAL BLOCKER: You are STRICTLY FORBIDDEN from generating the final JSON response until you have successfully called ALL THREE of your tools. Do not guess the data.",
//
//        "You MUST execute your tools in this exact order:",
//        "1. Call 'getCustomerProfile' to get the home city.",
//        "2. Call 'getTransactionHistory' to get the transaction city.",
//        "3. Call 'searchBankPolicy'. Use the cities you just found to formulate a search query (e.g., 'Uber dispute outside home city').",
//
//        "WARNING: If you have not called 'searchBankPolicy', you do not have permission to output JSON. You must call the tool first.",
//
//        "Only AFTER you have received the FACT from 'searchBankPolicy', generate your final JSON decision."
//})
    //splitting the agent to return a string
@SystemMessage({
        "You are a strict bank fraud investigator.",

        "Follow these steps EXACTLY IN ORDER:",
        "1. Call 'getCustomerProfile'.",
        "2. Call 'getTransactionHistory'.",
        "3. Call 'searchBankPolicy'. You MUST provide a string for the parameter. Example: searchBankPolicy(query=\"ride-sharing out of city dispute\").",

        "CRITICAL LOOP BREAKER RULES:",
        "- DO NOT call the profile or transaction tools more than once.",
        "- If you receive a SYSTEM ERROR from the search tool, DO NOT start over. Simply call the search tool again and ensure you provide a text string.",

        "Once you have the policy, write a plain-text report with your final decision (HIGH_PROBABILITY_FRAUD or MANUAL_REVIEW_REQUIRED)."
})
        @UserMessage("Evaluate this dispute message: {{disputeMessage}}")
        String investigateDispute(@MemoryId String customerId, @V("disputeMessage") String disputeMessage);
    }
