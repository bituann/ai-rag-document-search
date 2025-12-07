AI RAG Document Search

> Backend service to upload documents and ask questions about them using Retrieval-Augmented Generation (RAG).

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green.svg)
![Build Status](https://img.shields.io/badge/build-unknown-lightgrey)

## What the project does

`ai-rag-document-search` is a Spring Boot backend that accepts documents (PDF, DOC, DOCX, TXT),
extracts and chunks text, stores documents and chunks, indexes them in a vector store, and answers
user questions using Retrieval-Augmented Generation (RAG) with an LLM.

The service integrates with Spring AI components (OpenAI model starter, Chroma vector store) to
perform similarity search and generate answers from context. It is also integrated with swagger docs which can be
used to test the endpoints 

## Why this is useful

- Upload documents and ask natural-language questions about their contents.
- Uses a vector store (Chroma via Spring AI) for efficient semantic search over document chunks.
- Produces an explained answer that includes the context chunks and similarity scores.
- Simple REST API that can be integrated into apps, chatbots, or document-management systems.

## Key features

- Upload documents via `POST /documents/upload` (multipart file)
- Query documents via `GET /documents/query?query=...&topK=5`
- List stored documents: `GET /documents/`
- Retrieve a document and its chunks: `GET /documents/{id}`

## Quick Start

Prerequisites

- Java 17
- Maven 3.6+
- PostgreSQL (or adapt `spring.datasource` for your DB)
- An OpenAI API key (or other supported LLM provider configured through Spring AI)

Configuration

Copy and edit `src/main/resources/application.properties` (or create a `application-dev.properties`) and set the required properties. At minimum configure the datasource and the model provider credentials, for example:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/ai_rag
spring.datasource.username=postgres
spring.datasource.password=password

# OpenAI (example environment variable approach)
OPENAI_API_KEY=your_openai_api_key_here

# Chroma config (if required by your setup)
# spring.ai.chroma.some.property=...
```

Running the application

From the repository root:

```powershell
Maven package and run
mvn -DskipTests package
java -jar target/ai-rag-document-search-0.0.1-SNAPSHOT.jar

# or for development
mvn spring-boot:run
```

If you use environment variables for secrets, set them in your shell before running.

## REST API (examples)

1) Upload a document

```bash
curl -X POST "http://localhost:8080/documents/upload" -F "file=@/path/to/your.docx"
```

Successful response: HTTP 200 and message `File uploaded successfully`.

2) Query documents

```bash
curl "http://localhost:8080/documents/query?query=What+is+the+purpose+of+this+document&topK=5"
```

Sample `QueryResponse` JSON (fields produced by the service):

```json
{
  "answer": "The document explains...",
  "chunks": ["...chunk text 1...", "...chunk text 2..."],
  "similarityScores": {"...chunk text 1...": 0.92}
}
```

3) List documents

```bash
curl "http://localhost:8080/documents/"
```

4) Retrieve a document by id

```bash
curl "http://localhost:8080/documents/{uuid}"
```

## Project structure (high level)

- `src/main/java/.../controller/AiRagController.java` — REST endpoints
- `src/main/java/.../service/DocumentService*.java` — business logic: upload, query, list, get
- `src/main/java/.../service/DocumentProcessingService.java` — text extraction and chunking
- `src/main/java/.../model/DocumentEntity.java` — JPA entity persisted to `documents` table
- `pom.xml` — Maven build and dependencies (Spring Boot, Spring AI, Chroma, OpenAI, Postgres)

## Environment & dependency notes

- The project uses Spring Boot 3.5.7 and Java 17 (see `pom.xml`).
- Vector store: Chroma (via Spring AI starter). Ensure Chroma is configured and available to the app.
- LLM: configured via `spring-ai-starter-model-openai` — set the provider credentials (for OpenAI set `OPENAI_API_KEY` or follow Spring AI docs).
- Database: PostgreSQL is used in the project; adjust `spring.datasource.*` properties for other DBs.

## Where to get help

- Open an issue in this repository for bugs or feature requests.
- For Spring AI usage consult the Spring AI docs: https://spring.io/projects/spring-ai
- For Chroma and OpenAI configuration, consult their respective docs (Chroma, OpenAI).

## Contributing

Contributions are welcome. Please read `CONTRIBUTING.md` if present. If not present, open an issue to discuss major changes before creating a PR.

## Maintainers

- Repository owner: `bituann` (see GitHub repo for contact details)

## License

See the `LICENSE` file in the repository for license details.

---

File: `README.md` (project root) — created by project maintainer
# ai-rag-document-search
A backend service that takes a document and answers a user's questions on it using Retrieval-Augmented Generation (RAG)
