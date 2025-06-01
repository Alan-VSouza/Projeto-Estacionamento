import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link } from 'react-router-dom'; 
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './styles/App.css';
import ParkingLot from './components/parking/ParkingLot';
import LoginWebsite from './pages/Login/Login';
import RegisterAdminPage from './pages/Login/RegisterAdminPage'; 
import StatisticsPage from './pages/Statistics/StatisticsPage'; 
import ReportsPage from './pages/Reports/ReportsPage';
import VehicleHistoryPage from './pages/VehicleHistory/VehicleHistoryPage';
import { validateToken } from './services/api/Api';
import logo from './assets/images/logo.png';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [mobileMenuActive, setMobileMenuActive] = useState(false);

  useEffect(() => {
    const checkAuthentication = async () => {
      setIsLoading(true);
      try {
        const isValid = await validateToken(); 
        setIsAuthenticated(isValid);
      } catch (error) {
        console.error('Erro ao verificar autenticação:', error);
        setIsAuthenticated(false);
        localStorage.removeItem('jwtToken'); 
      } finally {
        setIsLoading(false);
      }
    };

    checkAuthentication();
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
    return (
      <div className="loading-screen">
        <div className="loading-container">
          <div className="loading-logo">
            <img src={logo} alt="Smart Parking Logo" className="logo-icon" />
            <h1 className="loading-title">Smart Parking</h1>
          </div>
          
          <div className="loading-spinner">
            <div className="spinner-ring"></div>
            <div className="spinner-ring"></div>
            <div className="spinner-ring"></div>
          </div>
          
          <div className="loading-text">
            <p className="loading-message">Verificando autenticação...</p>
            <div className="loading-dots">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
        
        <div className="loading-background">
          <div className="floating-shape shape-1"></div>
          <div className="floating-shape shape-2"></div>
          <div className="floating-shape shape-3"></div>
          <div className="floating-shape shape-4"></div>
        </div>
      </div>
    );
  }

  return (
    <Router>
      <div className="App">
        <header className="App-header">
          <h1>
            <img src={logo} alt="Smart Parking Logo" className="header-logo" />
            Smart Parking
          </h1>
          <nav>
            <ul className={`nav-list ${mobileMenuActive ? 'active' : ''}`}>
              {isAuthenticated && (
                <>
                  <li>
                    <Link to="/" onClick={() => setMobileMenuActive(false)}>
                      Dashboard
                    </Link>
                  </li>
                  <li className="dropdown">
                    <span>Relatórios ▼</span>
                    <ul className="dropdown-menu">
                      <li>
                        <Link to="/reports" onClick={() => setMobileMenuActive(false)}>
                          Receita Diária
                        </Link>
                      </li>
                      <li>
                        <Link to="/vehicle-history" onClick={() => setMobileMenuActive(false)}>
                          Histórico de Veículos
                        </Link>
                      </li>
                      <li>
                        <Link to="/statistics" onClick={() => setMobileMenuActive(false)}>
                          Estatísticas
                        </Link>
                      </li>
                    </ul>
                  </li>
                  <li className="dropdown">
                    <span>Sistema ▼</span>
                    <ul className="dropdown-menu">
                      <li>
                        <Link to="/settings" onClick={() => setMobileMenuActive(false)}>
                          Configurações
                        </Link>
                      </li>
                      <li>
                        <Link to="/users" onClick={() => setMobileMenuActive(false)}>
                          Usuários
                        </Link>
                      </li>
                    </ul>
                  </li>
                  <li>
                    <button onClick={handleLogout}>
                      Sair
                    </button>
                  </li>
                </>
              )}
              {!isAuthenticated && (
                <>
                  <li>
                    <Link to="/register-admin" onClick={() => setMobileMenuActive(false)}>
                      Registrar Funcionário
                    </Link>
                  </li>
                  <li>
                    <Link to="/login" onClick={() => setMobileMenuActive(false)}>
                      Fazer Login
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
                  <LoginWebsite onLoginSuccess={handleLoginSuccess} />
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
              path="/vehicle-history" 
              element={
                isAuthenticated ? (
                  <VehicleHistoryPage />
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
            <Route 
              path="/statistics" 
              element={
                isAuthenticated ? (
                  <StatisticsPage />
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
        
        <ToastContainer
          position="top-right"
          autoClose={3000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
          theme="light"
        />
      </div>
    </Router>
  );
}

export default App;
