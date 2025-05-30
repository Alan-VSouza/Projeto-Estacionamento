import React, { useState, useEffect } from 'react';
import { getDailyRevenueReport } from '../../services/reportsApi';

function DailyRevenueReport() {
  const [reportData, setReportData] = useState(null);
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const loadReport = async (date) => {
    setLoading(true);
    setError(null);
    try {
      const data = await getDailyRevenueReport(date);
      setReportData(data);
    } catch (err) {
      setError(err.message);
      setReportData(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadReport(selectedDate);
  }, [selectedDate]);

  const handleDateChange = (e) => {
    setSelectedDate(e.target.value);
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  const formatTime = (hours) => {
    const h = Math.floor(hours);
    const m = Math.round((hours - h) * 60);
    return `${h}h ${m}m`;
  };

  return (
    <div className="daily-revenue-report">
      <div className="report-header">
        <h2>Relatório Diário de Receita</h2>
        <div className="date-selector">
          <label htmlFor="report-date">Data:</label>
          <input
            type="date"
            id="report-date"
            value={selectedDate}
            onChange={handleDateChange}
            max={new Date().toISOString().split('T')[0]}
          />
        </div>
      </div>

      {loading && <div className="loading">Carregando relatório...</div>}
      
      {error && <div className="error-message">Erro: {error}</div>}

      {reportData && !loading && (
        <div className="report-content">
          <div className="report-cards">
            <div className="report-card revenue">
              <h3>Receita Total</h3>
              <div className="value">{formatCurrency(reportData.receitaTotal || 0)}</div>
            </div>
            
            <div className="report-card vehicles">
              <h3>Veículos Atendidos</h3>
              <div className="value">{reportData.quantidade || 0}</div>
            </div>
            
            <div className="report-card avg-time">
              <h3>Tempo Médio</h3>
              <div className="value">{formatTime(reportData.tempoMedioHoras || 0)}</div>
            </div>
            
            <div className="report-card occupancy">
              <h3>Taxa de Ocupação</h3>
              <div className="value">{((reportData.ocupacaoMedia || 0) * 100).toFixed(1)}%</div>
            </div>
          </div>

          <div className="report-summary">
            <h3>Resumo do Dia</h3>
            <p><strong>Data:</strong> {new Date(selectedDate).toLocaleDateString('pt-BR')}</p>
            <p><strong>Total de veículos:</strong> {reportData.quantidade || 0}</p>
            <p><strong>Receita gerada:</strong> {formatCurrency(reportData.receitaTotal || 0)}</p>
            <p><strong>Tempo médio de permanência:</strong> {formatTime(reportData.tempoMedioHoras || 0)}</p>
            <p><strong>Taxa de ocupação média:</strong> {((reportData.ocupacaoMedia || 0) * 100).toFixed(2)}%</p>
          </div>
        </div>
      )}
    </div>
  );
}

export default DailyRevenueReport;