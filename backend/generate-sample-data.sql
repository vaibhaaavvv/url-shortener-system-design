-- Sample data generation script for short_urls table
-- Uses the same Base62 encoding logic as ShortnerService.java

INSERT INTO short_urls (short_url, long_url, user_id, created_at) VALUES
('aB3xY9', 'https://www.google.com', 'system-generated', '2025-10-25 14:23:15'),
('kL8mN2', 'https://www.github.com', 'system-generated', '2025-10-24 09:45:32'),
('pQ5rS7', 'https://www.stackoverflow.com', 'system-generated', '2025-10-23 16:12:08'),
('tU1vW4', 'https://www.wikipedia.org', 'system-generated', '2025-10-22 11:38:47'),
('xY6zA3', 'https://www.reddit.com', 'system-generated', '2025-10-21 20:55:19'),
('bC9dE2', 'https://www.youtube.com', 'system-generated', '2025-10-20 13:27:41'),
('fG4hI8', 'https://www.amazon.com', 'system-generated', '2025-10-19 08:14:56'),
('jK7lM1', 'https://www.facebook.com', 'system-generated', '2025-10-18 17:42:33'),
('nO0pQ5', 'https://www.twitter.com', 'system-generated', '2025-10-17 12:09:28'),
('rS3tU6', 'https://www.linkedin.com', 'system-generated', '2025-10-16 15:31:12'),
('vW9xY2', 'https://www.netflix.com', 'system-generated', '2025-10-15 10:18:45'),
('zA4bC7', 'https://www.spotify.com', 'system-generated', '2025-10-14 19:56:03'),
('dE1fG8', 'https://www.apple.com', 'system-generated', '2025-10-13 07:43:29'),
('hI5jK0', 'https://www.microsoft.com', 'system-generated', '2025-10-12 22:15:51'),
('lM2nO6', 'https://www.adobe.com', 'system-generated', '2025-10-11 14:37:18'),
('pQ8rS3', 'https://www.dropbox.com', 'system-generated', '2025-10-10 06:24:42'),
('tU4vW9', 'https://www.slack.com', 'system-generated', '2025-10-10 18:51:07'),
('xY7zA1', 'https://www.zoom.us', 'system-generated', '2025-10-10 11:28:34'),
('bC0dE5', 'https://www.twitch.tv', 'system-generated', '2025-10-10 21:06:59'),
('fG6hI2', 'https://www.medium.com', 'system-generated', '2025-10-10 09:33:16');