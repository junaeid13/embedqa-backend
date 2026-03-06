-- =====================================================
-- V1__initial_schema.sql
-- Initial database schema for EmbedQA API Testing Platform
-- Author: akash
-- Date: 2025-01-11
-- =====================================================

-- =====================================================
-- API Collections Table
-- Stores collections/folders for organizing API requests
-- =====================================================
CREATE TABLE api_collections (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE INDEX idx_api_collections_name ON api_collections(name);
CREATE INDEX idx_api_collections_created_at ON api_collections(created_at);

COMMENT ON TABLE api_collections IS 'Stores collections/folders for organizing API requests';
COMMENT ON COLUMN api_collections.name IS 'Collection name';
COMMENT ON COLUMN api_collections.description IS 'Collection description';

-- =====================================================
-- Environments Table
-- Stores environment configurations with variables
-- =====================================================
CREATE TABLE environments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    variables_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE INDEX idx_environments_name ON environments(name);

COMMENT ON TABLE environments IS 'Stores environment configurations (dev, staging, prod, etc.)';
COMMENT ON COLUMN environments.variables_json IS 'JSON array of environment variables with name, value, enabled fields';

-- =====================================================
-- API Requests Table
-- Stores saved API requests
-- =====================================================
CREATE TABLE api_requests (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(2048) NOT NULL,
    method VARCHAR(10) NOT NULL,
    description TEXT,
    request_body TEXT,
    body_type VARCHAR(20),
    auth_type VARCHAR(20),
    auth_config TEXT,
    collection_id BIGINT REFERENCES api_collections(id) ON DELETE SET NULL,
    environment_id BIGINT REFERENCES environments(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE INDEX idx_api_requests_name ON api_requests(name);
CREATE INDEX idx_api_requests_method ON api_requests(method);
CREATE INDEX idx_api_requests_collection_id ON api_requests(collection_id);
CREATE INDEX idx_api_requests_environment_id ON api_requests(environment_id);
CREATE INDEX idx_api_requests_created_at ON api_requests(created_at);

COMMENT ON TABLE api_requests IS 'Stores saved API requests';
COMMENT ON COLUMN api_requests.method IS 'HTTP method: GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS';
COMMENT ON COLUMN api_requests.body_type IS 'Body type: NONE, JSON, XML, RAW, FORM_DATA';
COMMENT ON COLUMN api_requests.auth_type IS 'Authentication type: NONE, BASIC_AUTH, BEARER_TOKEN, API_KEY';
COMMENT ON COLUMN api_requests.auth_config IS 'JSON object containing auth configuration';

-- =====================================================
-- Request Headers Table
-- Stores headers for API requests
-- =====================================================
CREATE TABLE request_headers (
    id BIGSERIAL PRIMARY KEY,
    header_name VARCHAR(255) NOT NULL,
    header_value TEXT NOT NULL,
    api_request_id BIGINT NOT NULL REFERENCES api_requests(id) ON DELETE CASCADE,
    api_response_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE INDEX idx_request_headers_api_request_id ON request_headers(api_request_id);
CREATE INDEX idx_request_headers_api_response_id ON request_headers(api_response_id);

COMMENT ON TABLE request_headers IS 'Stores headers for API requests and responses';

-- =====================================================
-- Query Parameters Table
-- Stores query parameters for API requests
-- =====================================================
CREATE TABLE query_parameter (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    value TEXT,
    api_request_id BIGINT REFERENCES api_requests(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE INDEX idx_query_parameter_api_request_id ON query_parameter(api_request_id);

COMMENT ON TABLE query_parameter IS 'Stores query parameters for API requests';

-- =====================================================
-- Environment Variables Table
-- Stores individual environment variables (alternative to JSON)
-- =====================================================
CREATE TABLE environment_variables (
    id BIGSERIAL PRIMARY KEY,
    var_key VARCHAR(255) NOT NULL,
    var_value TEXT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    environment_id BIGINT NOT NULL REFERENCES environments(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE INDEX idx_environment_variables_environment_id ON environment_variables(environment_id);
CREATE INDEX idx_environment_variables_key ON environment_variables(var_key);

COMMENT ON TABLE environment_variables IS 'Stores individual environment variables';

-- =====================================================
-- API Responses Table
-- Stores cached/saved API responses
-- =====================================================
CREATE TABLE api_responses (
    id BIGSERIAL PRIMARY KEY,
    status_code INTEGER,
    status_message VARCHAR(255),
    body TEXT,
    response_time BIGINT,
    size BIGINT,
    metadata TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE INDEX idx_api_responses_status_code ON api_responses(status_code);

COMMENT ON TABLE api_responses IS 'Stores cached/saved API responses';
COMMENT ON COLUMN api_responses.response_time IS 'Response time in milliseconds';
COMMENT ON COLUMN api_responses.size IS 'Response size in bytes';
COMMENT ON COLUMN api_responses.metadata IS 'JSON object containing additional metadata';

-- =====================================================
-- Request History Table
-- Stores history of all executed API requests
-- =====================================================
CREATE TABLE request_history (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(2048) NOT NULL,
    method VARCHAR(10) NOT NULL,
    request_headers TEXT,
    request_body TEXT,
    body_type VARCHAR(20),
    auth_type VARCHAR(20),
    auth_config TEXT,
    query_params TEXT,
    status_code INTEGER NOT NULL,
    status_text VARCHAR(255),
    response_headers TEXT,
    response_body TEXT,
    response_time BIGINT NOT NULL,
    response_size BIGINT,
    executed_at TIMESTAMP NOT NULL,
    api_request_id BIGINT REFERENCES api_requests(id) ON DELETE SET NULL,
    collection_id BIGINT REFERENCES api_collections(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE INDEX idx_request_history_method ON request_history(method);
CREATE INDEX idx_request_history_status_code ON request_history(status_code);
CREATE INDEX idx_request_history_executed_at ON request_history(executed_at);
CREATE INDEX idx_request_history_api_request_id ON request_history(api_request_id);
CREATE INDEX idx_request_history_collection_id ON request_history(collection_id);
CREATE INDEX idx_request_history_url ON request_history(url);

COMMENT ON TABLE request_history IS 'Stores history of all executed API requests';
COMMENT ON COLUMN request_history.request_headers IS 'JSON object of request headers';
COMMENT ON COLUMN request_history.query_params IS 'JSON object of query parameters';
COMMENT ON COLUMN request_history.response_headers IS 'JSON object of response headers';
COMMENT ON COLUMN request_history.response_time IS 'Response time in milliseconds';
COMMENT ON COLUMN request_history.response_size IS 'Response size in bytes';

-- =====================================================
-- Add foreign key for request_headers.api_response_id
-- (After api_responses table is created)
-- =====================================================
ALTER TABLE request_headers 
    ADD CONSTRAINT fk_request_headers_api_response 
    FOREIGN KEY (api_response_id) 
    REFERENCES api_responses(id) 
    ON DELETE SET NULL;
