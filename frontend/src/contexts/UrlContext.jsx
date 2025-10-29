import { createContext, useContext, useState } from 'react';

const UrlContext = createContext();

export const useUrlContext = () => {
  const context = useContext(UrlContext);
  if (!context) {
    throw new Error('useUrlContext must be used within UrlProvider');
  }
  return context;
};

export const UrlProvider = ({ children }) => {
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const triggerRefresh = () => {
    setRefreshTrigger(prev => prev + 1);
  };

  return (
    <UrlContext.Provider value={{ refreshTrigger, triggerRefresh }}>
      {children}
    </UrlContext.Provider>
  );
};