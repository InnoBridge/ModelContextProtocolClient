package io.github.innobridge.mcpclient.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for Model Context Protocol operations.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Model Context Protocol", description = "API endpoints for Model Context Protocol operations")
public class MCPController {

    @Autowired
    @Qualifier("braveSearch")
    private McpSyncClient braveSearchClient;

    /**
     * Hello World endpoint.
     *
     * @return A simple hello world message
     */
    @GetMapping("/hello")
    @Operation(summary = "Hello World endpoint", description = "Returns a simple hello world message")
    public ResponseEntity<String> helloWorld() {
        log.info("Hello World requested");
        return ResponseEntity.ok("Hello World from MCP Client!");
    }

    @GetMapping("/tools")
    @Operation(summary = "Tools endpoint", description = "Returns a list of tools")
    public ResponseEntity<ListToolsResult> tools() {
        ListToolsResult result = braveSearchClient.listTools();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/bravesearch")
    @Operation(summary = "Brave Search endpoint", description = "Performs a search using Brave Search")
    public ResponseEntity<CallToolResult> bravesearch(@RequestParam String query) {
        log.info("Brave Search requested with query: {}", query);
        
        // Create a CallToolRequest with the "brave_web_search" tool and the query parameter
        CallToolRequest toolRequest = new CallToolRequest(
            "brave_web_search",  // Tool name
            Map.of("query", query)  // Tool parameters
        );
        
        // Call the tool and get the result
        CallToolResult result = braveSearchClient.callTool(toolRequest);
        
        return ResponseEntity.ok(result);
    }
}
