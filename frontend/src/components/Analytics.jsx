import { useState, useEffect } from 'react';
import { getClickCounts, getDailyClicks } from '../services/api.js';

const Analytics = ({ shortUrl, onClose }) => {
  const [clickCount, setClickCount] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [dailyClicks, setDailyClicks] = useState({});
  const [chartLoading, setChartLoading] = useState(true);
  const [showTable, setShowTable] = useState(false);
  const [dateRange, setDateRange] = useState(7);

  const getDateRange = () => {
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(endDate.getDate() - (dateRange - 1));
    return {
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0]
    };
  };

  const fetchClickCount = async () => {
    try {
      const code = shortUrl.includes('miniurl.com/') 
        ? shortUrl.split('miniurl.com/')[1] 
        : shortUrl;
      
      const clickCounts = await getClickCounts([code]);
      setClickCount(clickCounts[code] || 0);
    } catch (err) {
      console.error('Error fetching click count:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const fetchDailyClicks = async () => {
    try {
      const code = shortUrl.includes('miniurl.com/') 
        ? shortUrl.split('miniurl.com/')[1] 
        : shortUrl;
      const { startDate, endDate } = getDateRange();
      
      const data = await getDailyClicks(code, startDate, endDate);
      setDailyClicks(data);
    } catch (err) {
      console.error('Error fetching daily clicks:', err);
    } finally {
      setChartLoading(false);
    }
  };

  useEffect(() => {
    fetchClickCount();
    fetchDailyClicks();
    const interval = setInterval(() => {
      fetchClickCount();
      fetchDailyClicks();
    }, 10000);
    return () => clearInterval(interval);
  }, [shortUrl, dateRange]);

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1000,
      animation: 'fadeIn 0.2s ease-out'
    }}>
      <div style={{
        backgroundColor: 'var(--bg-primary)',
        padding: '2rem',
        borderRadius: '0.5rem',
        maxWidth: '1000px',
        width: '98%',
        maxHeight: '95vh',
        border: '1px solid var(--border-color)',
        animation: 'slideIn 0.3s ease-out',
        overflowY: 'auto'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
          <h2>URL Analytics</h2>
          <button
            onClick={onClose}
            style={{
              background: 'none',
              border: 'none',
              fontSize: '1.5rem',
              cursor: 'pointer',
              color: 'var(--text-secondary)'
            }}
          >
            ×
          </button>
        </div>
        
        <div style={{ marginBottom: '1rem' }}>
          <strong>URL:</strong>
          <div style={{
            fontFamily: 'monospace',
            backgroundColor: 'var(--bg-secondary)',
            padding: '0.5rem',
            borderRadius: '0.25rem',
            marginTop: '0.25rem',
            wordBreak: 'break-all'
          }}>
            https://miniurl.com/{shortUrl.includes('miniurl.com/') ? shortUrl.split('miniurl.com/')[1] : shortUrl}
          </div>
        </div>

        <div style={{ marginBottom: '2rem' }}>
          <strong>Total Clicks:</strong>
          <div style={{ 
            fontSize: '2rem', 
            color: 'var(--primary-color)', 
            marginTop: '0.5rem',
            transition: 'all 0.3s ease-in-out'
          }}>
            {isLoading ? (
              <div style={{
                width: '30px',
                height: '30px',
                border: '3px solid var(--border-color)',
                borderTop: '3px solid var(--primary-color)',
                borderRadius: '50%',
                animation: 'spin 1s linear infinite'
              }} />
            ) : clickCount}
          </div>
        </div>

        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem', flexWrap: 'wrap', gap: '1rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              <strong>Daily Clicks:</strong>
              <select
                value={dateRange}
                onChange={(e) => setDateRange(Number(e.target.value))}
                style={{
                  padding: '0.25rem 0.5rem',
                  border: '1px solid var(--border-color)',
                  borderRadius: '0.25rem',
                  fontSize: '0.875rem',
                  cursor: 'pointer'
                }}
              >
                <option value={7}>Last 7 Days</option>
                <option value={30}>Last 30 Days</option>
              </select>
            </div>
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.875rem' }}>
              <input
                type="checkbox"
                checked={showTable}
                onChange={(e) => setShowTable(e.target.checked)}
                style={{ cursor: 'pointer' }}
              />
              Table View
            </label>
          </div>
          {chartLoading ? (
            <div style={{ textAlign: 'center', padding: '2rem' }}>
              <div style={{
                width: '40px',
                height: '40px',
                border: '4px solid var(--border-color)',
                borderTop: '4px solid var(--primary-color)',
                borderRadius: '50%',
                animation: 'spin 1s linear infinite',
                margin: '0 auto'
              }} />
              <div style={{ marginTop: '1rem', color: 'var(--text-secondary)' }}>Loading chart...</div>
            </div>
          ) : (
            <div>
              {Object.keys(dailyClicks).length === 0 ? (
                <div style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-secondary)' }}>
                  No data available
                </div>
              ) : showTable ? (
                <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '1rem' }}>
                  <thead>
                    <tr style={{ backgroundColor: 'var(--bg-secondary)' }}>
                      <th style={{ padding: '0.75rem', textAlign: 'left', borderBottom: '1px solid var(--border-color)' }}>Date</th>
                      <th style={{ padding: '0.75rem', textAlign: 'right', borderBottom: '1px solid var(--border-color)' }}>Clicks</th>
                    </tr>
                  </thead>
                  <tbody>
                    {Object.entries(dailyClicks)
                      .sort(([a], [b]) => new Date(a) - new Date(b))
                      .map(([date, clicks]) => (
                        <tr key={date}>
                          <td style={{ padding: '0.75rem', borderBottom: '1px solid var(--border-color)' }}>
                            {new Date(date).toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' })}
                          </td>
                          <td style={{ padding: '0.75rem', textAlign: 'right', borderBottom: '1px solid var(--border-color)' }}>
                            {clicks}
                          </td>
                        </tr>
                      ))
                    }
                  </tbody>
                </table>
              ) : (
                <div style={{ display: 'flex', alignItems: 'end', gap: '1rem', height: '250px', padding: '3rem 1rem 1rem' }}>
                  {Object.entries(dailyClicks)
                    .sort(([a], [b]) => new Date(a) - new Date(b))
                    .map(([date, clicks]) => {
                      const maxClicks = Math.max(...Object.values(dailyClicks), 1);
                      const barHeight = clicks === 0 ? 0 : Math.max((clicks / maxClicks) * 150, 10);
                      return (
                        <div key={date} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', minWidth: '60px' }}>
                          <div style={{ fontSize: '0.875rem', marginBottom: '0.5rem', minHeight: '20px', fontWeight: '500' }}>
                            {clicks}
                          </div>
                          <div style={{
                            width: '40px',
                            height: `${barHeight}px`,
                            backgroundColor: 'var(--primary-color)',
                            borderRadius: '0.25rem 0.25rem 0 0',
                            transition: 'height 0.5s ease-in-out'
                          }} />
                          <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', marginTop: '0.5rem', textAlign: 'center' }}>
                            {new Date(date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                          </div>
                        </div>
                      );
                    })
                  }
                </div>
              )}
            </div>
          )}
        </div>
        
        <style>{`
          @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
          }
          @keyframes slideIn {
            from { transform: translateY(-20px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
          }
          @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
          }
        `}</style>
      </div>
    </div>
  );
};

export default Analytics;