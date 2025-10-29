const API_BASE_URL = 'http://localhost:8080';

const getCookie = (name) => {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
  return null;
};

const initUser = async () => {
  const response = await fetch(`${API_BASE_URL}/api/v1/user/init`, {
    method: 'POST',
    credentials: 'include'
  });
  return response.json();
};

const apiRequest = async (url, options = {}) => {
  // Check if userId cookie exists
  if (!getCookie('userId')) {
    await initUser();
  }
  
  return fetch(url, {
    ...options,
    credentials: 'include'
  });
};

export const shortenUrl = async (longUrl, customAlias, expirationDate) => {
  const response = await apiRequest(`${API_BASE_URL}/api/v1/shorten`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ longUrl, customAlias, expirationDate })
  });
  return response.json();
};

export const getUserUrls = async (pageNumber = 0, count = 10) => {
  const response = await apiRequest(`${API_BASE_URL}/api/v1/urls?pageNumber=${pageNumber}&count=${count}`);
  return response.json();
};

export const getClickCounts = async (shortUrls) => {
  const params = shortUrls.map(url => `shortUrls=${encodeURIComponent(url)}`).join('&');
  const response = await apiRequest(`${API_BASE_URL}/api/v1/analytics/click-counts?${params}`);
  return response.json();
};

export const getDailyClicks = async (shortUrl, startDate, endDate) => {
  const response = await apiRequest(`${API_BASE_URL}/api/v1/analytics/daily-clicks?shortUrl=${encodeURIComponent(shortUrl)}&startDate=${startDate}&endDate=${endDate}`);
  return response.json();
};