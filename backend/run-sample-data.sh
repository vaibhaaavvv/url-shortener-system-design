#!/bin/bash

# Script to insert sample data into PostgreSQL database
echo "Inserting sample data into short_urls table..."

docker exec -i $(docker ps -q --filter "ancestor=postgres") psql -U admin -d url_shortener < generate-sample-data.sql

echo "Sample data insertion completed!"