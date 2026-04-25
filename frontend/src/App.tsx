import { Routes, Route, Navigate } from "react-router-dom";
import { useLocation } from "react-router-dom";
import LandingPage from "./pages/LandingPage";
import AuthPage from "./pages/AuthPage";
import SymptomsPage from "./pages/SymptomsPage";
import AdminPage from "./pages/AdminPage";

function App() {
  const location = useLocation();
  const isAdminRoute = location.pathname.startsWith("/admin");

  return (
    <div
      className={`${isAdminRoute ? "max-w-4xl" : "max-w-md"} mx-auto min-h-screen`}
    >
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/auth" element={<AuthPage />} />
        <Route path="/symptoms" element={<SymptomsPage />} />
        <Route path="/admin" element={<AdminPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  );
}

export default App;
