#!/bin/bash

# Kill existing processes on ports
echo "Stopping existing services..."
lsof -ti:8080 | xargs kill -9 2>/dev/null || true
lsof -ti:8081 | xargs kill -9 2>/dev/null || true
lsof -ti:8082 | xargs kill -9 2>/dev/null || true

# Build all services
echo "Building services..."
mvn clean package -DskipTests

# Start all services in background
echo "Starting services..."
if [ "$1" = "debug" ]; then
    echo "Debug mode enabled - Debug ports: Gateway=5005, Write=5006, Read=5007"
    java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar &
    java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5006 -jar shortening-service/target/shortening-service-0.0.1-SNAPSHOT.jar &
    java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5007 -jar redirect-analytics-service/target/redirect-analytics-service-0.0.1-SNAPSHOT.jar &
else
    java -jar api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar &
    java -jar shortening-service/target/shortening-service-0.0.1-SNAPSHOT.jar &
    java -jar redirect-analytics-service/target/redirect-analytics-service-0.0.1-SNAPSHOT.jar &
fi

echo "All services started!"
echo "API Gateway: http://localhost:8080"
echo "Shortening Service: http://localhost:8081"
echo "Redirect Analytics Service: http://localhost:8082"
echo "Press Ctrl+C to stop all services"

# Trap SIGINT and SIGTERM to gracefully shutdown
trap 'echo "Shutting down services..."; kill $(jobs -p); wait; echo "All services stopped."; exit 0' SIGINT SIGTERM

wait