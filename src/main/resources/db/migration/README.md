# Database Migrations

This project uses [Flyway](https://flywaydb.org/) for database migrations.

## Migration Files

Migration files are located in `src/main/resources/db/migration/` and follow the naming convention:

```
V{version}__{description}.sql
```

For example:
- `V1__initial_schema.sql` - Initial database schema
- `V2__add_indexes_and_constraints.sql` - Additional indexes and constraints
- `V3__add_new_feature.sql` - Future migrations

## Current Migrations

| Version | Description |
|---------|-------------|
| V1 | Initial schema with all tables (api_collections, environments, api_requests, request_headers, query_parameter, environment_variables, api_responses, request_history) |
| V2 | Additional indexes and constraints for performance and data integrity |

## How Flyway Works

1. **On application startup**, Flyway checks the `flyway_schema_history` table
2. It compares applied migrations with available migration files
3. It applies any new migrations in order
4. Failed migrations will prevent the application from starting

## Configuration

### Development (application-dev.yml)
```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
```

### Production (application-prod.yml)
```yaml
spring:
  flyway:
    enabled: true
    clean-disabled: true  # Never allow clean in production!
    validate-on-migrate: true
```

## Commands

### Using Maven Flyway Plugin (Optional)

Add to `pom.xml` for command-line migrations:

```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <version>10.0.0</version>
    <configuration>
        <url>jdbc:postgresql://localhost:5432/embedqa</url>
        <user>embedqa_user</user>
        <password>embedqa_pass</password>
    </configuration>
</plugin>
```

Then run:
```bash
# Show migration info
mvn flyway:info

# Apply migrations
mvn flyway:migrate

# Validate migrations
mvn flyway:validate

# Clean database (DANGEROUS!)
mvn flyway:clean
```

## Creating New Migrations

1. Create a new file in `src/main/resources/db/migration/`
2. Name it `V{next_version}__{description}.sql`
3. Write your SQL migration
4. Test locally before committing

### Example Migration

```sql
-- V3__add_tags_to_requests.sql
-- Add tags support for API requests

CREATE TABLE request_tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(7) DEFAULT '#808080',
    api_request_id BIGINT NOT NULL REFERENCES api_requests(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_request_tags_request_id ON request_tags(api_request_id);
CREATE INDEX idx_request_tags_name ON request_tags(name);
```

## Best Practices

1. **Never modify applied migrations** - Create a new migration instead
2. **Test migrations locally** before pushing
3. **Keep migrations small and focused** - One feature per migration
4. **Always include rollback plan** in comments for complex migrations
5. **Use transactions** for data migrations (PostgreSQL supports DDL in transactions)

## Troubleshooting

### Migration checksum mismatch
If you accidentally modified an applied migration:
```sql
-- Update the checksum in flyway_schema_history
UPDATE flyway_schema_history 
SET checksum = NULL 
WHERE version = '1';
```
Then restart the application.

### Reset database (Development only!)
```bash
# Using Spring profile
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run -Dspring.flyway.clean-disabled=false

# Or manually
psql -U embedqa_user -d embedqa -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
```