# SmartDoc AI Evaluation

Document: JobConnect_SpringBoot_Interview_Prep.pdf (13 pages, 47 chunks)
Settings: topK=4, chunk size 800 chars, overlap 150, min-score 0.58 (questions 2-7 were first run with 0.5)
Models: Gemini chat model gemini-3.5-flash-lite, embedding model gemini-embedding-001 (768 dimensions)

| # | Question | Type | Right page in sources? | Answer correct? | Top score |
|---|----------|------|------------------------|-----------------|-----------|
| 1 | How is the JWT signed, and what stops someone from forging one? | Exact wording | Pending | Pending | Pending |
| 2 | What HTTP status code do you return for a duplicate application attempt, and why? | Exact wording | Yes (page 13, ranked 1st) | Yes (one citation slightly off) | 0.69 |
| 3 | Why BCryptPasswordEncoder bean specifically, and where is it used? | Exact wording | Yes (page 13 ranked 2nd, page 6 ranked 1st) | Yes | 0.84 |
| 4 | Why hash passwords with BCrypt instead of a fast hash? | Paraphrased | Yes (page 6, ranked 1st) | Yes (cited chunk numbers as pages) | 0.79 |
| 5 | How can a JWT be cancelled before it expires? | Paraphrased | Yes (page 6, ranked 1st) | Yes | 0.67 |
| 6 | How does JobConnect let users search and filter jobs? | Paraphrased | Yes (page 9, ranked 2nd) | Yes | 0.66 (correct chunk 0.63) |
| 7 | How was JobConnect deployed? | Paraphrased | Yes (page 12, ranked 4th) | Yes | 0.66 (correct chunk 0.609) |
| 8 | Who won the 2018 football world cup? | Not in PDF | n/a | Correct refusal, empty sources | none |
| 9 | What is the difference between HashMap and ConcurrentHashMap? | Not in PDF | n/a | Correct refusal, empty sources after setting min-score to 0.58 and also filtering scores in code (with 0.5 the filter let 4 chunks at 0.54-0.56 through) | highest unrelated chunk 0.56 |
| 10 | How do I bake a chocolate cake? | Not in PDF | n/a | Correct refusal, empty sources | none |

## Result
- Questions 2-7: 6 of 6 returned the correct page in sources and a correct answer (questions 6 and 7 to be re-checked at min-score 0.58; question 1 pending).
- Unsupported questions: 3 of 3 correctly refused with empty sources.

## Limitations observed
- Questions that include the project name ("JobConnect") rank generic intro chunks above the specific one, and the correct chunk can land close to the cut-off (0.609 vs 0.58 for question 7).
- In a PDF about one topic, unrelated Java questions still score around 0.54-0.56, close to weak correct matches (0.61), so the cut-off needs tuning per document and leaves a narrow margin.
- Gemini sometimes cited chunk numbers as if they were page numbers; the prompt was updated to cite pages only.
- The vector search's built-in similarity threshold did not filter reliably, so scores are also checked in Java.
- Scanned PDFs are not supported.
## Live demo
 Deployed on Render (Docker) with Neon PostgreSQL + pgvector. Requests need an `X-API-Key` header (available on request). The first request after idle takes about a minute because the free instance sleeps.