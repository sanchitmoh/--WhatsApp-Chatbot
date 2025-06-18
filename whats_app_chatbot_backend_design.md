**WhatsApp Navigation Chatbot Backend Design**

This document provides a step-by-step guide to design a scalable, maintainable, and production-ready backend system for a WhatsApp chatbot that integrates with Meta’s WhatsApp Business API, Firebase, and is deployed on Render. It assumes you are new to system design but aims to follow industry best practices.

---

## 1. System Architecture Overview

**Goal:** Enable users to interact via WhatsApp, process navigation queries, and respond intelligently.

**Key components:**

1. **WhatsApp Ingress Layer** (Webhook Receiver)
2. **Controller Layer** (Spring Boot REST Controllers)
3. **Service Layer** (Business Logic & Intent Routing)
4. **Repository Layer** (Firebase + SQL config store)
5. **Integration Clients** (WhatsApp API client, Firebase SDK)
6. **Monitoring & Logging** (Prometheus + Grafana + ELK)
7. **CI/CD Pipeline** (GitHub Actions + Render)

```mermaid
flowchart LR
  subgraph Users
    U[WhatsApp User]
  end
  subgraph ChatbotBackend
    A[Webhook Receiver]
    B[Controllers]
    C[Services]
    D[Repositories]
    E[WhatsApp API Client]
    F[Firebase DB]
    G[Config Store (Postgres)]
    H[Monitoring]
    I[Logging]
  end
  U -->|message| A --> B --> C --> D
  C --> E
  B -->|persist| F
  D -->|config| G
  subgraph Observability
      H & I
  end
  ChatbotBackend --> Observability
```

**Step-by-step:**

1. Configure Meta WhatsApp webhook URL in the Business API portal.
2. Webhook Receiver maps incoming events to internal DTOs.
3. Controller validates and forwards to Service.
4. Service applies NLP/intent matching and routing.
5. Service stores conversation logs in Firebase.
6. Service fetches bot configuration from relational store.
7. Responses sent via WhatsApp API client.
8. Metrics emitted; logs shipped to ELK.

---

## 2. Layered Architecture

1. **Controller Layer**

   - Spring `@RestController`
   - Endpoints: `/webhook`, `/health`, `/metrics`, `/admin/config`.
   - Input validation via Hibernate Validator.

2. **Service Layer**

   - Core business logic.
   - Intent router: rule-based or pluggable NLP.
   - Error handling and retries.

3. **Repository Layer**

   - FirebaseFirestoreRepository: stores messages.
   - ConfigRepository (JPA): stores intents/responses in PostgreSQL.

---

## 3. API Design (Swagger/OpenAPI)

```yaml
openapi: 3.0.1
info:
  title: WhatsApp Navigation Chatbot API
  version: '1.0.0'
servers:
  - url: https://api.yourdomain.com
paths:
  /webhook:
    post:
      summary: WhatsApp webhook receiver
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/WhatsAppEvent'
      responses:
        '200':
          description: Received
  /admin/config:
    get:
      summary: List bot intents
      security:
        - ApiKeyAuth: []
      responses:
        '200':
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Intent'
components:
  securitySchemes:
    ApiKeyAuth:
      type: apiKey
      in: header
      name: X-API-Key
  schemas:
    WhatsAppEvent:
      type: object
      properties:
        contacts:
          type: array
        messages:
          type: array
    Intent:
      type: object
      properties:
        id:
          type: string
        trigger:
          type: string
        response:
          type: string
```

> **Next:** Generate full YAML and host via Swagger UI (see Deployment Pipeline).

---

## 4. Database Schema

### 4.1 Firebase (Messages Log)

- **Collection**: `conversations`
  - **Document**: `conversationId`
    - `userId`: string
    - `timestamp`: timestamp
    - `direction`: enum["INBOUND"|"OUTBOUND"]
    - `message`: string
    - `intentId`: string

### 4.2 Relational Store (PostgreSQL on Render)

| Table     | Columns                                                     | Notes                       |
| --------- | ----------------------------------------------------------- | --------------------------- |
| `intents` | `id` (PK UUID), `name`, `trigger`, `response`, `created_at` | Bot config                  |
| `users`   | `id` (PK UUID), `phone_number`, `created_at`                | Registered users (optional) |

> **Step:** Use Spring Data JPA entities and automatic DDL.

---

## 5. Deployment Pipeline (CI/CD)

1. **Repository**: GitHub (branch: `main`, `dev`).
2. **CI**: GitHub Actions `.github/workflows/ci.yml`:
   - Steps: checkout, build (`mvn clean package`), unit tests, static analysis (Checkstyle, SpotBugs).
3. **CD**: On `main` merge:
   - Build Docker image, push to Docker Hub.
   - Deploy to Render via Render API key.
4. **Render**:
   - Two services: `chatbot-api` (Docker) and `postgres` (managed).

---

## 6. Observability Plan

1. **Logging**:
   - SLF4J + Logback.
   - JSON logs shipped to ELK (Elasticsearch, Logstash, Kibana).
2. **Metrics**:
   - Micrometer + Prometheus.
   - Expose `/actuator/metrics`.
3. **Alerting**:
   - Prometheus Alertmanager: rate limit breaches, error spikes.
4. **Tracing**:
   - OpenTelemetry + Jaeger for distributed traces.

---

## 7. Security Best Practices

- **Rate Limiting**: Bucket4j or Spring Cloud Gateway.
- **API Authentication**: API keys for admin endpoints, HMAC for webhook security.
- **Input Validation**: Reject invalid payloads.
- **Secrets Management**: Store keys in Render environment variables.

---

## 8. Testing Strategy

1. **Unit Tests**: JUnit + Mockito for each service/repo.
2. **Integration Tests**: Spring `@SpringBootTest`, using Testcontainers for PostgreSQL.
3. **Contract Tests**: Pact for WhatsApp API stubs.
4. **End-to-End**: Simulate webhook events.

---

### Next Steps for Beginners:

1. Clone [starter template](https://github.com/your-org/whatsapp-chatbot-starter).
2. Follow README: configure env vars, Firebase credentials, WhatsApp API keys.
3. Run locally with `mvn spring-boot:run`, test `/webhook` via Postman.
4. Gradually implement features per above sections.

*This design document equips you with the blueprint and concrete steps to build a robust WhatsApp chatbot backend. Feel free to ask for deeper explanations on any section!*

