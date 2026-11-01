   package com.smartdoc.ai.controller;

   import org.springframework.web.bind.annotation.PostMapping;
   import org.springframework.web.bind.annotation.RequestBody;
   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RestController;

   import com.smartdoc.ai.service.RagService;
   import com.smartdoc.ai.service.RagService.AskResult;

   @RestController
   @RequestMapping("/api/questions")
   public class QuestionController {

       private final RagService ragService;

       public QuestionController(RagService ragService) {
           this.ragService = ragService;
       }

       public record AskRequest(String documentId, String question) {}

       @PostMapping("/ask")
       public AskResult ask(@RequestBody AskRequest request) {
           return ragService.ask(request.documentId(), request.question());
       }
   }