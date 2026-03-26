package com.ravi.poc.bankingai.aiconfig;
import com.ravi.poc.bankingai.models.DisputeDecision;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface JsonFormatterAgent {

    @SystemMessage({
            "You are a precise data extraction engine.",
            "Read the provided investigation report.",
            "Extract the exact cities, the quoted policy, the reasoning, and the final status.",
            "Output ONLY raw, valid JSON matching the requested schema."
    })
    @UserMessage("Extract data from this report: \n\n{{report}}")
        // This agent has NO tools, so the JSON lock works perfectly!
    DisputeDecision extractDecision(String report);
}