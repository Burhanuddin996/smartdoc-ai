package com.smartdoc.ai.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

class RagServiceTest {

    private VectorStore vectorStore;
    private ChatClient chatClient;
    private RagService ragService;

    @BeforeEach
    void setUp() {
        vectorStore = mock(VectorStore.class);
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
        when(builder.build()).thenReturn(chatClient);
        ragService = new RagService(vectorStore, builder, 0.6);
    }

    @Test
    void returnsNotFoundAndSkipsGeminiWhenNothingRetrieved() {
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());

        var result = ragService.ask("doc-1", "Who won the world cup?");

        assertEquals("I cannot find this in the uploaded document.", result.answer());
        assertTrue(result.sources().isEmpty());
        verifyNoInteractions(chatClient);
    }

    @Test
    void returnsAnswerAndSourcesWhenChunksFound() {
        Document doc = new Document("JobConnect uses PostgreSQL.",
                Map.<String, Object>of("pageNumber", 2, "chunkIndex", 5));
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc));
        when(chatClient.prompt().system(anyString()).user(anyString()).call().content())
                .thenReturn("PostgreSQL (Page 2)");

        var result = ragService.ask("doc-1", "Which database does JobConnect use?");

        assertEquals("PostgreSQL (Page 2)", result.answer());
        assertEquals(1, result.sources().size());
        assertEquals(2, result.sources().get(0).page());
    }
}