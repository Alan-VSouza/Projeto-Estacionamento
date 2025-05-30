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
  };

  if (isLoading) {
    return <div>Carregando...</div>;
  }

  return (
    <Router>
      <div className="App">
        <header className="App-header">
          <h1>Gerenciador de Estacionamento</h1>
          <nav>
            {isAuthenticated && (
              <>
                <Link to="/" style={{ color: 'white', marginRight: '10px' }}>Estacionamento</Link>
                <Link to="/reports" style={{ color: 'white', marginRight: '10px' }}>Relatórios</Link>
                <button onClick={handleLogout}>Sair</button>
              </>
            )}
            {!isAuthenticated && (
              <>
                <Link to="/register-admin" style={{ color: 'white', marginRight: '10px' }}>Registrar Funcionário</Link>
                <Link to="/login" style={{ color: 'white' }}>Fazer Login</Link>
              </>
            )}
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
          <p>&copy; {new Date().getFullYear()} Meu Projeto de Estacionamento</p>
        </footer>
      </div>
    </Router>
  );
}

export default App;
