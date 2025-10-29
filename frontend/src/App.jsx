import UrlShortenerForm from './components/UrlShortenerForm.jsx'
import UrlList from './components/UrlList.jsx'
import { UrlProvider } from './contexts/UrlContext.jsx'

function App() {

  return (
    <UrlProvider>
      <div style={{ 
        display: 'flex', 
        padding: '2rem',
        minHeight: '100vh',
        gap: '0'
      }}>
        <div style={{ width: '40%', paddingRight: '2rem' }}>
          <UrlShortenerForm />
        </div>
        <div style={{ width: '20%' }}>
          {/* Empty space between */}
        </div>
        <div style={{ width: '40%', paddingLeft: '2rem' }}>
          <UrlList />
        </div>
      </div>
    </UrlProvider>
  )
}

export default App