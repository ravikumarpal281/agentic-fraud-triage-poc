package com.ravi.poc.bankingai.aiconfig;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PolicyTools {

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    public PolicyTools(EmbeddingModel embeddingModel) {
        // Create an isolated vector database just for this tool
        this.embeddingStore = new InMemoryEmbeddingStore<>();
        this.embeddingModel = embeddingModel;
    }

    @PostConstruct
    public void init() throws URISyntaxException {
        // 1. Read the policy document on startup
        Path documentPath = Paths.get(Objects.requireNonNull(getClass().getResource("/bank-policy.txt")).toURI());
        Document document = FileSystemDocumentLoader.loadDocument(documentPath, new TextDocumentParser());

        // 2. Chunk it and store the vectors
        EmbeddingStoreIngestor.builder()
                .documentSplitter(dev.langchain4j.data.document.splitter.DocumentSplitters.recursive(300, 50))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build()
                .ingest(document);

        System.out.println("✅ Bank Policy successfully loaded into Vector DB!");
    }

    @Tool("Searches the official Bank Policy database. Use this to find rules about disputes, locations, or ride-sharing.")
    public String searchBankPolicy(@P("search_text'") String query) {
        // 🚨 DEFENSIVE PROGRAMMING: Catch the LLM's mistake!
        if (query == null || query.trim().isEmpty()) {
            System.out.println("⚠️ AI tried to search with a NULL query. Sending error back to LLM.");
            // Add a hint on exactly how to fix it
            return "SYSTEM ERROR: You passed a null query. You must pass a string value. Try calling this tool again with the query: 'Uber dispute rules'. DO NOT start over.";
        }

        System.out.println("🔍 AI TOOL CALLED: Searching Policy for -> " + query);

        // 1. Convert the AI's search query into a vector
        dev.langchain4j.data.embedding.Embedding queryEmbedding = embeddingModel.embed(query).content();

        // 2. Find the top 2 most relevant paragraphs
        List<EmbeddingMatch<TextSegment>> results = embeddingStore.findRelevant(queryEmbedding, 2, 0.6);

        if (results.isEmpty()) {
            return "FACT: No matching bank policy found for that query. Try a different search.";
        }

        // 3. Return the paragraphs as clear plaintext
        String formattedResults = results.stream()
                .map(match -> match
                        .embedded().text())
                .collect(Collectors.joining("\n---\n"));

        return "FACT: Here are the relevant policy rules:\n" + formattedResults;
    }
}
