package io.github.innobridge.mcpclient.configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Qualifier;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.spec.ClientMcpTransport;
import io.modelcontextprotocol.spec.McpSchema.ClientCapabilities;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration for Model Context Protocol Client.
 */
@Configuration
@Slf4j
public class McpConfiguration {

    @Value("${BRAVE_API_KEY}")
    private String braveApiKey;

    /**
     * Creates a McpSyncClient bean for Brave Search.
     *
     * @return The McpSyncClient
     */
    @Bean(name = "braveSearch", destroyMethod = "close")
    public McpSyncClient braveSearchMcpSyncClient(
            @Qualifier("braveSearchTransport") ClientMcpTransport transport) {
        log.info("Initializing McpSyncClient for Brave Search");
        
        // Create a sync client with custom configuration
        McpSyncClient client = McpClient.sync(transport)
            .requestTimeout(Duration.ofSeconds(10))
            .capabilities(ClientCapabilities.builder()
                .roots(true)      // Enable roots capability
                .sampling()       // Enable sampling capability
                .build())
            .build();

        // Initialize connection
        client.initialize();
        
        return client;
    }

    /**
     * Creates a McpSyncClient bean for WebFlux MCP Server.
     *
     * @return The McpSyncClient
     */
    @Bean(name = "webFlux", destroyMethod = "close")
    public McpSyncClient webFluxMcpSyncClient(
            @Qualifier("webFluxTransport") ClientMcpTransport transport) {
        log.info("Initializing McpSyncClient for WebFlux MCP Server");
        
        // Create a sync client with custom configuration
        McpSyncClient client = McpClient.sync(transport)
            .requestTimeout(Duration.ofSeconds(10))
            .capabilities(ClientCapabilities.builder()
                .roots(true)      // Enable roots capability
                .sampling()       // Enable sampling capability
                .build())
            .build();

        // Initialize connection
        client.initialize();
        
        return client;
    }
    
    /**
     * Creates a McpSyncClient bean for Stdio MCP Server.
     *
     * @return The McpSyncClient
     */
    @Bean(name = "stdio", destroyMethod = "close")
    public McpSyncClient stdioMcpSyncClient(
            @Qualifier("stdioTransport") ClientMcpTransport transport) {
        log.info("Initializing McpSyncClient for Stdio MCP Server");
        
        // Create a sync client with custom configuration
        McpSyncClient client = McpClient.sync(transport)
            .requestTimeout(Duration.ofSeconds(30))
            .capabilities(ClientCapabilities.builder()
                .roots(true)      // Enable roots capability
                .sampling()       // Enable sampling capability
                .build())
            .build();

        // Initialize connection
        client.initialize();
        
        return client;
    }

    /**
     * Creates a ClientMcpTransport bean for WebFlux MCP Server.
     *
     * @return The ClientMcpTransport for WebFlux MCP Server
     */
    @Bean(name = "webFluxTransport")
    public ClientMcpTransport createWebFluxTransport() {
        return new WebFluxSseClientTransport(
            WebClient.builder()
            .baseUrl("http://localhost:8081"));
    }

    /**
     * Creates a ClientMcpTransport bean for Stdio MCP Server.
     *
     * @return The ClientMcpTransport for Stdio MCP Server
     */
    @Bean(name = "stdioTransport")
    public ClientMcpTransport createStdioTransport() {
        log.info("Creating StdioClientTransport");
        
        // Using StdioClientTransport with local MCP server jar
        ServerParameters params = ServerParameters.builder("java")
            .args(
                "-Dtransport.mode=stdio",
                "-Dspring.main.web-application-type=none",  // Disable web server
                "-Dspring.main.banner-mode=off",
                "-Dlogging.pattern.console=",  // Disable console logging pattern
                "-Dlogging.file.name=../logs/mcpserver.log",  // Redirect logs to file
                "-jar",
                "../ModelContextProtocolServer/target/mcpserver-0.0.1-SNAPSHOT.jar"
            )
            .build();
        
        log.info("Starting server process with jar: {}", "../ModelContextProtocolServer/target/mcpserver-0.0.1-SNAPSHOT.jar");
        return new StdioClientTransport(params);
    }

    /**
     * Creates a ClientMcpTransport bean for Brave Search.
     *
     * @return The ClientMcpTransport for Brave Search
     */
    @Bean(name = "braveSearchTransport")
    public ClientMcpTransport createBraveSearchTransport() {
        // Set up environment variables for Brave Search
        Map<String, String> env = new HashMap<>();
        env.put("BRAVE_API_KEY", braveApiKey);

        // Using StdioClientTransport with Brave Search server
        ServerParameters params = ServerParameters.builder("npx")
            .args("-y", "@modelcontextprotocol/server-brave-search")
            .env(env)
            .build();
        return new StdioClientTransport(params);
    }
}
