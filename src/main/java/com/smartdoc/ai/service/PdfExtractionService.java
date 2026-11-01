   package com.smartdoc.ai.service;

   import java.io.IOException;
   import java.io.InputStream;
   import java.util.ArrayList;
   import java.util.List;

   import org.apache.pdfbox.Loader;
   import org.apache.pdfbox.pdmodel.PDDocument;
   import org.apache.pdfbox.text.PDFTextStripper;
   import org.springframework.stereotype.Service;

   @Service
   public class PdfExtractionService {

       public record PageText(int pageNumber, String text) {}

       public List<PageText> extractPages(InputStream in) throws IOException {
           List<PageText> pages = new ArrayList<>();
           try (PDDocument doc = Loader.loadPDF(in.readAllBytes())) {
               PDFTextStripper stripper = new PDFTextStripper();
               for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                   stripper.setStartPage(i);
                   stripper.setEndPage(i);
                   String text = stripper.getText(doc).trim();
                   if (!text.isBlank()) {
                       pages.add(new PageText(i, text));
                   }
               }
           }
           return pages;
       }
   }