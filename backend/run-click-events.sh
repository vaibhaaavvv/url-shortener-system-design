#!/bin/bash

echo "Generating click events for system URLs..."

docker exec -i $(docker ps -q --filter "ancestor=postgres") psql -U admin -d url_shortener < generate-click-events.sql

echo "Click events generation completed!"