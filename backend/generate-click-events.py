#!/usr/bin/env python3
import psycopg2
import random
from datetime import datetime, timedelta

# Database connection
conn = psycopg2.connect(
    host="localhost",
    port=5432,
    database="url_shortener",
    user="admin",
    password="test@123"
)
cur = conn.cursor()

# Get all system-generated short URLs
cur.execute("SELECT short_url FROM short_urls WHERE user_id = 'system-generated'")
short_urls = [row[0] for row in cur.fetchall()]

user_agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36"

# Generate click events for each URL
for short_url in short_urls:
    click_count = random.randint(1, 10000)
    
    for _ in range(click_count):
        # Random timestamp within past 1 month
        days_ago = random.randint(0, 30)
        hours_ago = random.randint(0, 23)
        minutes_ago = random.randint(0, 59)
        seconds_ago = random.randint(0, 59)
        
        timestamp = datetime.now() - timedelta(days=days_ago, hours=hours_ago, minutes=minutes_ago, seconds=seconds_ago)
        
        cur.execute(
            "INSERT INTO click_events (short_url, timestamp, user_agent) VALUES (%s, %s, %s)",
            (short_url, timestamp, user_agent)
        )

conn.commit()
cur.close()
conn.close()

print(f"Generated click events for {len(short_urls)} URLs")