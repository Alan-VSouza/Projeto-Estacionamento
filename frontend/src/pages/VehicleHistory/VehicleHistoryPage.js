import React, { useState } from 'react';
import { toast } from 'react-toastify';
import { getVehicleHistory } from '../../services/api/reportsApi';

function VehicleHistoryPage() {
  const [placa, setPlaca] = useState('');
  const [historico, setHistorico] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searched, setSearched] = useState(false);
  const [placaError, setPlacaError] = useState(''); 

  const validarPlaca = (placa) => {
    const placaLimpa = placa.trim();
    
    if (!placaLimpa) {
      return "Por favor, digite uma placa válida";
    }
    
    if (!/^[A-Z0-9]+$/.test(placaLimpa)) {
      return "❌ Placa inválida. Use apenas letras maiúsculas e números, sem caracteres especiais.";
    }

    if (placaLimpa.length !== 7) {
      return "❌ Placa deve ter exatamente 7 caracteres (ABC1234).";
    }

    const formatoAntigo = /^[A-Z]{3}[0-9]{4}$/; 
    const formatoMercosulCarro = /^[A-Z]{3}[0-9]{1}[A-Z]{1}[0-9]{2}$/; 
    const formatoMercosulMoto = /^[A-Z]{3}[0-9]{2}[A-Z]{1}[0-9]{1}$/; 
    
    if (!formatoAntigo.test(placaLimpa) && 
        !formatoMercosulCarro.test(placaLimpa) && 
        !formatoMercosulMoto.test(placaLimpa)) {
      return "❌ Formato de placa inválido. Use: ABC1234 (antigo) ou ABC1A23 (Mercosul).";
    }
    
    return null; 
  };

  const formatarPlaca = (placa) => {
    if (!placa) return '';
    return placa.replace(/[^a-zA-Z0-9]/g, '').toUpperCase();
  };

  const handlePlacaChange = (e) => {
    const valorDigitado = e.target.value;
    const placaFormatada = formatarPlaca(valorDigitado);
    
    setPlaca(placaFormatada);

    const erro = validarPlaca(placaFormatada);
    setPlacaError(erro);
  };

  const handleSearch = async (e) => {
    e.preventDefault();

    const erro = validarPlaca(placa);
    if (erro) {
      setPlacaError(erro);
      toast.error(erro);
      return;
    }
    
    setLoading(true);
    setError(null);
    setSearched(true);
    setPlacaError(''); 
    
    try {
      const data = await getVehicleHistory(placa.trim());
      setHistorico(data);
      toast.success(`✅ Histórico encontrado para ${placa}`);
    } catch (err) {
      setError(err.message);
      setHistorico([]);
      toast.error(`❌ Erro ao buscar histórico: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const formatDateTime = (dateString) => {
    return new Date(dateString).toLocaleString('pt-BR');
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value || 0);
  };

  const calculateDuration = (entrada, saida) => {
    if (!saida) return 'Em andamento';
    const start = new Date(entrada);
    const end = new Date(saida);
    const diffMs = end - start;
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
    return `${diffHours}h ${diffMinutes}m`;
  };

  return (
    <div className="vehicle-history-page">
      <div className="page-header">
        <h1>🔍 Histórico de Veículos</h1>
        <p>Consulte o histórico completo de entradas e saídas por placa</p>
      </div>

      <div className="search-section">
        <form onSubmit={handleSearch} className="search-form">
          <div className="search-input-group">
            <input
              type="text"
              placeholder="Digite a placa do veículo (ex: ABC1234)"
              value={placa}
              onChange={handlePlacaChange}
              className={`search-input ${placaError ? 'input-error' : ''}`} 
              maxLength="7"
            />
            <button 
              type="submit" 
              disabled={loading || placaError}
              className="search-button"
            >
              {loading ? '🔄 Buscando...' : '🔍 Buscar'}
            </button>
          </div>

          {placaError && (
            <div className="placa-error-message">
              {placaError}
            </div>
          )}

          <div className="search-hints">
            <small>
              ✅ Letras serão automaticamente maiúsculas<br/>
              ✅ Formatos aceitos: ABC1234 (antigo) ou ABC1A23 (Mercosul)<br/>
              ❌ Não use símbolos: @#!$%-, etc.
            </small>
          </div>
        </form>
      </div>

      {error && (
        <div className="error-message">
          ❌ {error}
        </div>
      )}

      {searched && !loading && !error && historico.length === 0 && (
        <div className="no-results">
          📭 Nenhum histórico encontrado para a placa <strong>{placa}</strong>
        </div>
      )}

      {historico.length > 0 && (
        <div className="history-results">
          <div className="results-header">
            <h2>📋 Histórico da Placa: <span className="plate-highlight">{placa}</span></h2>
            <p className="results-count">📊 {historico.length} registro(s) encontrado(s)</p>
          </div>
          
          <div className="history-grid">
            {historico.map((registro, index) => (
              <div key={index} className="history-card">
                <div className="card-header">
                  <span className="entry-number">#{historico.length - index}</span>
                  <span className={`status-badge ${registro.horaSaida ? 'completed' : 'active'}`}>
                    {registro.horaSaida ? '✅ Finalizado' : '🔄 Em andamento'}
                  </span>
                </div>
                
                <div className="card-content">
                  <div className="time-info">
                    <div className="time-entry">
                      <span className="time-label">🚪 Entrada:</span>
                      <span className="time-value">{formatDateTime(registro.horaEntrada)}</span>
                    </div>
                    
                    <div className="time-entry">
                      <span className="time-label">🚪 Saída:</span>
                      <span className="time-value">
                        {registro.horaSaida ? formatDateTime(registro.horaSaida) : '🔄 Em andamento'}
                      </span>
                    </div>
                    
                    <div className="time-entry">
                      <span className="time-label">⏱️ Duração:</span>
                      <span className="time-value">{calculateDuration(registro.horaEntrada, registro.horaSaida)}</span>
                    </div>
                  </div>
                  
                  <div className="payment-info">
                    <div className="payment-amount">
                      <span className="payment-label">💰 Valor:</span>
                      <span className="payment-value">{formatCurrency(registro.valor)}</span>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

export default VehicleHistoryPage;
