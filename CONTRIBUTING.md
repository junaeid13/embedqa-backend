# Contributing to EmbedQA Backend

First off, thank you for considering contributing to EmbedQA! 🎉

It's people like you that make EmbedQA such a great tool for API testing. This document provides guidelines and steps for contributing to the backend repository.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Development Setup](#development-setup)
  - [Project Structure](#project-structure)
- [How to Contribute](#how-to-contribute)
  - [Reporting Bugs](#reporting-bugs)
  - [Suggesting Features](#suggesting-features)
  - [Your First Contribution](#your-first-contribution)
  - [Pull Request Process](#pull-request-process)
- [Development Guidelines](#development-guidelines)
  - [Coding Standards](#coding-standards)
  - [Architecture Guidelines](#architecture-guidelines)
  - [Commit Messages](#commit-messages)
  - [Testing](#testing)
- [API Documentation](#api-documentation)
- [Database Migrations](#database-migrations)
- [Community](#community)

## Code of Conduct

This project and everyone participating in it is governed by our [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to [maintainer-akash.bhuiyann@gmail.com].

## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or higher - [Download](https://adoptium.net/)
- **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** or **PostgreSQL 14+** - For database
- **Git** - [Download](https://git-scm.com/)
- **IDE** (recommended):
  - [IntelliJ IDEA](https://www.jetbrains.com/idea/) (recommended)
  - [Eclipse](https://www.eclipse.org/downloads/)
  - [VS Code](https://code.visualstudio.com/) with Java extensions

### Development Setup

1. **Fork the repository**
   
   Click the "Fork" button at the top right of the [EmbedQA repository](https://github.com/AkashBhuiyan/embedqa).

2. **Clone your fork**
   ```bash
   git clone https://github.com/YOUR_USERNAME/embedqa.git
   cd embedqa
   ```

3. **Add upstream remote**
   ```bash
   git remote add upstream https://github.com/your-org/embedqa.git
   ```

4. **Set up the database**
   
   **MySQL:**
   ```sql
   CREATE DATABASE embedqa;
   CREATE USER 'embedqa'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON embedqa.* TO 'embedqa'@'localhost';
   FLUSH PRIVILEGES;
   ```
   
   **PostgreSQL:**
   ```sql
   CREATE DATABASE embedqa;
   CREATE USER embedqa WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE embedqa TO embedqa;
   ```

5. **Configure application properties**
   ```bash
   cp src/main/resources/application.example.yml src/main/resources/application.yml
   ```
   
   Update `application.yml` with your database credentials:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/embedqa
       username: embedqa
       password: your_password
     jpa:
       hibernate:
         ddl-auto: update
       show-sql: true
   ```

6. **Build the project**
   ```bash
   mvn clean install
   ```

7. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   
   Or run from your IDE by executing the main class `EmbedQaApplication.java`.

8. **Verify the setup**
   - Application starts at `http://localhost:8080`
   - Swagger UI available at `http://localhost:8080/swagger-ui.html`
   - Health check: `curl http://localhost:8080/actuator/health`

## How to Contribute

### Reporting Bugs

Before creating bug reports, please check the [existing issues](https://github.com/AkashBhuiyan/embedqa/issues) to avoid duplicates.

When creating a bug report, please include:

1. **Clear title** - Descriptive summary of the issue
2. **Environment details**
   - Java version
   - Spring Boot version
   - Database type and version
   - Operating system
3. **Steps to reproduce** - Detailed steps to reproduce the behavior
4. **Expected behavior** - What you expected to happen
5. **Actual behavior** - What actually happened
6. **Logs** - Relevant stack traces or error logs
7. **Request/Response** - API request and response (if applicable)

**Example:**
```markdown
## Bug Report

**Title:** POST request body not sent when using FORM_DATA type

**Environment:**
- Java: 17.0.9
- Spring Boot: 3.2.1
- MySQL: 8.0.35
- OS: Ubuntu 22.04

**Steps to Reproduce:**
1. Send POST request via `/api/v1/execute`
2. Set bodyType to "FORM_DATA"
3. Include formData array with key-value pairs
4. Execute request

**Expected:** Form data should be sent as application/x-www-form-urlencoded
**Actual:** 415 Unsupported Media Type error

**Request:**
```json
{
  "url": "https://httpbin.org/post",
  "method": "POST",
  "bodyType": "FORM_DATA",
  "formData": [{"key": "name", "value": "test", "enabled": true}]
}
```

**Error Log:**
```
org.springframework.web.client.HttpClientErrorException$UnsupportedMediaType: 415
```
```

### Suggesting Features

Feature requests are welcome! Please check existing issues and discussions before creating a new one.

**When suggesting a feature:**

1. **Use a clear title** - Start with "Feature:" prefix
2. **Describe the problem** - What problem does this solve?
3. **Describe the solution** - How should it work?
4. **API Design** - Proposed endpoints, request/response format
5. **Consider alternatives** - What alternatives have you considered?

### Your First Contribution

Looking for something to work on? Check out:

- Issues labeled [`good first issue`](https://github.com/your-org/embedqa/labels/good%20first%20issue) - Great for newcomers
- Issues labeled [`help wanted`](https://github.com/your-org/embedqa/labels/help%20wanted) - Extra attention needed
- Issues labeled [`documentation`](https://github.com/your-org/embedqa/labels/documentation) - Improve our docs

**First time contributing to open source?** Check out [How to Contribute to Open Source](https://opensource.guide/how-to-contribute/).

### Pull Request Process

1. **Create a branch**
   ```bash
   # Update your local main branch
   git checkout main
   git pull upstream main
   
   # Create a feature branch
   git checkout -b feature/your-feature-name
   
   # Or for bug fixes
   git checkout -b fix/issue-number-description
   ```

   **Branch naming conventions:**
   - `feature/description` - New features
   - `fix/issue-number-description` - Bug fixes
   - `docs/description` - Documentation changes
   - `refactor/description` - Code refactoring
   - `test/description` - Adding tests

2. **Make your changes**
   - Write clean, readable code
   - Follow the [coding standards](#coding-standards)
   - Add/update tests as needed
   - Update documentation if necessary

3. **Run tests and checks**
   ```bash
   # Run all tests
   mvn test
   
   # Run with coverage
   mvn test jacoco:report
   
   # Check code style (if Checkstyle is configured)
   mvn checkstyle:check
   ```

4. **Commit your changes**
   ```bash
   git add .
   git commit -m "feat: add form data support in API executor"
   ```
   See [Commit Messages](https://github.com/AkashBhuiyan/embedqa/commit/97d5254aedcc9605049e9d346431e6704c941daf) for format guidelines.

5. **Keep your branch updated**
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

6. **Push your branch**
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Create a Pull Request**
   - Go to your fork on GitHub
   - Click "Compare & pull request"
   - Fill in the PR template
   - Link related issues using keywords (e.g., "Closes #123")

8. **PR Review Process**
   - Maintainers will review your PR
   - Address any requested changes
   - Once approved, your PR will be merged

**PR Requirements:**
- [ ] All tests pass
- [ ] Code follows style guidelines
- [ ] New code has test coverage
- [ ] Documentation updated (if applicable)
- [ ] Commits follow conventional format
- [ ] PR description is complete
- [ ] No merge conflicts

## Development Guidelines

### Coding Standards

We follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) with some modifications.

**General Rules:**
- Use 4 spaces for indentation (no tabs)
- Maximum line length: 120 characters
- Use meaningful variable and method names
- Write self-documenting code
- Add JavaDoc for public APIs

**Naming Conventions:**
```
| Type    | Convention  | Example |
|---------|-------------|----------------------|
| Classes | PascalCase  | `ApiExecutorService` |
| Methods | camelCase   | `executeRequest()`   |
| Variables| camelCase  | `requestBody`        |
| Constants| UPPER_SNAKE_CASE | `MAX_TIMEOUT`  |
| Packages | lowercase  | `com.akash.embedqa`  |
```
**Code Organization:**
```java
public class MyService {
    // 1. Static fields
    private static final Logger log = LoggerFactory.getLogger(MyService.class);
    
    // 2. Instance fields
    private final MyRepository repository;
    private final AnotherService anotherService;
    
    // 3. Constructors
    public MyService(MyRepository repository, AnotherService anotherService) {
        this.repository = repository;
        this.anotherService = anotherService;
    }
    
    // 4. Public methods
    public MyDto findById(Long id) {
        return repository.findById(id)
            .map(this::mapToDto)
            .orElseThrow(() -> new ResourceNotFoundException("Entity", id));
    }
    
    // 5. Private methods
    private MyDto mapToDto(MyEntity entity) {
        // mapping logic
    }
}
```

### Architecture Guidelines

**Layered Architecture:**

```
┌─────────────────────────────────────┐
│           Controller Layer          │  ← HTTP handling, validation
├─────────────────────────────────────┤
│            Service Layer            │  ← Business logic
├─────────────────────────────────────┤
│          Repository Layer           │  ← Data access
├─────────────────────────────────────┤
│            Database                 │
└─────────────────────────────────────┘
```

**Controller Guidelines:**
- Handle HTTP concerns only
- Validate input using `@Valid`
- Return consistent response format (`ApiResult<T>`)
- Use appropriate HTTP status codes
- Document with Swagger annotations

```java
@RestController
@RequestMapping("/api/v1/collections")
@Tag(name = "Collections", description = "Collection management APIs")
@RequiredArgsConstructor
public class CollectionController {
    
    private final CollectionService collectionService;
    
    @GetMapping("/{id}")
    @Operation(summary = "Get collection by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "404", description = "Collection not found")
    })
    public ApiResult<CollectionDTO> getById(@PathVariable Long id) {
        return ApiResult.success(collectionService.findById(id));
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new collection")
    public ApiResult<CollectionDTO> create(@Valid @RequestBody CreateCollectionDTO dto) {
        return ApiResult.success(collectionService.create(dto));
    }
}
```

**Service Guidelines:**
- Contain business logic
- Use interfaces for abstraction
- Handle transactions with `@Transactional`
- Throw custom exceptions for error cases

```java
public interface CollectionService {
    CollectionDTO findById(Long id);
    CollectionDTO create(CreateCollectionDTO dto);
    CollectionDTO update(Long id, UpdateCollectionDTO dto);
    void delete(Long id);
}

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CollectionServiceImpl implements CollectionService {
    
    private final CollectionRepository repository;
    
    @Override
    public CollectionDTO findById(Long id) {
        log.debug("Finding collection by ID: {}", id);
        return repository.findById(id)
            .map(this::mapToDto)
            .orElseThrow(() -> new ResourceNotFoundException("Collection", id));
    }
    
    @Override
    @Transactional
    public CollectionDTO create(CreateCollectionDTO dto) {
        log.debug("Creating collection: {}", dto.getName());
        ApiCollection collection = ApiCollection.builder()
            .name(dto.getName())
            .description(dto.getDescription())
            .build();
        return mapToDto(repository.save(collection));
    }
}
```

**Entity Guidelines:**
- Use Lombok annotations to reduce boilerplate
- Define relationships carefully
- Use `@Builder` for flexible object creation
- Add audit fields (`createdAt`, `updatedAt`)

```java
@Entity
@Table(name = "api_collections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiCollection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ApiRequest> requests = new ArrayList<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Helper methods for bidirectional relationships
    public void addRequest(ApiRequest request) {
        requests.add(request);
        request.setCollection(this);
    }
    
    public void removeRequest(ApiRequest request) {
        requests.remove(request);
        request.setCollection(null);
    }
}
```

**DTO Guidelines:**
- Separate DTOs for request and response
- Use validation annotations
- Use records for simple DTOs (Java 17+)

```java
// Request DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCollectionDTO {
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
}

// Response DTO (using record)
public record CollectionDTO(
    Long id,
    String name,
    String description,
    int requestCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

### Commit Messages

We use [Conventional Commits](https://www.conventionalcommits.org/) format:

```
<type>(<scope>): <subject>

[optional body]

[optional footer]
```

**Types:**
- `feat` - New feature
- `fix` - Bug fix
- `docs` - Documentation changes
- `style` - Code style changes (formatting, etc.)
- `refactor` - Code refactoring
- `perf` - Performance improvements
- `test` - Adding or updating tests
- `chore` - Maintenance tasks

**Scope (optional):** Module or area affected
- `executor`, `collection`, `request`, `environment`, `history`, `auth`, `config`

**Examples:**
```bash
feat(executor): add form data support for POST requests

fix(history): resolve pagination returning incorrect total count

docs: add API documentation for environment endpoints

refactor(collection): extract mapping logic to separate class

test(executor): add integration tests for request execution

chore: upgrade Spring Boot to 3.2.1
```

**Rules:**
- Use imperative mood ("add" not "added" or "adds")
- Don't capitalize first letter
- No period at the end
- Keep subject under 72 characters
- Reference issues in footer: `Closes #123`

### Testing

**Test Structure:**
```
src/test/java/com/akash/embedqa/
├── controller/           # Controller/Integration tests
├── service/              # Service unit tests
├── repository/           # Repository tests
```

**Running Tests:**
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CollectionServiceTest

# Run with coverage report
mvn test jacoco:report
# Report at: target/site/jacoco/index.html

# Run integration tests only
mvn verify -P integration-test
```

**Unit Test Guidelines:**
- Test one behavior per test method
- Use descriptive test names
- Follow Arrange-Act-Assert pattern
- Mock external dependencies
- Aim for 80%+ code coverage

**Example Service Test:**
```java
@ExtendWith(MockitoExtension.class)
class CollectionServiceImplTest {
    
    @Mock
    private CollectionRepository repository;
    
    @InjectMocks
    private CollectionServiceImpl service;
    
    @Test
    @DisplayName("Should return collection when found by ID")
    void findById_WhenExists_ReturnsCollection() {
        // Arrange
        Long id = 1L;
        ApiCollection collection = ApiCollection.builder()
            .id(id)
            .name("Test Collection")
            .build();
        when(repository.findById(id)).thenReturn(Optional.of(collection));
        
        // Act
        CollectionDTO result = service.findById(id);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("Test Collection");
        verify(repository).findById(id);
    }
    
    @Test
    @DisplayName("Should throw exception when collection not found")
    void findById_WhenNotExists_ThrowsException() {
        // Arrange
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> service.findById(id))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Collection")
            .hasMessageContaining("999");
    }
    
    @Test
    @DisplayName("Should create collection successfully")
    void create_WithValidData_ReturnsCreatedCollection() {
        // Arrange
        CreateCollectionDTO dto = new CreateCollectionDTO("New Collection", "Description");
        ApiCollection saved = ApiCollection.builder()
            .id(1L)
            .name(dto.getName())
            .description(dto.getDescription())
            .build();
        when(repository.save(any())).thenReturn(saved);
        
        // Act
        CollectionDTO result = service.create(dto);
        
        // Assert
        assertThat(result.name()).isEqualTo("New Collection");
        verify(repository).save(any(ApiCollection.class));
    }
}
```

## API Documentation

We use **Swagger/OpenAPI** for API documentation.

**Access Swagger UI:**
- Local: `http://localhost:8085/swagger-ui.html`
- API Docs JSON: `http://localhost:8085/v3/api-docs`

**Adding Documentation:**
```java
@Operation(
    summary = "Execute API request",
    description = "Executes an HTTP request and returns the response"
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Request executed successfully",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request parameters"
    )
})
@PostMapping("/execute")
public ApiResult<ApiResponseDTO> execute(@Valid @RequestBody ExecuteRequestDTO request) {
    // ...
}
```

## Database Migrations

If using **Flyway** for database migrations:

**Creating a migration:**
```bash
# Location: src/main/resources/db/migration/
# Naming: V{version}__{description}.sql

# Example:
V1__create_collections_table.sql
V2__create_requests_table.sql
V3__add_environment_id_to_requests.sql
```

**Migration file example:**
```sql
-- V3__add_environment_id_to_requests.sql

ALTER TABLE api_requests
ADD COLUMN environment_id BIGINT,
ADD CONSTRAINT fk_request_environment
    FOREIGN KEY (environment_id)
    REFERENCES environments(id)
    ON DELETE SET NULL;
```

**Running migrations:**
```bash
# Migrations run automatically on startup
mvn spring-boot:run

# Or manually
mvn flyway:migrate
```

## Community

- **Discussions:** [GitHub Discussions](https://github.com/AkashBhuiyan/embedqa/discussions)
- **Issues:** [GitHub Issues](https://github.com/AkashBhuiyan/embedqa/issues)

### Getting Help

If you need help:

1. Check the [documentation](README.md)
2. Check the [API docs](http://localhost:8085/swagger-ui.html)
3. Search [existing issues](https://github.com/AkashBhuiyan/embedqa/issues)
4. Ask in [Discussions](https://github.com/AkashBhuiyan/embedqa/discussions)
5. Create a new issue with the `question` label

---

## Recognition

Contributors are recognized in our [README](README.md#contributors) and release notes. We appreciate every contribution, big or small!

---

## License

By contributing, you agree that your contributions will be licensed under the same license as the project.

---

Thank you for contributing to EmbedQA! 🙏

*Happy coding!* ☕
