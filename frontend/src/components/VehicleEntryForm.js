import React, { useState } from 'react';

const modalStyles = {
  position: 'fixed',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  backgroundColor: 'white',
  padding: '30px', 
  borderRadius: '8px',
  boxShadow: '0 4px 8px rgba(0,0,0,0.2)',
  zIndex: 1000,
  width: '400px', 
  border: '1px solid #ccc',
};

const overlayStyles = {
  position: 'fixed',
  top: 0,
  left: 0,
  right: 0,
  bottom: 0,
  backgroundColor: 'rgba(0,0,0,0.5)',
  zIndex: 999,
};

const formGroupStyles = {
  marginBottom: '20px',
};

const labelStyles = {
  display: 'block',
  marginBottom: '8px', 
  fontWeight: 'bold',
};

const inputStyles = {
  width: '100%',
  padding: '12px', 
  border: '1px solid #ccc',
  borderRadius: '4px',
  boxSizing: 'border-box',
  fontSize: '16px', 
};

const buttonContainerStyles = {
  display: 'flex',
  justifyContent: 'flex-end',
  gap: '10px', 
  marginTop: '20px',
};

const buttonStyles = {
  padding: '12px 20px', 
  borderRadius: '4px',
  border: 'none',
  cursor: 'pointer',
  fontSize: '16px',
};

const submitButtonStyles = {
  ...buttonStyles,
  backgroundColor: '#4CAF50',
  color: 'white',
};

const cancelButtonStyles = {
  ...buttonStyles,
  backgroundColor: '#f44336',
  color: 'white',
};


function VehicleEntryForm({ spotId, onSubmit, onCancel }) {
  const [placa, setPlaca] = useState('');
  const [tipoVeiculo, setTipoVeiculo] = useState('CARRO'); 
  const [modelo, setModelo] = useState('');
  const [cor, setCor] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!placa.trim()) {
      setError('A placa é obrigatória.');
      return;
    }
    onSubmit({
      placa: placa.trim().toUpperCase(),
      tipoVeiculo,
      modelo: modelo.trim(),
      cor: cor.trim(),
    });
  };

  return (
    <>
      <div style={overlayStyles} onClick={onCancel} />
      <div style={modalStyles}>
        <h3>Registrar Entrada na Vaga: {spotId}</h3>
        <form onSubmit={handleSubmit}>
          {error && <p style={{ color: 'red' }}>{error}</p>}
          <div style={formGroupStyles}>
            <label htmlFor="placa" style={labelStyles}>Placa:</label>
            <input
              type="text"
              id="placa"
              style={inputStyles}
              value={placa}
              onChange={(e) => setPlaca(e.target.value)}
              required
              autoFocus
            />
          </div>
          <div style={formGroupStyles}>
            <label htmlFor="tipoVeiculo" style={labelStyles}>Tipo de Veículo:</label>
            <select
              id="tipoVeiculo"
              style={inputStyles}
              value={tipoVeiculo}
              onChange={(e) => setTipoVeiculo(e.target.value)}
            >
              <option value="CARRO">Carro</option>
              <option value="MOTO">Moto</option>
            </select>
          </div>
          <div style={formGroupStyles}>
            <label htmlFor="modelo" style={labelStyles}>Modelo:</label>
            <input
              type="text"
              id="modelo"
              style={inputStyles}
              value={modelo}
              onChange={(e) => setModelo(e.target.value)}
            />
          </div>
          <div style={formGroupStyles}>
            <label htmlFor="cor" style={labelStyles}>Cor:</label>
            <input
              type="text"
              id="cor"
              style={inputStyles}
              value={cor}
              onChange={(e) => setCor(e.target.value)}
            />
          </div>
          <div style={buttonContainerStyles}>
            <button type="button" style={cancelButtonStyles} onClick={onCancel}>Cancelar</button>
            <button type="submit" style={submitButtonStyles}>Registrar Entrada</button>
          </div>
        </form>
      </div>
    </>
  );
}

export default VehicleEntryForm;
