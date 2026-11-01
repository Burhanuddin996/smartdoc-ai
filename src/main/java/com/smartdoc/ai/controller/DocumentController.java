package com.smartdoc.ai.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.smartdoc.ai.repository.DocumentRepository;
import com.smartdoc.ai.service.ChunkingService;
import com.smartdoc.ai.service.PdfExtractionService;
import com.smartdoc.ai.service.PdfExtractionService.PageText;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final PdfExtractionService pdfService;
    private final ChunkingService chunkingService;
    private final VectorStore vectorStore;
    private final DocumentRepository documentRepository;

    public DocumentController(PdfExtractionService pdfService,
                              ChunkingService chunkingService,
                              VectorStore vectorStore,
                              DocumentRepository documentRepository) {
        this.pdfService = pdfService;
        this.chunkingService = chunkingService;
        this.vectorStore = vectorStore;
        this.documentRepository = documentRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String name = file.getOriginalFilename();
        if (file.isEmpty() || name == null || !name.toLowerCase().endsWith(".pdf")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Upload a non-empty PDF file"));
        }
        List<PageText> pages = pdfService.extractPages(file.getInputStream());
        if (pages.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No text found. Scanned PDFs are not supported"));
        }

        String documentId = UUID.randomUUID().toString();
        List<ChunkingService.Chunk> chunks = chunkingService.chunk(documentId, pages);

        List<Document> docs = chunks.stream()
                .map(c -> new Document(c.text(), Map.<String, Object>of(
                        "documentId", c.documentId(),
                        "filename", name,
                        "pageNumber", c.pageNumber(),
                        "chunkIndex", c.chunkIndex())))
                .toList();
        vectorStore.add(docs);
        documentRepository.save(documentId, name, pages.size(), docs.size());

        return ResponseEntity.ok(Map.of(
                "documentId", documentId,
                "filename", name,
                "pagesIndexed", pages.size(),
                "chunksIndexed", docs.size(),
                "status", "INDEXED"));
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return documentRepository.findAll();
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> delete(@PathVariable String documentId) {
        vectorStore.delete("documentId == '" + documentId + "'");
        int removed = documentRepository.delete(documentId);
        if (removed == 0) {
            return ResponseEntity.status(404).body(Map.of("error", "Document not found"));
        }
        return ResponseEntity.ok(Map.of("deleted", documentId));
    }
}