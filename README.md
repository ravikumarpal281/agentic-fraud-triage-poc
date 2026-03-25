Intelligent Transaction Dispute & Triage Agent
An event-driven Agentic AI microservice built for the banking domain.

Architecture Highlights:

Framework: Java 17, Spring Boot, LangChain4j

AI Orchestration: Local Llama 3.1 (via Ollama) / OpenAI GPT-4o-mini

Integration: Utilizes strict JSON Structured Outputs and LLM "Tool Calling" to autonomously query mocked core-banking databases via ThreadLocal context binding.

Event-Driven Routing: Asynchronously publishes deterministic fraud routing decisions (e.g., HIGH_PROBABILITY_FRAUD, MANUAL_REVIEW_REQUIRED) to RabbitMQ.

Implicit RAG Flow:
sequenceDiagram
    autonumber
    actor User as Client (cURL)
    participant Ctrl as DisputeController
    participant Agent as CustomerServiceAgent (LangChain4j)
    participant VDB as Vector DB (InMemory)
    participant LLM as Llama 3.1 (Ollama)
    participant Tools as BankingTools
    participant MQ as RabbitMQ

    User->>Ctrl: POST /api/v1/disputes (Message)
    Ctrl->>Agent: evaluateDispute(message)
    
    rect rgb(255, 200, 200)
        Note over Agent, VDB: 🚨 THE BOTTLENECK: Implicit Retrieval
        Agent->>VDB: Auto-search using User's raw message
        VDB-->>Agent: Returns massive block of Bank Policy text
        Agent->>Agent: Appends dense policy text to System Prompt
    end

    Agent->>LLM: Sends bloated prompt to LLM
    
    Note over LLM: LLM suffers Cognitive Overload.<br/>Forgets to call tools, hallucinates variables.
    
    LLM-->>Agent: Returns broken/hallucinated JSON
    Agent-->>Ctrl: Returns DisputeDecision Record
    Ctrl->>MQ: Publishes Event Payload
    MQ-->>User: HTTP 200 OK
