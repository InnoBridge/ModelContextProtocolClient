# ModelContextProtocolClient

A Spring Boot client application that demonstrates different transport methods (WebFlux SSE, Stdio) for communicating with the MCP server.

## Setup

1. Create a `.env` file in the root directory with your API keys:
```bash
BRAVE_SEARCH_API_KEY=your_brave_search_api_key_here
WEATHER_API_KEY=your_weather_api_key_here
```

2. Clone https://github.com/InnoBridge/ModelContextProtocolServer

3. Docker Setup
   1. Update volume mappings in docker-compose.yml:
   ```yaml
    modelcontextprotocolclient_application
    ...
   volumes:
     - .:/app
     - /var/run/docker.sock:/var/run/docker.sock
     - ./local/root:/root
     - ../{path to ModelContextProtocolServer repo}:/ModelContextProtocolServer
   ```

   2. Start the Docker containers:
   ```bash
   sudo docker compose up
   ```

   3. Build and run the mcp server:
   ```bash
   # In a new terminal
   sudo docker exec -it modelcontextprotocolserver-application sh
   cd /app
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

   4. Build and run the client:
   ```bash
   # In another new terminal
   docker exec -it modelcontextprotocolclient-application sh
   ./mvnw spring-boot:run
   ```

## Available Endpoints

### WebFlux Transport

- `GET /webflux/tools` - List all available tools
- `POST /webflux/calculate` - Calculate using the calculator tool
  ```bash
  curl -X POST "http://localhost:8080/webflux/calculate?operation=add&a=5&b=3"
  ```
- `POST /webflux/weather` - Get weather information
  ```bash
  curl -X POST "http://localhost:8080/webflux/weather?location=San%20Francisco&format=celsius"
  ```

### Stdio Transport

- `GET /stdio/tools` - List all available tools
- `POST /stdio/calculate` - Calculate using the calculator tool
  ```bash
  curl -X POST "http://localhost:8080/stdio/calculate?operation=add&a=5&b=3"
  ```
- `POST /stdio/weather` - Get weather information
  ```bash
  curl -X POST "http://localhost:8080/stdio/weather?location=San%20Francisco&format=celsius"
  ```

### BraveSearch Transport

- `GET /tools/bravesearch` - List available tools
- `POST /bravesearch` - Perform a web search
  ```bash
  curl -X POST "http://localhost:8080/bravesearch?query=spring%20boot"
  ```

## Configuration

The application supports multiple transport configurations:

1. WebFlux SSE Transport (default)
   - Communicates with the server using Server-Sent Events
   - Server must be running in WebFlux mode

2. Stdio Transport
   - Communicates with the server using standard input/output
   - Automatically launches the server jar in stdio mode

3. BraveSearch Transport
   - Communicates with the BraveSearch API
   - Requires `BRAVE_SEARCH_API_KEY` in `.env` file
   - Get your API key from [Brave Search API](https://brave.com/search/api/)

4. Weather API
   - Used by both WebFlux and Stdio transports for weather information
   - Requires `WEATHER_API_KEY` in `.env` file
   - Get your API key from [WeatherAPI](https://www.weatherapi.com/)
