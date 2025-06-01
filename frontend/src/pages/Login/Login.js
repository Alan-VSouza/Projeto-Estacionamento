import React, { useState } from 'react';
import { toast } from 'react-toastify';
import { loginUser } from '../../services/api/Api';

function LoginWebsite({ onLoginSuccess }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const token = await loginUser(email, password);
      localStorage.setItem('jwtToken', token); 
      if (onLoginSuccess) {
        onLoginSuccess(token);
      }
      toast.success('🎉 Login realizado com sucesso!', {
        position: "top-right",
        autoClose: 3000,
      });
    } catch (err) {
      setError(err.message);
      toast.error(`❌ ${err.message}`, {
        position: "top-right",
        autoClose: 4000,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div>
          <label htmlFor="password">Senha:</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        {error && <p style={{ color: 'red' }}>{error}</p>}
        <button type="submit" disabled={loading}>
          {loading ? 'Entrando...' : 'Entrar'}
        </button>
      </form>
    </div>
  );
}

export default LoginWebsite;
