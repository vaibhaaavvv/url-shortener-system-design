import { useState, useEffect } from 'react';
import { useUrlContext } from '../contexts/UrlContext.jsx';
import { getUserUrls, getClickCounts } from '../services/api.js';
import Analytics from './Analytics.jsx';

const UrlList = () => {
  const [urls, setUrls] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [page, setPage] = useState(0);
  const [error, setError] = useState('');
  const [isInitialLoad, setIsInitialLoad] = useState(true);
  const [selectedUrl, setSelectedUrl] = useState(null);
  const { refreshTrigger } = useUrlContext();

  const updateClickCountsWithData = async (urlData) => {
    if (!urlData || urlData.length === 0) return;


    try {
      // Extract short codes from URLs, removing miniurl.com prefix if present
      const shortCodes = urlData.map(url => {
        const shortUrl = url.shortenedUrl || '';
        const code = shortUrl.includes('miniurl.com/') 
          ? shortUrl.split('miniurl.com/')[1] 
          : shortUrl;
        return code;
      }).filter(code => code); // Remove empty codes


      const clickCounts = await getClickCounts(shortCodes);
      console.log('Received click counts:', clickCounts);
      
      // Update URLs with new click counts
      setUrls(prev => prev.map(url => {
        const shortUrl = url.shortUrl || url.shortenedUrl || '';
        const code = shortUrl.includes('miniurl.com/') 
          ? shortUrl.split('miniurl.com/')[1] 
          : shortUrl;
        return {
          ...url,
          clickCounts: clickCounts.hasOwnProperty(code) ? clickCounts[code] : url.clickCounts
        };
      }));
    } catch (err) {
      console.error('Error updating click counts:', err);
    }
  };

  const fetchUrls = async (pageNumber = 0, append = false) => {
    setIsLoading(true);
    setError('');

    try {
      const data = await getUserUrls(pageNumber, 10);
      
      if (append) {
        setUrls(prev => [...prev, ...data.urls]);
      } else {
        setUrls(data.urls);
      }
      
      setHasMore(data.hasMore);
      setPage(pageNumber);
    } catch (err) {
      console.error('Error fetching URLs:', err);
      setError('Failed to load URLs');
    } finally {
      setIsLoading(false);
    }
  };

  const updateClickCounts = async () => {
    if (urls.length === 0) return;
    await updateClickCountsWithData(urls);
  };

  const loadMore = () => {
    if (!isLoading && hasMore) {
      fetchUrls(page + 1, true);
    }
  };

  const handleRedirect = (shortUrlCode) => {
    // Extract just the code if it's a full URL
    const code = shortUrlCode.includes('miniurl.com/') 
      ? shortUrlCode.split('miniurl.com/')[1] 
      : shortUrlCode;
    
    console.log('Redirecting to:', `http://localhost:8080/api/v1/${code}`);
    window.open(`http://localhost:8080/api/v1/${code}`, '_blank');
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInMinutes = Math.floor((now - date) / (1000 * 60));
    
    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes} min ago`;
    if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)} hr ago`;
    return `${Math.floor(diffInMinutes / 1440)} days ago`;
  };

  useEffect(() => {
    fetchUrls();
    setIsInitialLoad(false);
  }, []);

  useEffect(() => {
    if (refreshTrigger > 0 && !isInitialLoad) {
      fetchUrls(); // Refresh list when new URL is created
    }
  }, [refreshTrigger, isInitialLoad]);

  // Call analytics API immediately after URLs are loaded
  useEffect(() => {
    if (urls.length > 0) {
      console.log('URLs loaded, calling analytics API immediately');
      updateClickCountsWithData(urls);
    }
  }, [urls.length > 0 ? JSON.stringify(urls.map(u => u.shortUrl)) : '']);

  // Periodic click count updates
  useEffect(() => {
    if (urls.length === 0) return;

    console.log('Setting up click count updates for', urls.length, 'URLs');
    const interval = setInterval(() => {
      console.log('Updating click counts...');
      updateClickCounts();
    }, 10000); // Update every 10 seconds
    
    return () => {
      clearInterval(interval);
    };
  }, [urls]);

  if (urls.length === 0 && !isLoading && !error) {
    return (
      <div className="container" style={{ marginTop: '2rem' }}>
        <h3>Your Shortened URLs</h3>
        <p style={{ color: 'var(--text-secondary)' }}>No URLs created yet. Create your first shortened URL above!</p>
      </div>
    );
  }

  return (
    <div>
      <h3>Your Shortened URLs</h3>
      
      {error && (
        <div className="error-text" style={{ marginBottom: '1rem' }}>
          {error}
        </div>
      )}

      <div style={{ height: 'calc(100vh - 200px)', overflowY: 'auto', border: '1px solid var(--border-color)', borderRadius: '0.5rem' }}>
        {urls.map((url, index) => (
          <div 
            key={`${url.shortUrl}-${index}`}
            style={{
              padding: '1rem',
              borderBottom: index < urls.length - 1 ? '1px solid var(--border-color)' : 'none',
              backgroundColor: 'var(--bg-primary)'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
              <div style={{ flex: 1, fontFamily: 'monospace', fontSize: '0.9rem', color: 'var(--primary-color)' }}>
                {url.shortenedUrl}
              </div>
              <button
                className="button button-primary"
                style={{ padding: '0.5rem 1rem' }}
                onClick={() => setSelectedUrl(url.shortenedUrl)}
              >
                Analytics
              </button>
              <button
                className="button button-primary"
                style={{ padding: '0.5rem 1rem' }}
                onClick={() => handleRedirect(url.shortenedUrl)}
              >
                Visit
              </button>
            </div>
            
            <div style={{ color: 'var(--text-primary)', marginBottom: '0.25rem' }}>
              <strong>Original:</strong> {url.longUrl.length > 60 ? `${url.longUrl.substring(0, 60)}...` : url.longUrl}
            </div>
            
            <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--text-secondary)' }}>
              <span>Created {formatDate(url.createdAt)}</span>
              <span>{url.clickCounts} clicks</span>
            </div>
          </div>
        ))}

        {hasMore && (
          <div style={{ padding: '1rem', textAlign: 'center', backgroundColor: 'var(--bg-secondary)' }}>
            <button
              className="button button-primary"
              onClick={loadMore}
              disabled={isLoading}
              style={{}}
            >
              {isLoading ? 'Loading...' : 'Load More'}
            </button>
          </div>
        )}
      </div>
      
      {selectedUrl && (
        <Analytics 
          shortUrl={selectedUrl} 
          onClose={() => setSelectedUrl(null)} 
        />
      )}
    </div>
  );
};

export default UrlList;