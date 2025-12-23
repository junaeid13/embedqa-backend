# EmbedQA - Professional API Testing Platform

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring_Boot-3.5-green?style=for-the-badge&logo=spring" alt="Spring Boot 3.5">
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql" alt="PostgreSQL">
</p>

REST API backend for the EmbedQA API testing platform.

> 🌐 **Frontend Repository:** [embedqa-ui](https://github.com/AkashBhuiyan/embedqa-ui)

## 🚀 Quick Start

## Tech Stack

| Technology | Version |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.5.7 |
| PostgreSQL | 16 |
| Apache HttpClient | 5.x |

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 16 (Docker or local installation

## Database Setup

### Option 1: Using Docker (Recommended)

```bash
cd docker-compose/db
docker-compose up -d
```

This creates:
- **Database:** `embedqa`
- **User:** `embedqa_user`
- **Password:** `embedqa_pass`
- **Port:** `5432`

To stop the database:
```bash
docker-compose down
```

To stop and remove data:
```bash
docker-compose down -v
```

or
```bash
docker run -d \
  --name embedqa-postgres \
  -e POSTGRES_DB=embedqa \
  -e POSTGRES_USER=embedqa_user \
  -e POSTGRES_PASSWORD=embedqa_pass \
  -p 5432:5432 \
  postgres:16

```


### Option 2: Manual PostgreSQL Installation

#### Linux (Ubuntu/Debian)

```bash
# Install PostgreSQL
sudo apt update
sudo apt install postgresql postgresql-contrib

# Start PostgreSQL service
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Create database and user
sudo -u postgres psql
```

```sql
CREATE DATABASE embedqa;
CREATE USER embedqa_user WITH ENCRYPTED PASSWORD 'embedqa_pass';
GRANT ALL PRIVILEGES ON DATABASE embedqa TO embedqa_user;
\c embedqa
GRANT ALL ON SCHEMA public TO embedqa_user;
\q
```

#### macOS

```bash
# Using Homebrew
brew install postgresql@16
brew services start postgresql@16

# Create database and user
psql postgres
```

```sql
CREATE DATABASE embedqa;
CREATE USER embedqa_user WITH ENCRYPTED PASSWORD 'embedqa_pass';
GRANT ALL PRIVILEGES ON DATABASE embedqa TO embedqa_user;
\c embedqa
GRANT ALL ON SCHEMA public TO embedqa_user;
\q
```

#### Windows

1. Download PostgreSQL from [postgresql.org](https://www.postgresql.org/download/windows/)
2. Run the installer and set password for `postgres` user
3. Open pgAdmin or psql and run:

```sql
CREATE DATABASE embedqa;
CREATE USER embedqa_user WITH ENCRYPTED PASSWORD 'embedqa_pass';
GRANT ALL PRIVILEGES ON DATABASE embedqa TO embedqa_user;
\c embedqa
GRANT ALL ON SCHEMA public TO embedqa_user;


The backend will start at `http://localhost:8085`
```

## 📁 Project Structure

```
src/main/java/com/akash/embedqa/
├── EmbedqaApplication.java
├── config/
│   ├── HttpClientConfig.java
│   └── WebConfig.java
├── controller/
│   ├── ApiExecutorController.java
│   ├── CollectionController.java
│   ├── EnvironmentController.java
│   └── RequestController.java
├── service/
│   ├── ApiExecutorService.java
│   ├── CollectionService.java
│   ├── EnvironmentService.java
│   └── RequestService.java
├── repository/
│   ├── ApiCollectionRepository.java
│   ├── ApiRequestRepository.java
│   ├── ApiResponseRepository.java
│   ├── EnvironmentRepository.java
│   └── EnvironmentVariableRepository.java
├── model/
│   ├── dtos/
│   │   ├── request/
│   │   └── response/
│   └── entities/
├── enums/
│   ├── HttpMethod.java
│   ├── BodyType.java
│   └── AuthType.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ApiExecutionException.java
│   └── ResourceNotFoundException.java
├── converter/
│   ├── AuthConfigConverter.java
│   └── EnvironmentVariablesConverter.java
└── utils/
    └── HashMapConverter.java
```

## API Endpoints

### Execute API

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/execute` | Execute HTTP request |

### Collections

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/collections` | Get all collections |
| GET | `/api/v1/collections/{id}` | Get collection by ID |
| POST | `/api/v1/collections` | Create collection |
| PUT | `/api/v1/collections/{id}` | Update collection |

### Environments

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/environments` | Get all environments |
| GET | `/api/v1/environments/{id}` | Get environment by ID |
| POST | `/api/v1/environments` | Create environment |
| PUT | `/api/v1/environments/{id}` | Update environment |
| DELETE | `/api/v1/environments/{id}` | Delete environment |

### Requests

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/requests` | Get all requests (paginated) |
| GET | `/api/v1/requests/{id}` | Get request by ID |
| POST | `/api/v1/requests` | Save request |
| PUT | `/api/v1/requests/{id}` | Update request |
| DELETE | `/api/v1/requests/{id}` | Delete request |

## 🔧 Configuration

### Application Profiles

| Profile | Database | Description |
|---------|----------|-------------|
| `dev` | PostgreSQL | Development configuration |
| `prod` | PostgreSQL | Production configuration |
| `test` | PostgreSQL | Testing configuration |

### Environment Variables

```bash
# Database Configuration (Production)
DATABASE_URL=jdbc:postgresql://localhost:5432/embedqa
DATABASE_USERNAME=embedqa_user
DATABASE_PASSWORD=embedqa_pass
```

## 📖 API Documentation

Swagger UI is available at: `http://localhost:8085/swagger-ui.html`

OpenAPI spec: `http://localhost:8085/api-docs`


## Contributing

We welcome contributions! Here's how you can help:

### Getting Started

1. **Fork the repository**

2. **Clone your fork**
   ```bash
   git clone https://github.com/YOUR_USERNAME/embedqa.git
   cd embedqa
   ```

3. **Create a branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

4. **Set up the development environment**
   ```bash
   # Start database
   cd docker-compose/db
   docker-compose up -d
   cd ../..
   
   # Run application
   ./mvnw spring-boot:run
   ```

## 🛣️ Roadmap

- [ ] WebSocket support
- [ ] GraphQL support
- [ ] Automated test runner
- [ ] Test assertions builder
- [ ] Request chaining
- [ ] Mock server
- [ ] Team collaboration
- [ ] Request documentation generator


## 👨‍💻 Author

**Akash** - [GitHub](https://github.com/akash)

---

<p align="center">
  Made with ❤️ for developers who need API testing tool
</p>
