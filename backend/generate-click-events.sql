-- Generate click events for system-generated URLs
DO $$
DECLARE
    url_record RECORD;
    click_count INTEGER;
    i INTEGER;
    random_timestamp TIMESTAMP;
BEGIN
    FOR url_record IN SELECT short_url FROM short_urls WHERE user_id = 'system-generated' LOOP
        click_count := floor(random() * 500 + 1)::INTEGER;
        
        FOR i IN 1..click_count LOOP
            random_timestamp := NOW() - INTERVAL '1 day' * (random() * 30) - INTERVAL '1 hour' * (random() * 24) - INTERVAL '1 minute' * (random() * 60);
            
            INSERT INTO click_events (short_url, timestamp, user_agent) 
            VALUES (
                url_record.short_url, 
                random_timestamp, 
                'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36'
            );
        END LOOP;
    END LOOP;
END $$;