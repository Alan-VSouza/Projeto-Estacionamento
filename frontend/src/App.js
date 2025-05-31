import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link } from 'react-router-dom'; 
import './App.css';
import ParkingLot from './components/ParkingLot';
import Login from './pages/login';
import RegisterAdminPage from './pages/RegisterAdminPage'; 
import ReportsPage from './pages/ReportsPage';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [mobileMenuActive, setMobileMenuActive] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      setIsAuthenticated(true);
    }
    setIsLoading(false);
  }, []);

  const handleLoginSuccess = () => { 
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    localStorage.removeItem('jwtToken');
    setIsAuthenticated(false);
    setMobileMenuActive(false);
  };

  const toggleMobileMenu = () => {
    setMobileMenuActive(!mobileMenuActive);
  };

  if (isLoading) {
    return <div>Carregando...</div>;
  }

  return (
    <Router>
      <div className="App">
        <header className="App-header">
          <h1>🚗 Smart Parking</h1>
          <nav>
            <ul className={`nav-list ${mobileMenuActive ? 'active' : ''}`}>
              {isAuthenticated && (
                <>
                  <li>
                    <Link to="/" onClick={() => setMobileMenuActive(false)}>
                      🏠 Dashboard
                    </Link>
                  </li>
                  <li className="dropdown">
                    <span>📊 Relatórios ▼</span>
                    <ul className="dropdown-menu">
                      <li>
                        <Link to="/reports" onClick={() => setMobileMenuActive(false)}>
                          📈 Receita Diária
                        </Link>
                      </li>
                      <li>
                        <Link to="/reports" onClick={() => setMobileMenuActive(false)}>
                          📋 Histórico
                        </Link>
                      </li>
                      <li>
                        <Link to="/reports" onClick={() => setMobileMenuActive(false)}>
                          📊 Estatísticas
                        </Link>
                      </li>
                    </ul>
                  </li>
                  <li className="dropdown">
                    <span>⚙️ Sistema ▼</span>
                    <ul className="dropdown-menu">
                      <li>
                        <Link to="/settings" onClick={() => setMobileMenuActive(false)}>
                          🔧 Configurações
                        </Link>
                      </li>
                      <li>
                        <Link to="/users" onClick={() => setMobileMenuActive(false)}>
                          👥 Usuários
                        </Link>
                      </li>
                    </ul>
                  </li>
                  <li>
                    <button onClick={handleLogout}>
                      🚪 Sair
                    </button>
                  </li>
                </>
              )}
              {!isAuthenticated && (
                <>
                  <li>
                    <Link to="/register-admin" onClick={() => setMobileMenuActive(false)}>
                      👤 Registrar Funcionário
                    </Link>
                  </li>
                  <li>
                    <Link to="/login" onClick={() => setMobileMenuActive(false)}>
                      🔑 Fazer Login
                    </Link>
                  </li>
                </>
              )}
            </ul>
            <div 
              className={`mobile-menu ${mobileMenuActive ? 'active' : ''}`}
              onClick={toggleMobileMenu}
            >
              <div className="line1"></div>
              <div className="line2"></div>
              <div className="line3"></div>
            </div>
          </nav>
        </header>
        <main>
          <Routes>
            <Route path="/register-admin" element={<RegisterAdminPage />} />
            <Route 
              path="/login" 
              element={
                !isAuthenticated ? (
                  <Login onLoginSuccess={handleLoginSuccess} />
                ) : (
                  <Navigate to="/" replace /> 
                )
              } 
            />
            <Route 
              path="/reports" 
              element={
                isAuthenticated ? (
                  <ReportsPage />
                ) : (
                  <Navigate to="/login" replace />
                )
              } 
            />
            <Route 
              path="/" 
              element={
                isAuthenticated ? (
                  <ParkingLot initialTotalSpots={15} />
                ) : (
                  <Navigate to="/login" replace /> 
                )
              } 
            />
            <Route path="*" element={<Navigate to={isAuthenticated ? "/" : "/login"} replace />} />
          </Routes>
        </main>
        <footer>
          <p>&copy; {new Date().getFullYear()} Smart Parking System - Desenvolvido por Alan, Ana e Fabiano</p>
        </footer>
      </div>
    </Router>
  );
}

export default App;
