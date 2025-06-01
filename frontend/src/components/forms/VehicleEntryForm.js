import React, { useState } from 'react';
import { toast } from 'react-toastify'; 

const modalStyles = {
  position: 'fixed',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  backgroundColor: 'rgba(255, 255, 255, 0.95)',
  backdropFilter: 'blur(20px)',
  padding: '40px',
  borderRadius: '25px',
  boxShadow: '0 25px 50px rgba(0, 0, 0, 0.2)',
  zIndex: 1000,
  width: '90%',
  maxWidth: '500px',
  border: '1px solid rgba(255, 255, 255, 0.3)',
  animation: 'modalEnter 0.3s ease-out',
};

const overlayStyles = {
  position: 'fixed',
  top: 0,
  left: 0,
  right: 0,
  bottom: 0,
  backgroundColor: 'rgba(0, 0, 0, 0.6)',
  backdropFilter: 'blur(5px)',
  zIndex: 999,
  animation: 'overlayEnter 0.3s ease-out',
};

const formGroupStyles = {
  marginBottom: '25px',
};

const labelStyles = {
  display: 'block',
  marginBottom: '8px',
  fontWeight: '600',
  color: '#4a5568',
  fontSize: '0.95em',
};

const inputStyles = {
  width: '100%',
  padding: '15px 18px',
  border: '2px solid #e2e8f0',
  borderRadius: '12px',
  boxSizing: 'border-box',
  fontSize: '1em',
  transition: 'all 0.3s ease',
  background: 'rgba(255, 255, 255, 0.8)',
  backdropFilter: 'blur(10px)',
};

const inputErrorStyles = {
  ...inputStyles,
  border: '2px solid #f56565',
  backgroundColor: '#fed7d7',
};

const selectStyles = {
  ...inputStyles,
  cursor: 'pointer',
};

const buttonContainerStyles = {
  display: 'flex',
  justifyContent: 'flex-end',
  gap: '15px',
  marginTop: '30px',
};

const buttonBaseStyles = {
  padding: '16px 24px',
  borderRadius: '12px',
  border: 'none',
  cursor: 'pointer',
  fontSize: '1.1em',
  fontWeight: '600',
  transition: 'all 0.3s ease',
  minWidth: '120px',
};

const submitButtonStyles = {
  ...buttonBaseStyles,
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  color: 'white',
  boxShadow: '0 10px 30px rgba(102, 126, 234, 0.3)',
};

const cancelButtonStyles = {
  ...buttonBaseStyles,
  background: '#e2e8f0',
  color: '#4a5568',
};

const errorMessageStyles = {
  color: '#c53030',
  fontSize: '0.9em',
  marginTop: '5px',
  padding: '8px',
  background: '#fed7d7',
  borderRadius: '5px',
  borderLeft: '4px solid #f56565',
};

const inputHintStyles = {
  color: '#666',
  fontSize: '0.8em',
  marginTop: '5px',
  display: 'block',
  lineHeight: '1.4',
};

const titleStyles = {
  margin: '0 0 30px 0',
  color: '#2d3748',
  fontWeight: '600',
  fontSize: '1.5em',
  textAlign: 'center',
};

function VehicleEntryForm({ spotId, onSubmit, onCancel }) {
  const [formData, setFormData] = useState({
    placa: '',
    tipoVeiculo: 'CARRO',
    modelo: '',
    cor: ''
  });
  
  const [errors, setErrors] = useState({});

  const validarCor = (cor) => {
  const corLimpa = cor.trim();

  if (!corLimpa) {
    return "Cor é obrigatória";
  }

  if (/^\d+$/.test(corLimpa)) {
    return "❌ Cor não pode ser apenas números. Use nomes como: branco, preto, azul, etc.";
  }

  if (/[^a-zA-ZÀ-ÿ\s]/.test(corLimpa)) {
    return "❌ Cor não pode conter caracteres especiais (@#!$%). Use apenas letras.";
  }

  if (/\d{3,}/.test(corLimpa)) {
    return "❌ Cor não pode conter sequências de números. Use nomes de cores.";
  }
  
  
  if (corLimpa.length < 3) {
    return "❌ Cor deve ter pelo menos 3 caracteres";
  }
  
    return null; 
  };


  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    if (name === 'cor') {
      const erroCor = validarCor(value);
      setErrors(prev => ({ ...prev, cor: erroCor }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    const erroCor = validarCor(formData.cor);
    if (erroCor) {
      setErrors(prev => ({ ...prev, cor: erroCor }));
      toast.error(erroCor);
      return;
    }
    
    if (!formData.placa.trim()) {
      toast.error('❌ Placa é obrigatória');
      return;
    }
    
    if (!formData.modelo.trim()) {
      toast.error('❌ Modelo é obrigatório');
      return;
    }
    
    onSubmit(formData);
  };

  return (
    <>
      <div style={overlayStyles} onClick={onCancel} />
      <div style={modalStyles}>
        <h3 style={titleStyles}>🚗 Registrar Entrada - {spotId}</h3>
        <form onSubmit={handleSubmit}>
          <div style={formGroupStyles}>
            <label htmlFor="placa" style={labelStyles}>Placa:</label>
            <input
              type="text"
              id="placa"
              name="placa"
              value={formData.placa}
              onChange={handleInputChange}
              placeholder="ABC-1234"
              maxLength="8"
              style={inputStyles}
              required
            />
          </div>

          <div style={formGroupStyles}>
            <label htmlFor="tipoVeiculo" style={labelStyles}>Tipo:</label>
            <select
              id="tipoVeiculo"
              name="tipoVeiculo"
              value={formData.tipoVeiculo}
              onChange={handleInputChange}
              style={selectStyles}
              required
            >
              <option value="CARRO">🚗 Carro</option>
              <option value="MOTO">🏍️ Moto</option>
            </select>
          </div>

          <div style={formGroupStyles}>
            <label htmlFor="modelo" style={labelStyles}>Modelo:</label>
            <input
              type="text"
              id="modelo"
              name="modelo"
              value={formData.modelo}
              onChange={handleInputChange}
              placeholder="Ex: Civic, Corolla"
              style={inputStyles}
              required
            />
          </div>

          <div style={formGroupStyles}>
            <label htmlFor="cor" style={labelStyles}>Cor:</label>
            <input
              type="text"
              id="cor"
              name="cor"
              value={formData.cor}
              onChange={handleInputChange}
              placeholder="Ex: branco, preto, azul"
              style={errors.cor ? inputErrorStyles : inputStyles}
              required
            />
            {errors.cor && (
              <div style={errorMessageStyles}>
                {errors.cor}
              </div>
            )}
            <small style={inputHintStyles}>
              ✅ Use nomes de cores: branco, preto, azul, vermelho, etc.<br/>
              ❌ Não use números: 123, 456, etc.
            </small>
          </div>

          <div style={buttonContainerStyles}>
            <button 
              type="button" 
              onClick={onCancel} 
              style={cancelButtonStyles}
            >
              Cancelar
            </button>
            <button 
              type="submit" 
              style={submitButtonStyles}
            >
              Registrar Entrada
            </button>
          </div>
        </form>
      </div>
    </>
  );
}

export default VehicleEntryForm;
