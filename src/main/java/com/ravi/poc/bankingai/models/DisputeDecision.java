package com.ravi.poc.bankingai.models;

import dev.langchain4j.model.output.structured.Description;

public record DisputeDecision(
        @Description("Must be exactly 'MANUAL_REVIEW_REQUIRED' or 'HIGH_PROBABILITY_FRAUD'")
        String status,
        @Description("The step-by-step logical reasoning explaining why this status was chosen.")
        String reasoning
) {

}
