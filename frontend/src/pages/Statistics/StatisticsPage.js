import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { getEstatisticasTempoReal, getEstatisticasSemanais } from '../../services/api/reportsApi';

function StatisticsPage() {
  const [estatisticas, setEstatisticas] = useState({
    vagasDisponiveis: 0,
    vagasOcupadas: 0,
    totalVagas: 200,
    receitaHoje: 0,
    veiculosAtendidosHoje: 0,
    tempoMedioHoje: 0,
    ocupacaoMediaHoje: 0,
    taxaOcupacaoAtual: 0
  });

  const [estatisticasSemanais, setEstatisticasSemanais] = useState({
    receitaSemanal: 0,
    veiculosSemanal: 0,
    tempoMedioSemanal: 0,
    mediaDiariaReceita: 0,
    mediaDiariaVeiculos: 0
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadEstatisticas = async () => {
    try {
      setLoading(true);
      const [tempoReal, semanais] = await Promise.all([
        getEstatisticasTempoReal(),
        getEstatisticasSemanais()
      ]);
      
      setEstatisticas(tempoReal);
      setEstatisticasSemanais(semanais);
      setError(null);
    } catch (err) {
      setError(err.message);
      toast.error(`Erro ao carregar estatísticas: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadEstatisticas();

    const interval = setInterval(loadEstatisticas, 30000);
    return () => clearInterval(interval);
  }, []);

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value || 0);
  };

  const formatTime = (hours) => {
    const h = Math.floor(hours);
    const m = Math.round((hours - h) * 60);
    return `${h}h ${m}m`;
  };

  const getOcupacaoColor = (taxa) => {
    if (taxa >= 80) return '#f56565'; 
    if (taxa >= 60) return '#ed8936'; 
    if (taxa >= 40) return '#48bb78'; 
    return '#4299e1'; 
  };

  if (loading) {
    return (
      <div className="statistics-page">
        <div className="loading">Carregando estatísticas...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="statistics-page">
        <div className="error-message">Erro: {error}</div>
      </div>
    );
  }

  return (
    <div className="statistics-page">
      <div className="page-header">
        <h1>Estatísticas em Tempo Real</h1>
        <p>Dashboard completo do sistema de estacionamento</p>
      </div>

      <div className="stats-section">
        <h2>Status Atual</h2>
        <div className="stats-grid">
          <div className="stat-card primary">
            <div className="stat-icon">🚗</div>
            <div className="stat-content">
              <h3>Vagas Ocupadas</h3>
              <div className="stat-value">{estatisticas.vagasOcupadas}</div>
              <div className="stat-subtitle">de {estatisticas.totalVagas} vagas</div>
            </div>
          </div>

          <div className="stat-card success">
            <div className="stat-icon">🅿️</div>
            <div className="stat-content">
              <h3>Vagas Disponíveis</h3>
              <div className="stat-value">{estatisticas.vagasDisponiveis}</div>
              <div className="stat-subtitle">prontas para uso</div>
            </div>
          </div>

          <div className="stat-card info">
            <div className="stat-icon">💰</div>
            <div className="stat-content">
              <h3>Receita Hoje</h3>
              <div className="stat-value">{formatCurrency(estatisticas.receitaHoje)}</div>
              <div className="stat-subtitle">{estatisticas.veiculosAtendidosHoje} veículos</div>
            </div>
          </div>

          <div className="stat-card warning">
            <div className="stat-icon">⏱️</div>
            <div className="stat-content">
              <h3>Tempo Médio</h3>
              <div className="stat-value">{formatTime(estatisticas.tempoMedioHoje)}</div>
              <div className="stat-subtitle">permanência hoje</div>
            </div>
          </div>
        </div>
      </div>

      <div className="stats-section">
        <h2>Taxa de Ocupação</h2>
        <div className="ocupacao-visual">
          <div className="ocupacao-bar">
            <div 
              className="ocupacao-fill"
              style={{ 
                width: `${estatisticas.taxaOcupacaoAtual}%`,
                backgroundColor: getOcupacaoColor(estatisticas.taxaOcupacaoAtual)
              }}
            ></div>
          </div>
          <div className="ocupacao-info">
            <span className="ocupacao-percentage">
              {estatisticas.taxaOcupacaoAtual.toFixed(1)}%
            </span>
            <span className="ocupacao-status">
              {estatisticas.taxaOcupacaoAtual >= 80 ? 'Lotado' :
               estatisticas.taxaOcupacaoAtual >= 60 ? 'Ocupado' :
               estatisticas.taxaOcupacaoAtual >= 40 ? 'Moderado' : 'Disponível'}
            </span>
          </div>
        </div>
      </div>

      <div className="stats-section">
        <h2>Resumo Semanal (Últimos 7 dias)</h2>
        <div className="stats-grid">
          <div className="stat-card weekly">
            <div className="stat-icon">📊</div>
            <div className="stat-content">
              <h3>Receita Semanal</h3>
              <div className="stat-value">{formatCurrency(estatisticasSemanais.receitaSemanal)}</div>
              <div className="stat-subtitle">Média: {formatCurrency(estatisticasSemanais.mediaDiariaReceita)}/dia</div>
            </div>
          </div>

          <div className="stat-card weekly">
            <div className="stat-icon">🚙</div>
            <div className="stat-content">
              <h3>Veículos Atendidos</h3>
              <div className="stat-value">{estatisticasSemanais.veiculosSemanal}</div>
              <div className="stat-subtitle">Média: {estatisticasSemanais.mediaDiariaVeiculos.toFixed(1)}/dia</div>
            </div>
          </div>

          <div className="stat-card weekly">
            <div className="stat-icon">🕐</div>
            <div className="stat-content">
              <h3>Tempo Médio Semanal</h3>
              <div className="stat-value">{formatTime(estatisticasSemanais.tempoMedioSemanal)}</div>
              <div className="stat-subtitle">permanência média</div>
            </div>
          </div>

          <div className="stat-card weekly">
            <div className="stat-icon">📈</div>
            <div className="stat-content">
              <h3>Performance</h3>
              <div className="stat-value">
                {estatisticas.taxaOcupacaoAtual >= 70 ? 'Excelente' :
                 estatisticas.taxaOcupacaoAtual >= 50 ? 'Boa' :
                 estatisticas.taxaOcupacaoAtual >= 30 ? 'Regular' : 'Baixa'}
              </div>
              <div className="stat-subtitle">utilização do estacionamento</div>
            </div>
          </div>
        </div>
      </div>

      <div className="stats-actions">
        <button onClick={loadEstatisticas} className="refresh-btn">
          🔄 Atualizar Estatísticas
        </button>
      </div>
    </div>
  );
}

export default StatisticsPage;
