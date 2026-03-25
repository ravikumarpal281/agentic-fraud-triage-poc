package com.ravi.poc.bankingai.aiconfig;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Configuration
public class AiConfig {
    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        // This creates a new, isolated memory window for every unique memoryId
        // We will keep the last 10 messages (5 user interactions + 5 AI responses)
        return memoryId -> MessageWindowChatMemory.withMaxMessages(10);
    }

    // 1. Configure the local Ollama Embedding Model
    @Bean
    public EmbeddingModel embeddingModel() {
        return OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("nomic-embed-text")
                .build();
    }

    // 2. Configure the RAG Content Retriever
    @Bean
    public ContentRetriever contentRetriever(EmbeddingModel embeddingModel) throws URISyntaxException {
        // Create an in-memory vector database
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Load your bank policy document
        Path documentPath = Paths.get(Objects.requireNonNull(getClass().getResource("/bank-policy.txt")).toURI());
        Document document = FileSystemDocumentLoader.loadDocument(documentPath, new TextDocumentParser());

        // LangChain4j's utility to automatically split the document, embed it, and store it
        dev.langchain4j.store.embedding.EmbeddingStoreIngestor.builder()
                .documentSplitter(dev.langchain4j.data.document.splitter.DocumentSplitters.recursive(300, 50))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build()
                .ingest(document);

        // Return the retriever that the AI Agent will use to search the database
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2) // Fetch the top 2 most relevant paragraphs
                .minScore(0.6) // Minimum similarity score
                .build();
    }
}
