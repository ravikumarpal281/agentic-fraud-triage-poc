package com.ravi.poc.bankingai.models;

import dev.langchain4j.model.output.structured.Description;

public record DisputeDecision(

        @Description("The exact home city returned by the getCustomerProfile tool. Do not guess.")
        String homeCity,

        @Description("The exact transaction city returned by the getTransactionHistory tool. Do not guess.")
        String transactionCity,

        @Description("The city the user claims to be in based ONLY on their message. If none, output 'NONE'.")
        String userClaimedCity,

        @Description("Quote the exact rule from the Bank Policy that applies to this specific scenario.")
        String applicablePolicy,

        @Description("Must be exactly 'MANUAL_REVIEW_REQUIRED' or 'HIGH_PROBABILITY_FRAUD'")
        String status,

        @Description("Explain why the policy applies to the homeCity and transactionCity.")
        String reasoning
) {}