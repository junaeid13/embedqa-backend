-- =====================================================
-- V2__add_indexes_and_constraints.sql
-- Additional indexes for performance optimization
-- Author: akash
-- Date: 2025-01-11
-- =====================================================

-- =====================================================
-- Composite Indexes for Common Queries
-- =====================================================

-- Request history: filter by method and status
CREATE INDEX idx_request_history_method_status 
    ON request_history(method, status_code);

-- Request history: filter by date range and status
CREATE INDEX idx_request_history_executed_status 
    ON request_history(executed_at DESC, status_code);

-- API requests: search by collection and method
CREATE INDEX idx_api_requests_collection_method 
    ON api_requests(collection_id, method);

-- =====================================================
-- Full-text search indexes (PostgreSQL specific)
-- =====================================================

-- Full-text search on request history URL
CREATE INDEX idx_request_history_url_gin 
    ON request_history USING gin(to_tsvector('english', url));

-- Full-text search on collection name and description
CREATE INDEX idx_api_collections_search 
    ON api_collections USING gin(
        to_tsvector('english', COALESCE(name, '') || ' ' || COALESCE(description, ''))
    );

-- Full-text search on request name and description
CREATE INDEX idx_api_requests_search 
    ON api_requests USING gin(
        to_tsvector('english', COALESCE(name, '') || ' ' || COALESCE(description, ''))
    );

-- =====================================================
-- Constraints for data integrity
-- =====================================================

-- Ensure HTTP method is valid
ALTER TABLE api_requests 
    ADD CONSTRAINT chk_api_requests_method 
    CHECK (method IN ('GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'HEAD', 'OPTIONS'));

ALTER TABLE request_history 
    ADD CONSTRAINT chk_request_history_method 
    CHECK (method IN ('GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'HEAD', 'OPTIONS'));

-- Ensure body type is valid (if specified)
ALTER TABLE api_requests 
    ADD CONSTRAINT chk_api_requests_body_type 
    CHECK (body_type IS NULL OR body_type IN ('NONE', 'JSON', 'XML', 'RAW', 'FORM_DATA'));

ALTER TABLE request_history 
    ADD CONSTRAINT chk_request_history_body_type 
    CHECK (body_type IS NULL OR body_type IN ('NONE', 'JSON', 'XML', 'RAW', 'FORM_DATA'));

-- Ensure auth type is valid (if specified)
ALTER TABLE api_requests 
    ADD CONSTRAINT chk_api_requests_auth_type 
    CHECK (auth_type IS NULL OR auth_type IN ('NONE', 'BASIC_AUTH', 'BEARER_TOKEN', 'API_KEY'));

ALTER TABLE request_history 
    ADD CONSTRAINT chk_request_history_auth_type 
    CHECK (auth_type IS NULL OR auth_type IN ('NONE', 'BASIC_AUTH', 'BEARER_TOKEN', 'API_KEY'));

-- Ensure response time is positive
ALTER TABLE request_history 
    ADD CONSTRAINT chk_request_history_response_time_positive 
    CHECK (response_time >= 0);

-- Ensure response size is positive (if specified)
ALTER TABLE request_history 
    ADD CONSTRAINT chk_request_history_response_size_positive 
    CHECK (response_size IS NULL OR response_size >= 0);

-- Ensure status code is valid HTTP status
ALTER TABLE request_history 
    ADD CONSTRAINT chk_request_history_status_code_valid 
    CHECK (status_code >= 100 AND status_code < 600);

ALTER TABLE api_responses 
    ADD CONSTRAINT chk_api_responses_status_code_valid 
    CHECK (status_code IS NULL OR (status_code >= 100 AND status_code < 600));
