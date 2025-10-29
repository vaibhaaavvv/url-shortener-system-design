import { useState, useEffect } from 'react';
import { useUrlContext } from '../contexts/UrlContext.jsx';
import { shortenUrl } from '../services/api.js';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

const UrlShortenerForm = () => {
  const [longUrl, setLongUrl] = useState('');
  const [shortUrl, setShortUrl] = useState('');
  const [customAlias, setCustomAlias] = useState('');
  const [expirationDate, setExpirationDate] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const { triggerRefresh } = useUrlContext();

  useEffect(() => {
  }, []);

  const isValidUrl = (url) => {
    try {
      new URL(url);
      return true;
    } catch {
      return false;
    }
  };

  const handleUrlChange = (e) => {
    const url = e.target.value;
    setLongUrl(url);
    setError('');
    setShortUrl('');
    setSuccessMessage('');
  };

  const handleAliasChange = (e) => {
    setCustomAlias(e.target.value);
    setError('');
    setShortUrl('');
    setSuccessMessage('');
  };

  const handleCloseSuccess = () => {
    setLongUrl('');
    setCustomAlias('');
    setExpirationDate(null);
    setShortUrl('');
    setSuccessMessage('');
    setError('');
  };

  const handleSubmit = async (e) => {
    console.log('Is valid URL:', isValidUrl(longUrl));
    
    e.preventDefault();
    if (!isValidUrl(longUrl)) {
      return;
    }

    setIsLoading(true);
    setError('');
    setSuccessMessage('');

    const requestBody = { longUrl };
    if (customAlias.trim()) {
      requestBody.customAlias = customAlias.trim();
    }
    if (expirationDate) {
      const localDate = new Date(expirationDate.getTime() - (expirationDate.getTimezoneOffset() * 60000));
      requestBody.expirationDate = localDate.toISOString();
    }

    console.log('Making API call to:', 'http://localhost:8080/api/v1/shorten');

    try {
      const data = await shortenUrl(longUrl, customAlias.trim() || null, expirationDate ? new Date(expirationDate.getTime() - (expirationDate.getTimezoneOffset() * 60000)).toISOString() : null);
      
      if (!data.shortUrl) {
        setError(data.message || 'Failed to shorten URL');
        return;
      }
      console.log('Response data:', data);

      setShortUrl(data.shortUrl);
      setSuccessMessage(data.message || 'URL shortened successfully!');
      triggerRefresh(); // Refresh the URL list
    } catch (err) {
      console.error('API call error:', err);
      setError(`Network error: ${err.message}`);
    } finally {
      setIsLoading(false);
    }
  };

  const handleRedirect = (shortUrlCode) => {
    // Simply open the redirect URL in a new tab
    // The backend will handle the redirect automatically
    window.open(`http://localhost:8080/api/v1/${shortUrlCode}`, '_blank');
  };

  const getInputClass = () => {
    if (!longUrl) return 'input';
    return isValidUrl(longUrl) ? 'input success' : 'input error';
  };

  return (
    <div>
      <h1>URL Shortener</h1>
      <p>Enter a long URL to get a shortened version</p>

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <input
            type="text"
            className={getInputClass()}
            placeholder="Enter your long URL here..."
            value={longUrl}
            onChange={handleUrlChange}
          />
          {longUrl && !isValidUrl(longUrl) && (
            <div className="error-text">Please enter a valid URL</div>
          )}
        </div>

        <div className="form-group">
          <input
            type="text"
            className="input"
            placeholder="Custom alias (optional)"
            value={customAlias}
            onChange={handleAliasChange}
          />
        </div>

        <div className="form-group">
          <DatePicker
            selected={expirationDate}
            onChange={setExpirationDate}
            showTimeSelect
            timeFormat="HH:mm"
            timeIntervals={15}
            dateFormat="MMMM d, yyyy h:mm aa"
            placeholderText="Expiry date (optional)"
            minDate={new Date()}
            className="input"
            isClearable
            style={{ width: '100%', minWidth: '300px' }}
          />
        </div>

        <button
          type="submit"
          className="button button-primary"
          disabled={!isValidUrl(longUrl) || isLoading}
          onClick={() => console.log('Button clicked!')}
        >
          {isLoading ? 'Generating...' : 'Generate Short URL'}
        </button>
      </form>

      {error && (
        <div className="error-text" style={{ marginTop: '1rem' }}>
          {error}
        </div>
      )}

      {successMessage && (
        <div style={{ 
          marginTop: '1rem', 
          padding: '0.75rem', 
          backgroundColor: '#f0fdf4', 
          color: '#166534', 
          border: '1px solid #bbf7d0', 
          borderRadius: '0.5rem'
        }}>
          {successMessage}
        </div>
      )}

      {shortUrl && (
        <div className="success-box" style={{ position: 'relative' }}>
          <button
            onClick={handleCloseSuccess}
            style={{
              position: 'absolute',
              top: '0.5rem',
              right: '0.5rem',
              background: 'none',
              border: 'none',
              fontSize: '1.2rem',
              cursor: 'pointer',
              color: 'var(--text-secondary)'
            }}
          >
            ×
          </button>
          <h3>Your shortened URL:</h3>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <div className="result-url" style={{ flex: 1 }}>
              https://miniurl.com/{shortUrl}
            </div>
            <button
              className="button button-primary"
              style={{ padding: '0.5rem 1rem', fontSize: '0.875rem' }}
              onClick={() => handleRedirect(shortUrl)}
            >
              Visit
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default UrlShortenerForm;