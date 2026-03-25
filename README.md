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

Key Takeaway: In Implicit RAG, the Agent orchestrates the database search automatically. The LLM is passive and gets overwhelmed by the injected data.

Agentic RAG
It uses a Reasoning + Acting (ReAct) loop to fetch exactly what it needs, one step at a time, keeping its context window clean.

sequenceDiagram
    autonumber
    actor User as Client (cURL)
    participant Ctrl as DisputeController
    participant Agent as CustomerServiceAgent (LangChain4j)
    participant LLM as Llama 3.1 (Ollama)
    participant BTools as BankingTools
    participant PTools as PolicyTools (Vector DB)
    participant MQ as RabbitMQ

    User->>Ctrl: POST /api/v1/disputes (Message)
    Ctrl->>Agent: evaluateDispute(message)
    Agent->>LLM: Sends CLEAN, short prompt
    
    rect rgb(200, 255, 200)
        Note over LLM, PTools: 🧠 THE SOLUTION: Agentic ReAct Loop
        LLM->>LLM: Reason: "I need the user's facts first."
        LLM->>BTools: Action: Call getCustomerProfile() & getTransactionHistory()
        BTools-->>LLM: Observation: "Leeds" & "London"
        
        LLM->>LLM: Reason: "Now I need the specific policy for an out-of-city dispute."
        LLM->>PTools: Action: Call searchBankPolicy("dispute outside home city")
        PTools-->>LLM: Observation: Returns only ONE targeted sentence.
    end

    LLM->>LLM: Reason: "Facts + Policy = High Fraud. Generating JSON."
    LLM-->>Agent: Returns strict, valid JSON
    Agent-->>Ctrl: Returns DisputeDecision Record
    Ctrl->>MQ: Publishes Event Payload
    MQ-->>User: HTTP 200 OK

    Key Takeaway: In Agentic RAG, the LLM dictates the flow. By moving the Vector Database behind a @Tool (PolicyTools), the LLM only queries the database when it explicitly decides it needs to, and it only reads the tiny snippet of data returned by its specific search query.
