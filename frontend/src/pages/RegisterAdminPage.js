import React, { useState } from 'react';
import { registerUser } from '../services/api';

function RegisterAdminPage() {
  const [name, setName] = useState('');
  const [lastname, setLastname] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(null);
    setSuccessMessage('');

    if (password !== confirmPassword) {
      setError('As senhas não coincidem.');
      return;
    }
    setLoading(true);
    try {
      const userData = { name, lastname, email, password };
      await registerUser(userData);
      setSuccessMessage('Administrador registrado com sucesso! Você pode precisar reiniciar o servidor para que as permissões de registro de admin sejam revogadas, ou fazer login.');
      setName('');
      setLastname('');
      setEmail('');
      setPassword('');
      setConfirmPassword('');
    } catch (err) {
      setError(err.message || 'Falha ao registrar administrador.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-admin-container">
      <h2>Registrar Administrador Inicial</h2>
      <p>Esta página deve ser usada apenas uma vez para criar a conta de administrador principal.</p>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="name">Nome:</label>
          <input type="text" id="name" value={name} onChange={(e) => setName(e.target.value)} required />
        </div>
        <div>
          <label htmlFor="lastname">Sobrenome:</label>
          <input type="text" id="lastname" value={lastname} onChange={(e) => setLastname(e.target.value)} required />
        </div>
        <div>
          <label htmlFor="email">Email:</label>
          <input type="email" id="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </div>
        <div>
          <label htmlFor="password">Senha:</label>
          <input type="password" id="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </div>
        <div>
          <label htmlFor="confirmPassword">Confirmar Senha:</label>
          <input type="password" id="confirmPassword" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} required />
        </div>
        {error && <p style={{ color: 'red' }}>{error}</p>}
        {successMessage && <p style={{ color: 'green' }}>{successMessage}</p>}
        <button type="submit" disabled={loading}>
          {loading ? 'Registrando...' : 'Registrar Administrador'}
        </button>
      </form>
    </div>
  );
}

export default RegisterAdminPage;
