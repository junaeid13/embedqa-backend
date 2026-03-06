package com.akash.embedqa.service;

import com.akash.embedqa.enums.HttpMethod;
import com.akash.embedqa.exception.ResourceNotFoundException;
import com.akash.embedqa.model.dtos.response.HistoryResponseDTO;
import com.akash.embedqa.model.entities.ApiCollection;
import com.akash.embedqa.model.entities.ApiRequest;
import com.akash.embedqa.model.entities.RequestHistory;
import com.akash.embedqa.repository.RequestHistoryRepository;
import com.akash.embedqa.service.impl.HistoryServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Author: akash
 * Date: 11/1/26
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HistoryService Unit Tests")
class HistoryServiceImplTest {

    @Mock
    private RequestHistoryRepository historyRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private HistoryServiceImpl historyService;

    @Captor
    private ArgumentCaptor<RequestHistory> historyCaptor;

    @Captor
    private ArgumentCaptor<LocalDateTime> dateCaptor;

    private RequestHistory testHistory;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        testHistory = RequestHistory.builder()
                .id(1L)
                .url("https://api.example.com/users")
                .method(HttpMethod.GET)
                .requestHeaders("{\"Authorization\":\"Bearer token\"}")
                .queryParams("{\"page\":\"1\"}")
                .requestBody(null)
                .bodyType("NONE")
                .authType("BEARER_TOKEN")
                .authConfig("{\"token\":\"bearer-token\"}")
                .statusCode(200)
                .statusText("OK")
                .responseHeaders("{\"Content-Type\":\"application/json\"}")
                .responseBody("{\"users\":[]}")
                .responseTime(150L)
                .responseSize(100L)
                .executedAt(now)
                .build();
    }

    @Nested
    @DisplayName("saveHistory() Tests")
    class SaveHistoryTests {

        @Test
        @DisplayName("Should save history entry")
        void saveHistory_SavesEntry() {
            // Arrange
            when(historyRepository.save(any(RequestHistory.class))).thenReturn(testHistory);

            // Act
            RequestHistory result = historyService.saveHistory(testHistory);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUrl()).isEqualTo("https://api.example.com/users");
            verify(historyRepository).save(testHistory);
        }

        @Test
        @DisplayName("Should save history with all fields")
        void saveHistory_SavesAllFields() {
            // Arrange
            when(historyRepository.save(any(RequestHistory.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            historyService.saveHistory(testHistory);

            // Assert
            verify(historyRepository).save(historyCaptor.capture());
            RequestHistory captured = historyCaptor.getValue();
            assertThat(captured.getMethod()).isEqualTo(HttpMethod.GET);
            assertThat(captured.getStatusCode()).isEqualTo(200);
            assertThat(captured.getResponseTime()).isEqualTo(150L);
        }
    }

    @Nested
    @DisplayName("getHistory() Tests")
    class GetHistoryTests {

        @Test
        @DisplayName("Should return paginated history")
        void getHistory_ReturnsPaginatedHistory() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<RequestHistory> histories = List.of(testHistory);
            Page<RequestHistory> historyPage = new PageImpl<>(histories, pageable, 1);

            when(historyRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(historyPage);

            // Act
            Page<HistoryResponseDTO> result = historyService.getHistory(
                    null, null, null, null, null, pageable
            );

            // Assert
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getUrl()).isEqualTo("https://api.example.com/users");
        }

        @Test
        @DisplayName("Should filter by HTTP method")
        void getHistory_WithMethodFilter_FiltersResults() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<RequestHistory> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(historyRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(emptyPage);

            // Act
            Page<HistoryResponseDTO> result = historyService.getHistory(
                    HttpMethod.POST, null, null, null, null, pageable
            );

            // Assert
            verify(historyRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Should filter by status code 200 (success range)")
        void getHistory_WithStatusCode200_FiltersSuccessRange() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<RequestHistory> page = new PageImpl<>(List.of(testHistory), pageable, 1);

            when(historyRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(page);

            // Act
            Page<HistoryResponseDTO> result = historyService.getHistory(
                    null, 200, null, null, null, pageable
            );

            // Assert
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should filter by status code 400+ (error range)")
        void getHistory_WithStatusCode400_FiltersErrorRange() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<RequestHistory> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(historyRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(emptyPage);

            // Act
            Page<HistoryResponseDTO> result = historyService.getHistory(
                    null, 400, null, null, null, pageable
            );

            // Assert
            verify(historyRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Should filter by search term in URL")
        void getHistory_WithSearchFilter_FiltersResults() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<RequestHistory> page = new PageImpl<>(List.of(testHistory), pageable, 1);

            when(historyRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(page);

            // Act
            Page<HistoryResponseDTO> result = historyService.getHistory(
                    null, null, "users", null, null, pageable
            );

            // Assert
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should filter by date range")
        void getHistory_WithDateRange_FiltersResults() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            LocalDateTime fromDate = now.minusDays(7);
            LocalDateTime toDate = now;
            Page<RequestHistory> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(historyRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(emptyPage);

            // Act
            Page<HistoryResponseDTO> result = historyService.getHistory(
                    null, null, null, fromDate, toDate, pageable
            );

            // Assert
            verify(historyRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Should return empty page when no history exists")
        void getHistory_WhenEmpty_ReturnsEmptyPage() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<RequestHistory> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(historyRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(emptyPage);

            // Act
            Page<HistoryResponseDTO> result = historyService.getHistory(
                    null, null, null, null, null, pageable
            );

            // Assert
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("Should apply all filters together")
        void getHistory_WithAllFilters_AppliesAllFilters() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            LocalDateTime fromDate = now.minusDays(7);
            LocalDateTime toDate = now;
            Page<RequestHistory> page = new PageImpl<>(List.of(testHistory), pageable, 1);

            when(historyRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(page);

            // Act
            Page<HistoryResponseDTO> result = historyService.getHistory(
                    HttpMethod.GET, 200, "users", fromDate, toDate, pageable
            );

            // Assert
            assertThat(result.getContent()).hasSize(1);
            verify(historyRepository).findAll(any(Specification.class), eq(pageable));
        }
    }

    @Nested
    @DisplayName("getById() Tests")
    class GetByIdTests {

        @Test
        @DisplayName("Should return history entry when found")
        void getById_WhenExists_ReturnsHistory() {
            // Arrange
            when(historyRepository.findById(1L)).thenReturn(Optional.of(testHistory));

            // Act
            HistoryResponseDTO result = historyService.getById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUrl()).isEqualTo("https://api.example.com/users");
            assertThat(result.getMethod()).isEqualTo(HttpMethod.GET);
            assertThat(result.getStatusCode()).isEqualTo(200);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void getById_WhenNotExists_ThrowsException() {
            // Arrange
            when(historyRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> historyService.getById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("History entry")
                    .hasMessageContaining("999");
        }

        @Test
        @DisplayName("Should return history with request and response details")
        void getById_ReturnsWithDetails() {
            // Arrange
            when(historyRepository.findById(1L)).thenReturn(Optional.of(testHistory));

            // Act
            HistoryResponseDTO result = historyService.getById(1L);

            // Assert
            assertThat(result.getRequest()).isNotNull();
            assertThat(result.getRequest().getUrl()).isEqualTo("https://api.example.com/users");
            assertThat(result.getRequest().getMethod()).isEqualTo(HttpMethod.GET);
            assertThat(result.getRequest().getBodyType()).isEqualTo("NONE");
            assertThat(result.getRequest().getAuthType()).isEqualTo("BEARER_TOKEN");

            assertThat(result.getResponse()).isNotNull();
            assertThat(result.getResponse().getStatusCode()).isEqualTo(200);
            assertThat(result.getResponse().getStatusText()).isEqualTo("OK");
            assertThat(result.getResponse().getBody()).isEqualTo("{\"users\":[]}");
            assertThat(result.getResponse().getResponseTime()).isEqualTo(150L);
            assertThat(result.getResponse().getResponseSize()).isEqualTo(100L);
        }

        @Test
        @DisplayName("Should parse headers JSON to map")
        void getById_ParsesHeadersToMap() {
            // Arrange
            when(historyRepository.findById(1L)).thenReturn(Optional.of(testHistory));

            // Act
            HistoryResponseDTO result = historyService.getById(1L);

            // Assert
            assertThat(result.getRequest().getHeaders()).isNotNull();
            assertThat(result.getRequest().getHeaders()).containsKey("Authorization");
            assertThat(result.getRequest().getHeaders().get("Authorization")).isEqualTo("Bearer token");
        }

        @Test
        @DisplayName("Should parse query params JSON to map")
        void getById_ParsesQueryParamsToMap() {
            // Arrange
            when(historyRepository.findById(1L)).thenReturn(Optional.of(testHistory));

            // Act
            HistoryResponseDTO result = historyService.getById(1L);

            // Assert
            assertThat(result.getRequest().getQueryParams()).isNotNull();
            assertThat(result.getRequest().getQueryParams()).containsKey("page");
            assertThat(result.getRequest().getQueryParams().get("page")).isEqualTo("1");
        }

        @Test
        @DisplayName("Should handle null JSON fields gracefully")
        void getById_WithNullJson_ReturnsEmptyMaps() {
            // Arrange
            testHistory.setRequestHeaders(null);
            testHistory.setQueryParams(null);
            testHistory.setResponseHeaders(null);
            testHistory.setAuthConfig(null);
            when(historyRepository.findById(1L)).thenReturn(Optional.of(testHistory));

            // Act
            HistoryResponseDTO result = historyService.getById(1L);

            // Assert
            assertThat(result.getRequest().getHeaders()).isEmpty();
            assertThat(result.getRequest().getQueryParams()).isEmpty();
            assertThat(result.getRequest().getAuthConfig()).isEmpty();
            assertThat(result.getResponse().getHeaders()).isEmpty();
        }

        @Test
        @DisplayName("Should get request name from associated ApiRequest")
        void getById_WithApiRequest_ReturnsRequestName() {
            // Arrange
            ApiCollection collection = ApiCollection.builder()
                    .id(1L)
                    .name("Test Collection")
                    .build();

            ApiRequest apiRequest = ApiRequest.builder()
                    .id(1L)
                    .name("Get Users")
                    .collection(collection)
                    .build();

            testHistory.setApiRequest(apiRequest);
            when(historyRepository.findById(1L)).thenReturn(Optional.of(testHistory));

            // Act
            HistoryResponseDTO result = historyService.getById(1L);

            // Assert
            assertThat(result.getRequestName()).isEqualTo("Get Users");
            assertThat(result.getCollectionName()).isEqualTo("Test Collection");
        }

        @Test
        @DisplayName("Should get collection name from history's direct collection")
        void getById_WithDirectCollection_ReturnsCollectionName() {
            // Arrange
            ApiCollection collection = ApiCollection.builder()
                    .id(1L)
                    .name("Direct Collection")
                    .build();
            testHistory.setCollection(collection);
            when(historyRepository.findById(1L)).thenReturn(Optional.of(testHistory));

            // Act
            HistoryResponseDTO result = historyService.getById(1L);

            // Assert
            assertThat(result.getCollectionName()).isEqualTo("Direct Collection");
        }
    }

    @Nested
    @DisplayName("deleteById() Tests")
    class DeleteByIdTests {

        @Test
        @DisplayName("Should delete history entry when found")
        void deleteById_WhenExists_DeletesEntry() {
            // Arrange
            when(historyRepository.existsById(1L)).thenReturn(true);
            doNothing().when(historyRepository).deleteById(1L);

            // Act
            historyService.deleteById(1L);

            // Assert
            verify(historyRepository).existsById(1L);
            verify(historyRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void deleteById_WhenNotExists_ThrowsException() {
            // Arrange
            when(historyRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> historyService.deleteById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("History entry")
                    .hasMessageContaining("999");

            verify(historyRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("clearAll() Tests")
    class ClearAllTests {

        @Test
        @DisplayName("Should delete all history entries")
        void clearAll_DeletesAllEntries() {
            // Arrange
            doNothing().when(historyRepository).deleteAllHistory();

            // Act
            historyService.clearAll();

            // Assert
            verify(historyRepository).deleteAllHistory();
        }
    }

    @Nested
    @DisplayName("getStats() Tests")
    class GetStatsTests {

        @Test
        @DisplayName("Should return correct statistics")
        void getStats_ReturnsCorrectStats() {
            // Arrange
            List<RequestHistory> histories = Arrays.asList(
                    createHistory(1L, HttpMethod.GET, 200, 100L),
                    createHistory(2L, HttpMethod.POST, 201, 150L),
                    createHistory(3L, HttpMethod.GET, 404, 50L),
                    createHistory(4L, HttpMethod.DELETE, 500, 200L)
            );

            when(historyRepository.findAll()).thenReturn(histories);

            // Act
            HistoryService.HistoryStats stats = historyService.getStats();

            // Assert
            assertThat(stats.totalRequests()).isEqualTo(4L);
            assertThat(stats.successCount()).isEqualTo(2L); // 200, 201
            assertThat(stats.errorCount()).isEqualTo(2L); // 404, 500
            assertThat(stats.avgResponseTime()).isEqualTo(125.0); // (100+150+50+200)/4
            assertThat(stats.methodBreakdown()).containsEntry("GET", 2L);
            assertThat(stats.methodBreakdown()).containsEntry("POST", 1L);
            assertThat(stats.methodBreakdown()).containsEntry("DELETE", 1L);
        }

        @Test
        @DisplayName("Should return empty stats when no history exists")
        void getStats_WhenEmpty_ReturnsEmptyStats() {
            // Arrange
            when(historyRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            HistoryService.HistoryStats stats = historyService.getStats();

            // Assert
            assertThat(stats.totalRequests()).isZero();
            assertThat(stats.successCount()).isZero();
            assertThat(stats.errorCount()).isZero();
            assertThat(stats.avgResponseTime()).isZero();
            assertThat(stats.methodBreakdown()).isEmpty();
        }

        @Test
        @DisplayName("Should handle null response time in average calculation")
        void getStats_WithNullResponseTime_HandlesGracefully() {
            // Arrange
            RequestHistory historyWithNullTime = createHistory(1L, HttpMethod.GET, 200, null);
            when(historyRepository.findAll()).thenReturn(List.of(historyWithNullTime));

            // Act
            HistoryService.HistoryStats stats = historyService.getStats();

            // Assert
            assertThat(stats.avgResponseTime()).isZero();
        }

        @Test
        @DisplayName("Should handle null method in breakdown")
        void getStats_WithNullMethod_HandlesGracefully() {
            // Arrange
            RequestHistory historyWithNullMethod = RequestHistory.builder()
                    .id(1L)
                    .method(null)
                    .statusCode(200)
                    .responseTime(100L)
                    .build();
            when(historyRepository.findAll()).thenReturn(List.of(historyWithNullMethod));

            // Act
            HistoryService.HistoryStats stats = historyService.getStats();

            // Assert
            assertThat(stats.methodBreakdown()).isEmpty();
        }

        @Test
        @DisplayName("Should handle null status code")
        void getStats_WithNullStatusCode_HandlesGracefully() {
            // Arrange
            RequestHistory historyWithNullStatus = RequestHistory.builder()
                    .id(1L)
                    .method(HttpMethod.GET)
                    .statusCode(null)
                    .responseTime(100L)
                    .build();
            when(historyRepository.findAll()).thenReturn(List.of(historyWithNullStatus));

            // Act
            HistoryService.HistoryStats stats = historyService.getStats();

            // Assert
            assertThat(stats.successCount()).isZero();
            assertThat(stats.errorCount()).isZero();
        }

        @Test
        @DisplayName("Should count 3xx status codes as success")
        void getStats_With3xxStatus_CountsAsSuccess() {
            // Arrange
            List<RequestHistory> histories = List.of(
                    createHistory(1L, HttpMethod.GET, 301, 100L),
                    createHistory(2L, HttpMethod.GET, 302, 100L)
            );
            when(historyRepository.findAll()).thenReturn(histories);

            // Act
            HistoryService.HistoryStats stats = historyService.getStats();

            // Assert
            assertThat(stats.successCount()).isEqualTo(2L);
            assertThat(stats.errorCount()).isZero();
        }
    }

    @Nested
    @DisplayName("deleteOlderThan() Tests")
    class DeleteOlderThanTests {

        @Test
        @DisplayName("Should delete history older than specified days")
        void deleteOlderThan_DeletesOldEntries() {
            // Arrange
            doNothing().when(historyRepository).deleteOlderThan(any(LocalDateTime.class));

            // Act
            historyService.deleteOlderThan(30);

            // Assert
            verify(historyRepository).deleteOlderThan(dateCaptor.capture());
            LocalDateTime cutoffDate = dateCaptor.getValue();
            assertThat(cutoffDate).isBefore(LocalDateTime.now().minusDays(29));
            assertThat(cutoffDate).isAfter(LocalDateTime.now().minusDays(31));
        }

        @Test
        @DisplayName("Should calculate correct cutoff date for 7 days")
        void deleteOlderThan_CalculatesCorrectDateFor7Days() {
            // Arrange
            doNothing().when(historyRepository).deleteOlderThan(any(LocalDateTime.class));

            // Act
            historyService.deleteOlderThan(7);

            // Assert
            verify(historyRepository).deleteOlderThan(dateCaptor.capture());
            LocalDateTime cutoffDate = dateCaptor.getValue();
            LocalDateTime expectedCutoff = LocalDateTime.now().minusDays(7);

            // Allow 1 minute tolerance for test execution time
            assertThat(cutoffDate).isAfterOrEqualTo(expectedCutoff.minusMinutes(1));
            assertThat(cutoffDate).isBeforeOrEqualTo(expectedCutoff.plusMinutes(1));
        }

        @Test
        @DisplayName("Should handle zero days")
        void deleteOlderThan_WithZeroDays_DeletesAllOld() {
            // Arrange
            doNothing().when(historyRepository).deleteOlderThan(any(LocalDateTime.class));

            // Act
            historyService.deleteOlderThan(0);

            // Assert
            verify(historyRepository).deleteOlderThan(dateCaptor.capture());
            LocalDateTime cutoffDate = dateCaptor.getValue();
            // Cutoff should be essentially now
            assertThat(cutoffDate).isAfter(LocalDateTime.now().minusMinutes(1));
        }
    }

    @Nested
    @DisplayName("Mapping Tests")
    class MappingTests {

        @Test
        @DisplayName("Should map to summary DTO correctly")
        void mapToSummaryDTO_MapsCorrectly() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<RequestHistory> page = new PageImpl<>(List.of(testHistory), pageable, 1);

            when(historyRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(page);

            // Act
            Page<HistoryResponseDTO> result = historyService.getHistory(
                    null, null, null, null, null, pageable
            );

            // Assert
            HistoryResponseDTO dto = result.getContent().get(0);
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getUrl()).isEqualTo("https://api.example.com/users");
            assertThat(dto.getMethod()).isEqualTo(HttpMethod.GET);
            assertThat(dto.getStatusCode()).isEqualTo(200);
            assertThat(dto.getStatusText()).isEqualTo("OK");
            assertThat(dto.getResponseTime()).isEqualTo(150L);
            assertThat(dto.getResponseSize()).isEqualTo(100L);
            assertThat(dto.getExecutedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should handle invalid JSON in headers gracefully")
        void parseJsonToMap_WithInvalidJson_ReturnsEmptyMap() {
            // Arrange
            testHistory.setRequestHeaders("invalid json {{{");
            when(historyRepository.findById(1L)).thenReturn(Optional.of(testHistory));

            // Act
            HistoryResponseDTO result = historyService.getById(1L);

            // Assert
            assertThat(result.getRequest().getHeaders()).isEmpty();
        }

        @Test
        @DisplayName("Should handle blank JSON string")
        void parseJsonToMap_WithBlankString_ReturnsEmptyMap() {
            // Arrange
            testHistory.setRequestHeaders("   ");
            when(historyRepository.findById(1L)).thenReturn(Optional.of(testHistory));

            // Act
            HistoryResponseDTO result = historyService.getById(1L);

            // Assert
            assertThat(result.getRequest().getHeaders()).isEmpty();
        }

        @Test
        @DisplayName("Should handle empty JSON string")
        void parseJsonToMap_WithEmptyString_ReturnsEmptyMap() {
            // Arrange
            testHistory.setRequestHeaders("");
            when(historyRepository.findById(1L)).thenReturn(Optional.of(testHistory));

            // Act
            HistoryResponseDTO result = historyService.getById(1L);

            // Assert
            assertThat(result.getRequest().getHeaders()).isEmpty();
        }
    }


    private RequestHistory createHistory(Long id, HttpMethod method, int statusCode, Long responseTime) {
        return RequestHistory.builder()
                .id(id)
                .url("https://api.example.com/test")
                .method(method)
                .statusCode(statusCode)
                .responseTime(responseTime)
                .executedAt(now)
                .build();
    }
}

