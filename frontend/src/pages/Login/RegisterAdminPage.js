import React, { useState } from 'react';
import { toast } from 'react-toastify';
import { registerUser } from '../../services/api/Api';

function RegisterAdminPage() {
  const [name, setName] = useState('');
  const [lastname, setLastname] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const validarNome = (nome, campo) => {
    const nomeLimpo = nome.trim();
    
    if (!nomeLimpo) {
      return `${campo} é obrigatório`;
    }
    if (/[^a-zA-ZÀ-ÿ\s]/.test(nomeLimpo)) {
      return `❌ ${campo} não pode conter números ou caracteres especiais`;
    }
    
    if (nomeLimpo.length < 2) {
      return `❌ ${campo} deve ter pelo menos 2 caracteres`;
    }
    
    return null;
  };

  const validarEmail = (email) => {
    const emailLimpo = email.trim();
    
    if (!emailLimpo) {
      return "Email é obrigatório";
    }

    if (/[^a-zA-Z0-9@._-]/.test(emailLimpo)) {
      return "❌ Email não pode conter caracteres especiais além de @, ., _ e -";
    }

    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(emailLimpo)) {
      return "❌ Formato de email inválido";
    }
    
    return null;
  };

  const validarSenha = (senha) => {
    const senhaLimpa = senha.trim();
    
    if (!senhaLimpa) {
      return "Senha é obrigatória";
    }

    if (/[^a-zA-Z0-9!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/.test(senhaLimpa)) {
      return "❌ Senha contém caracteres não permitidos";
    }
    
    if (senhaLimpa.length < 8) {
      return "❌ Senha deve ter pelo menos 8 caracteres";
    }

    if (!/(?=.*[a-zA-Z])(?=.*\d)/.test(senhaLimpa)) {
      return "❌ Senha deve conter pelo menos uma letra e um número";
    }
    
    return null;
  };

  const capitalizarNome = (nome) => {
    if (!nome) return '';
    return nome.charAt(0).toUpperCase() + nome.slice(1).toLowerCase();
  };

  const handleNameChange = (e) => {
    const valor = capitalizarNome(e.target.value);
    setName(valor);
    const erro = validarNome(valor, 'Nome');
    setErrors(prev => ({ ...prev, name: erro }));
  };

  const handleLastnameChange = (e) => {
    const valor = capitalizarNome(e.target.value);
    setLastname(valor);
    const erro = validarNome(valor, 'Sobrenome');
    setErrors(prev => ({ ...prev, lastname: erro }));
  };

  const handleEmailChange = (e) => {
    const valor = e.target.value.toLowerCase();
    setEmail(valor);
    const erro = validarEmail(valor);
    setErrors(prev => ({ ...prev, email: erro }));
  };

  const handlePasswordChange = (e) => {
    const valor = e.target.value;
    setPassword(valor);
    const erro = validarSenha(valor);
    setErrors(prev => ({ ...prev, password: erro }));

    if (confirmPassword) {
      const erroConfirm = valor !== confirmPassword ? "❌ Senhas não coincidem" : null;
      setErrors(prev => ({ ...prev, confirmPassword: erroConfirm }));
    }
  };

  const handleConfirmPasswordChange = (e) => {
    const valor = e.target.value;
    setConfirmPassword(valor);
    const erro = valor !== password ? "❌ Senhas não coincidem" : null;
    setErrors(prev => ({ ...prev, confirmPassword: erro }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(null);
    setSuccessMessage('');

    const erroName = validarNome(name, 'Nome');
    const erroLastname = validarNome(lastname, 'Sobrenome');
    const erroEmail = validarEmail(email);
    const erroPassword = validarSenha(password);
    const erroConfirm = password !== confirmPassword ? "❌ Senhas não coincidem" : null;

    if (erroName || erroLastname || erroEmail || erroPassword || erroConfirm) {
      setErrors({
        name: erroName,
        lastname: erroLastname,
        email: erroEmail,
        password: erroPassword,
        confirmPassword: erroConfirm
      });
      toast.error('❌ Corrija os erros antes de continuar');
      return;
    }

    setLoading(true);
    try {
      const userData = { 
        name: name.trim(), 
        lastname: lastname.trim(), 
        email: email.trim(), 
        password: password.trim() 
      };
      await registerUser(userData);
      setSuccessMessage('✅ Administrador registrado com sucesso!');
      toast.success('🎉 Administrador registrado com sucesso!');

      setName('');
      setLastname('');
      setEmail('');
      setPassword('');
      setConfirmPassword('');
      setErrors({});
    } catch (err) {
      setError(err.message || 'Falha ao registrar administrador.');
      toast.error(`❌ ${err.message || 'Falha ao registrar administrador.'}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page-wrapper">
      <div className="register-admin-container">
        <h2>👤 Registrar Administrador Inicial</h2>
        <p>Esta página deve ser usada apenas uma vez para criar a conta de administrador principal.</p>
        
        <form onSubmit={handleSubmit}>
          <div>
            <label htmlFor="name">Nome:</label>
            <input 
              type="text" 
              id="name" 
              value={name} 
              onChange={handleNameChange}
              className={errors.name ? 'input-error' : ''}
              required 
            />
            {errors.name && (
              <div className="error-message">
                {errors.name}
              </div>
            )}
            <small>
              ✅ Primeira letra será automaticamente maiúscula
            </small>
          </div>
          
          <div>
            <label htmlFor="lastname">Sobrenome:</label>
            <input 
              type="text" 
              id="lastname" 
              value={lastname} 
              onChange={handleLastnameChange}
              className={errors.lastname ? 'input-error' : ''}
              required 
            />
            {errors.lastname && (
              <div className="error-message">
                {errors.lastname}
              </div>
            )}
            <small>
              ✅ Primeira letra será automaticamente maiúscula
            </small>
          </div>
          
          <div>
            <label htmlFor="email">Email:</label>
            <input 
              type="email" 
              id="email" 
              value={email} 
              onChange={handleEmailChange}
              className={errors.email ? 'input-error' : ''}
              required 
            />
            {errors.email && (
              <div className="error-message">
                {errors.email}
              </div>
            )}
            <small>
              ✅ Use apenas letras, números e @, ., _, -
            </small>
          </div>
          
          <div>
            <label htmlFor="password">Senha:</label>
            <input 
              type="password" 
              id="password" 
              value={password} 
              onChange={handlePasswordChange}
              className={errors.password ? 'input-error' : ''}
              required 
            />
            {errors.password && (
              <div className="error-message">
                {errors.password}
              </div>
            )}
            <small>
              ✅ Mínimo 8 caracteres, pelo menos 1 letra e 1 número
            </small>
          </div>
          
          <div>
            <label htmlFor="confirmPassword">Confirmar Senha:</label>
            <input 
              type="password" 
              id="confirmPassword" 
              value={confirmPassword} 
              onChange={handleConfirmPasswordChange}
              className={errors.confirmPassword ? 'input-error' : ''}
              required 
            />
            {errors.confirmPassword && (
              <div className="error-message">
                {errors.confirmPassword}
              </div>
            )}
            <small>
              ✅ Digite a mesma senha para confirmar
            </small>
          </div>
          
          {error && <p style={{ color: 'red' }}>{error}</p>}
          {successMessage && <p style={{ color: 'green' }}>{successMessage}</p>}
          
          <button 
            type="submit" 
            disabled={loading || Object.values(errors).some(erro => erro)}
          >
            {loading ? '⏳ Registrando...' : '✅ Registrar Administrador'}
          </button>
        </form>
      </div>
    </div>
  );
}

export default RegisterAdminPage;
