package com.akash.embedqa.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * Author: akash
 * Date: 17/12/25
 */
@Configuration
public class HttpClientConfig {

    // Maximum total HTTP connections across all target hosts
    private static final int MAX_TOTAL_CONNECTIONS = 100;

    // Maximum concurrent connections allowed per single host (route)
    private static final int MAX_CONNECTIONS_PER_ROUTE = 20;

    // Time to establish a TCP connection with the target server
    private static final int CONNECTION_TIMEOUT_MS = 30000; // 30 seconds

    // Time waiting for data after the connection is established
    private static final int SOCKET_TIMEOUT_MS = 60000; // 60 seconds

    // Time to wait for a free connection from the connection pool
    private static final int CONNECTION_REQUEST_TIMEOUT_MS = 30000; // 30 seconds

    /**
     * Creates a singleton Apache CloseableHttpClient bean
     * with connection pooling, timeouts, and SSL support.
     */
    @Bean
    public CloseableHttpClient httpClient()
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        // Create SSL context that trusts all certificates (for development)
        // In production, configure proper certificate validation
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(null, (chain, authType) -> true)
                .build();

        /*
         * Connection manager with pooling support
         * ---------------------------------------
         * Manages and reuses HTTP connections efficiently.
         */
        PoolingHttpClientConnectionManager connectionManager =
                PoolingHttpClientConnectionManagerBuilder.create()

                        // Configure HTTPS using the custom SSL context
                        .setSSLSocketFactory(
                                SSLConnectionSocketFactoryBuilder.create()
                                        .setSslContext(sslContext)
                                        .build()
                        )

                        // Default socket configuration (read timeout)
                        .setDefaultSocketConfig(
                                SocketConfig.custom()
                                        .setSoTimeout(Timeout.ofMilliseconds(SOCKET_TIMEOUT_MS))
                                        .build()
                        )

                        // Enforce strict connection pool limits
                        .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)

                        // Reuse the most recently used connection first (better performance)
                        .setConnPoolPolicy(PoolReusePolicy.LIFO)

                        // Default connection-level configuration
                        .setDefaultConnectionConfig(
                                ConnectionConfig.custom()
                                        // Time allowed to establish the connection
                                        .setConnectTimeout(Timeout.ofMilliseconds(CONNECTION_TIMEOUT_MS))

                                        // Time waiting for data packets
                                        .setSocketTimeout(Timeout.ofMilliseconds(SOCKET_TIMEOUT_MS))

                                        // Maximum lifetime of a connection
                                        .setTimeToLive(TimeValue.ofMinutes(10))
                                        .build()
                        )

                        // Total connections allowed across all routes
                        .setMaxConnTotal(MAX_TOTAL_CONNECTIONS)

                        // Maximum connections per route (host)
                        .setMaxConnPerRoute(MAX_CONNECTIONS_PER_ROUTE)

                        .build();

        /*
         * Default request configuration
         * -----------------------------
         * Applied to every HTTP request automatically.
         */
        RequestConfig requestConfig = RequestConfig.custom()

                // Maximum time to wait for a free connection from the pool
                .setConnectionRequestTimeout(
                        Timeout.ofMilliseconds(CONNECTION_REQUEST_TIMEOUT_MS)
                )

                // Maximum time waiting for a response
                .setResponseTimeout(Timeout.ofMilliseconds(SOCKET_TIMEOUT_MS))

                .build();

        /*
         * Build and return the HTTP client
         * -------------------------------
         */
        return HttpClients.custom()

                // Use the pooled connection manager
                .setConnectionManager(connectionManager)

                // Apply default request-level configuration
                .setDefaultRequestConfig(requestConfig)

                // Remove expired connections automatically
                .evictExpiredConnections()

                // Remove connections idle for more than 30 seconds
                .evictIdleConnections(TimeValue.of(30, TimeUnit.SECONDS))

                .build();
    }
}

