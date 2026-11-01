# SmartDoc AI

Upload a text-based PDF, ask questions about it, and get answers grounded in the document, with page references. Built with Java, Spring Boot, Spring AI, Gemini, and PostgreSQL + pgvector (retrieval-augmented generation).

## Features
- Upload a PDF (type and size validated) and extract text page by page
- Overlapping chunking with page metadata kept on every chunk
- Gemini embeddings stored in pgvector and searched per document
- Answers with sources (page, chunk, score, excerpt)
- Refuses questions the document does not answer: a similarity cut-off removes weak matches before the model is called
- List and delete documents (metadata and vectors)

## Architecture
Client (Postman) -> Spring Boot REST controllers -> services
-> Spring AI -> Gemini (embedding model + chat model)
-> PostgreSQL + pgvector (chunk text, vectors, metadata)

Upload flow: PDF -> PDFBox text per page -> chunks (800 characters, 150 overlap) -> embeddings -> pgvector.
Ask flow: question -> embedding -> top 4 chunks of that document with score >= 0.58 -> prompt -> Gemini -> answer + sources.

## Run locally
Prerequisites: Java 21, Docker, a Gemini API key from Google AI Studio.

1. Start the database: `docker compose up -d`
2. Set the environment variable `GEMINI_API_KEY`
3. Run: `./mvnw spring-boot:run` (or run `AiApplication` from your IDE)

## API
| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/documents/upload` | form-data, key `file` (PDF) |
| GET | `/api/documents` | list indexed documents |
| DELETE | `/api/documents/{documentId}` | delete a document and its vectors |
| POST | `/api/questions/ask` | body: `{ "documentId": "...", "question": "..." }` |

Example response:
{ "answer": "...", "sources": [ { "documentId": "...", "page": 13, "chunkIndex": 43, "score": 0.69, "excerpt": "..." } ] }

## Evaluation
Tested manually on a 13-page PDF with 10 questions (7 answerable, 3 not). Details and limitations are in EVALUATION.md.

## Limitations
- Text-based PDFs only (no OCR for scanned files)
- No authentication
- The relevance cut-off (0.58) was tuned on one document and may need adjusting for others
- Questions that repeat the project name can rank generic chunks highly

## Future improvements
React chat UI, streaming responses, hybrid keyword + vector search, reranking, authentication with per-user documents.