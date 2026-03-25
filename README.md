Intelligent Transaction Dispute & Triage Agent
An event-driven Agentic AI microservice built for the banking domain.

Architecture Highlights:

Framework: Java 17, Spring Boot, LangChain4j

AI Orchestration: Local Llama 3.1 (via Ollama) / OpenAI GPT-4o-mini

Integration: Utilizes strict JSON Structured Outputs and LLM "Tool Calling" to autonomously query mocked core-banking databases via ThreadLocal context binding.

Event-Driven Routing: Asynchronously publishes deterministic fraud routing decisions (e.g., HIGH_PROBABILITY_FRAUD, MANUAL_REVIEW_REQUIRED) to RabbitMQ.
