package com.smartdoc.ai.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.smartdoc.ai.service.PdfExtractionService.PageText;

class ChunkingServiceTest {

    private final ChunkingService service = new ChunkingService();

    @Test
    void shortPageGivesOneChunkWithMetadata() {
        var chunks = service.chunk("doc-1", List.of(new PageText(3, "Hello world")));

        assertEquals(1, chunks.size());
        assertEquals("doc-1", chunks.get(0).documentId());
        assertEquals(3, chunks.get(0).pageNumber());
    }

    @Test
    void longPageIsSplitWithOverlap() {
        String text = IntStream.range(0, 2000)
                .mapToObj(i -> String.valueOf((char) ('a' + i % 26)))
                .collect(Collectors.joining());

        var chunks = service.chunk("doc-1", List.of(new PageText(1, text)));

        assertTrue(chunks.size() > 1);
        String endOfFirst = chunks.get(0).text().substring(chunks.get(0).text().length() - 150);
        assertEquals(endOfFirst, chunks.get(1).text().substring(0, 150));
    }
}