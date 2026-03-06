# Docker Setup

## Quick Start

```bash
# Start services
docker compose up -d --build

# View logs
docker compose logs -f

# Stop services
docker compose down
```

## Custom Port

```bash
SERVER_PORT=9095 docker compose up -d --build
```

## Files

| File | Purpose | Loaded |
|------|---------|--------|
| `docker-compose.yml` | Base config | Always |
| `docker-compose.override.yml` | Dev settings | Auto |
| `docker-compose.prod.yml` | Prod settings | Manual |
| `docker-compose.test.yml` | Test settings | Manual |

## Usage

```bash
# Dev (auto-loads override.yml)
TAG=v1 docker compose up -d --build

# Prod
TAG=v1 DB_PASSWORD=secret docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Test
TAG=v1 docker compose -f docker-compose.yml -f docker-compose.test.yml up -d

# View logs
docker compose logs -f

# Stop
docker compose down
```

## Environment Variables

| Variable | Default |
|----------|---------|
| `SERVER_PORT` | `8085` |
| `DB_PASSWORD` | Required (prod) |
| `TAG` | `latest` |

## Commands

```bash
# Build image
docker build -t akash9229/embedqa:v1 .

# Push to Docker Hub
docker push akash9229/embedqa:v1

# Custom port
SERVER_PORT=9000 docker compose up -d

# Prod with password
TAG=v1 DB_PASSWORD=secret docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Reset everything
docker compose down -v
```

## Health Check

```bash
curl http://localhost:8085/actuator/health
```
