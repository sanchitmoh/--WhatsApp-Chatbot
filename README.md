# WhatsApp Navigation Chatbot Backend

A production-ready Spring Boot backend for a WhatsApp chatbot that integrates with Meta's WhatsApp Business API, Firebase Firestore, and PostgreSQL.

## Features

- WhatsApp Business API integration (webhook + client)
- Firebase Firestore for conversation logging
- PostgreSQL for intent storage
- Rate limiting with Bucket4j
- API key authentication
- Prometheus metrics and JSON logging
- Swagger/OpenAPI documentation
- Docker containerization
- GitHub Actions CI/CD pipeline

## Prerequisites

- Java 17 or later
- Maven 3.9 or later
- Docker and Docker Compose
- PostgreSQL 15
- Firebase project with Firestore
- WhatsApp Business API account
- GitHub account
- Render account (for deployment)

## Local Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-org/whatsapp-chatbot.git
   cd whatsapp-chatbot/backend
   ```

2. Create a Firebase project and download the service account key:
   - Go to Firebase Console
   - Create a new project
   - Enable Firestore
   - Generate a new private key
   - Save as `src/main/resources/firebase-credentials.json`

3. Set up environment variables:
   ```bash
   # WhatsApp API
   export WHATSAPP_API_URL=https://graph.facebook.com
   export WHATSAPP_ACCESS_TOKEN=your_access_token
   export WHATSAPP_VERIFY_TOKEN=your_verify_token
   export WHATSAPP_PHONE_NUMBER_ID=your_phone_number_id

   # Firebase
   export FIREBASE_PROJECT_ID=your_project_id
   export FIREBASE_CREDENTIALS_PATH=classpath:firebase-credentials.json

   # Security
   export API_KEY=your_api_key

   # Database
   export POSTGRES_URL=jdbc:postgresql://localhost:5432/chatbot
   export POSTGRES_USER=postgres
   export POSTGRES_PASSWORD=postgres
   ```

4. Start PostgreSQL using Docker:
   ```bash
   docker-compose up -d postgres
   ```

5. Build and run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will be available at `http://localhost:8080/api`.

## API Documentation

Once the application is running, you can access:
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI spec: `http://localhost:8080/api/api-docs`

## Testing

Run the test suite:
```bash
mvn test
```

## Docker Build

Build the Docker image:
```bash
docker build -t whatsapp-chatbot .
```

Run the container:
```bash
docker run -p 8080:8080 \
  -e WHATSAPP_ACCESS_TOKEN=your_token \
  -e WHATSAPP_VERIFY_TOKEN=your_token \
  -e WHATSAPP_PHONE_NUMBER_ID=your_id \
  -e FIREBASE_PROJECT_ID=your_project_id \
  -e API_KEY=your_api_key \
  -e POSTGRES_URL=jdbc:postgresql://host.docker.internal:5432/chatbot \
  whatsapp-chatbot
```

## Deployment

1. Set up GitHub repository secrets:
   - `DOCKERHUB_USERNAME`: Your Docker Hub username
   - `DOCKERHUB_TOKEN`: Your Docker Hub access token
   - `RENDER_API_KEY`: Your Render API key
   - `RENDER_SERVICE_ID`: Your Render service ID

2. Push to main branch to trigger deployment:
   ```bash
   git push origin main
   ```

The GitHub Actions workflow will:
1. Build and test the application
2. Build and push Docker image
3. Deploy to Render

## Monitoring

- Health check: `http://localhost:8080/api/actuator/health`
- Metrics: `http://localhost:8080/api/actuator/prometheus`
- Logs: JSON format in console/log files

## Security

- API key authentication for admin endpoints
- Rate limiting per client
- Input validation
- Secure secrets management
- Non-root Docker user

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 
