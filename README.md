# URL Shortener - Microservices Architecture

A scalable URL shortening service built with Spring Boot microservices, featuring real-time analytics and Redis caching.

## Architecture

### Services
- **API Gateway** (Port 8080) -Entry point with rate limiting and routing
- **Shortening Service** (Port 8081) -URL creation and user management  
- **Redirect Analytics Service** (Port 8082) -URL redirection and click analytics

### Technology Stack
- **Backend**: Spring Boot 3.3.5, Java 17
- **Frontend**: React with Vite
- **Database**: PostgreSQL
- **Cache**: Redis (URL caching, rate limiting, click streams)
- **Gateway**: Spring Cloud Gateway
- **Build**: Maven project

### Key Features
- **SHortening**: System generated short URLs
- **Custom Aliases**: User defined short URLs
- **Expiration Dates**: Time based URL expiration
- **Rate Limiting**: 10 req/sec with 20 burst capacity
- **Real-time Analytics**: Click tracking with Redis Streams
- **Caching**: Redis-based URL lookup optimization
- **Background Cleanup**: Hourly expired URL removal

## Prerequisites

- **Docker** (v20.10+) and **Docker Compose** (v2.0+)
- **Ports**: 3000, 8080, 8081, 8082, 5432, 6379 must be available

## Setup

### Quick Start
```bash
git clone https://github.com/vaibhaaavvv/url-shortener-system-design.git
cd url-shortener-system-design
docker-compose up --build
```

*Note: Initial build may take 2-3 minutes*

### Seed Data Setup
Data scripts run automatically in containers. If scripts fail, manually load sample data:

```bash
# Check if data exists
docker exec -it url-shortener-db psql -U admin -d url_shortener -c "SELECT COUNT(*) FROM short_urls;"

# If count is 0, load sample URLs
docker exec -i url-shortener-db psql -U admin -d url_shortener < backend/generate-sample-data.sql

# Check click events
docker exec -it url-shortener-db psql -U admin -d url_shortener -c "SELECT COUNT(*) FROM click_events;"

# If count is 0, load sample events
docker exec -i url-shortener-db psql -U admin -d url_shortener < backend/generate-click-events.sql
```

### Access
- **Application**: http://localhost:3000
- **API Gateway**: http://localhost:8080


## API Endpoints
### URL Shortening
```bash
POST /api/v1/user/init          # Initialize user session
POST /api/v1/shorten            # Create short URL
```

### Analytics & Redirection  
```bash
GET /api/v1/{shortUrl}          # Redirect to original URL
GET /api/v1/urls                # List user URLs (paginated)
GET /api/v1/analytics/click-counts        # lifetime click count
GET /api/v1/analytics/daily-clicks       # Daily click analytics
```

## Database Schema

### short_urls
- `short_url` (PK) - 6-character Base62 identifier
- `long_url` - Original URL (max 2048 chars)
- `user_id` - UUID-based user identifier
- `created_at` - Creation timestamp
- `expiration_date` - Optional expiration

### click_events  
- `id` (PK) - Auto-generated
- `short_url` - Reference to shortened URL
- `timestamp` - Click time
- `user_agent` - Browser information

## Assumptions

- **Single Database**: All services share PostgreSQL instance
- **Redis Availability**: Required for caching and rate limiting
- **IP-based Rate Limiting**: Users identified by IP address
- **Cookie-based Sessions**: 1-year user identification
- **Base62 Encoding**: 6-character URL generation from UUID
- **Hourly Cleanup**: Expired URLs removed every hour

## Future Improvements

- [ ] User authentication and accounts (Login/Signup System)
- [ ] Bulk URL operations
- [ ] QR code generation
- [ ] URL analytics dashboard
- [ ] Health checks and monitoring
- [ ] Metrics collection with Grafana logs
- [ ] URL validation against malicious sites
- [ ] API key authentication
- [ ] Database sharding for horizontal scaling

## Architecture Decisions

### Why Java Spring Boot?
- **High Concurrency**: JVM threading model handles thousands of simultaneous URL requests efficiently.
- **Enterprise Microservices**: Built-in service discovery, API gateway.
- **Ecosystem Maturity**: Production friendly Redis integration,scheduled tasks,and validation frameworks
- **Trade-off**: Slightly higher memory usage and startup time vs Node.js, but gains in reliability, maintainability, and enterprise features outweigh the costs for a production URL shortener service.

### Why PostgreSQL over SQLite?
- **Concurrent Writes**: Multiple services can write simultaneously without file locking issues
- **ACID Transactions**: Ensures data consistency across microservices operations
- **Scalability**: Handles high-volume URL shortening and analytics queries efficiently
- **Production Grade**: Built for multi-user, high-availability environments

### Why React?
- **Component Reusability**: Modular UI components for URL forms, lists, and analytics
- **State Management**: Efficient handling of user sessions and URL data
- **Fast Development**: Vite provides instant hot reload and optimized builds
- **Ecosystem**: Rich library support for HTTP clients, routing, and UI components

### Why Redis?
- **High-Speed Caching**: In-memory storage for fast URL lookups and reduced database load
- **Rate Limiting**: Built-in support for token bucket algorithm with automatic expiration
- **Real-time Streams**: Redis Streams for efficient click event processing and analytics
- **Scalability**: Handles thousands of concurrent requests with minimal latency

### Design Patterns
- **Microservices**: Separation of concerns (write vs read operations)
- **Event-driven Analytics**: Redis Streams for real-time click processing
- **Stateless Design**: No user registration required
- **Cache-first Strategy**: Redis before database for URL lookups
- **UUID-based Generation**: Ensures uniqueness without coordination

## Contributors

- **Vaibhav Sharma**