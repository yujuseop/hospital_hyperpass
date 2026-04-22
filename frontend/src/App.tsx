import { Routes, Route, Navigate } from 'react-router-dom'
import LandingPage from './pages/LandingPage'
import AuthPage from './pages/AuthPage'
import SymptomsPage from './pages/SymptomsPage'
import WaitingPage from './pages/WaitingPage'

function App() {
  return (
    <div className="max-w-md mx-auto min-h-screen">
      <Routes>
        <Route path="/"         element={<LandingPage />} />
        <Route path="/auth"     element={<AuthPage />} />
        <Route path="/symptoms" element={<SymptomsPage />} />
        <Route path="/waiting"  element={<WaitingPage />} />
        <Route path="*"         element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  )
}

export default App
