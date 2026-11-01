   package com.smartdoc.ai.service;

   import java.util.ArrayList;
   import java.util.List;

   import org.springframework.stereotype.Service;

   import com.smartdoc.ai.service.PdfExtractionService.PageText;

   @Service
   public class ChunkingService {

       private static final int CHUNK_SIZE = 800;   // characters
       private static final int OVERLAP = 150;      // characters

       public record Chunk(String documentId, int pageNumber, int chunkIndex, String text) {}

       public List<Chunk> chunk(String documentId, List<PageText> pages) {
           List<Chunk> chunks = new ArrayList<>();
           int index = 0;
           for (PageText page : pages) {
               String text = page.text().replaceAll("\\s+", " ").trim();
               int start = 0;
               while (start < text.length()) {
                   int end = Math.min(start + CHUNK_SIZE, text.length());
                   chunks.add(new Chunk(documentId, page.pageNumber(), index++, text.substring(start, end)));
                   if (end == text.length()) break;
                   start = end - OVERLAP;
               }
           }
           return chunks;
       }
   }