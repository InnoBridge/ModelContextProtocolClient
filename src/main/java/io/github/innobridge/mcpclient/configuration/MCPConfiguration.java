package io.github.innobridge.mcpclient.configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.ClientMcpTransport;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration for Model Context Protocol Client.
 */
@Configuration
@Slf4j
public class MCPConfiguration {

    @Value("${BRAVE_API_KEY}")
    private String braveApiKey;

    /**
     * Creates a McpSyncClient bean.
     *
     * @return The McpSyncClient
     */
    @Bean(name = "braveSearch", destroyMethod = "close")
    public McpSyncClient mcpSyncClient() {
        log.info("Initializing McpSyncClient for Brave Search");
        
        // Create transport using StdioClientTransport
        ClientMcpTransport transport = createTransport();
        
        // Create a sync client with custom configuration
        McpSyncClient client = McpClient.sync(transport)
            .requestTimeout(Duration.ofSeconds(10))
            .capabilities(io.modelcontextprotocol.spec.McpSchema.ClientCapabilities.builder()
                .roots(true)      // Enable roots capability
                .sampling()       // Enable sampling capability
                .build())
            .build();

        // Initialize connection
        client.initialize();
        
        return client;
    }
    
    private ClientMcpTransport createTransport() {
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
