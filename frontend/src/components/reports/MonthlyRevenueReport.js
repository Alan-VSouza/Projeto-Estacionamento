import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';

const API_BASE_URL = 'http://localhost:8080';
const getToken = () => localStorage.getItem('jwtToken');

const getMonthlyReport = async (mes, ano) => {
  const token = getToken();
  const response = await fetch(`${API_BASE_URL}/estacionamento/relatorios/mensal?mes=${mes}&ano=${ano}`, {
    headers: { 'Authorization': `Bearer ${token}` },
  });
  if (!response.ok) throw new Error('Erro ao buscar relatório mensal');
  return await response.json();
};

const exportMonthlyReportPDF = async (mes, ano) => {
  const token = getToken();
  const response = await fetch(`${API_BASE_URL}/estacionamento/relatorios/mensal/export/pdf?mes=${mes}&ano=${ano}`, {
    headers: { 'Authorization': `Bearer ${token}` },
  });
  if (!response.ok) throw new Error('Erro ao exportar PDF mensal');
  
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `relatorio-mensal-${mes}-${ano}.pdf`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  window.URL.revokeObjectURL(url);
};

function MonthlyRevenueReport() {
  const [reportData, setReportData] = useState(null);
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1);
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [exporting, setExporting] = useState(false);

  const meses = [
    '', 'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
  ];

  const loadReport = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getMonthlyReport(selectedMonth, selectedYear);
      setReportData(data);
    } catch (err) {
      setError(err.message);
      setReportData(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadReport();
  }, [selectedMonth, selectedYear]);

  const handleExportPDF = async () => {
    setExporting(true);
    try {
      await exportMonthlyReportPDF(selectedMonth, selectedYear);
      toast.success('📄 PDF mensal exportado com sucesso!');
    } catch (error) {
      toast.error(`❌ Erro ao exportar PDF: ${error.message}`);
    } finally {
      setExporting(false);
    }
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
    <div className="monthly-revenue-report">
      <div className="report-header">
        <h2>📅 Relatório Mensal</h2>
        <div className="date-selector">
          <select 
            value={selectedMonth} 
            onChange={(e) => setSelectedMonth(parseInt(e.target.value))}
            style={{ marginRight: '10px', padding: '8px', borderRadius: '5px' }}
          >
            {meses.slice(1).map((mes, index) => (
              <option key={index + 1} value={index + 1}>{mes}</option>
            ))}
          </select>
          <select 
            value={selectedYear} 
            onChange={(e) => setSelectedYear(parseInt(e.target.value))}
            style={{ padding: '8px', borderRadius: '5px' }}
          >
            {[2023, 2024, 2025, 2026].map(year => (
              <option key={year} value={year}>{year}</option>
            ))}
          </select>
        </div>
      </div>

      {loading && <div className="loading">Carregando relatório mensal...</div>}
      
      {error && <div className="error-message">Erro: {error}</div>}

      {reportData && !loading && (
        <div className="report-content">
          <div className="report-cards">
            <div className="report-card revenue">
              <h3>💰 Receita Total</h3>
              <div className="value">{formatCurrency(reportData.receitaTotal || 0)}</div>
            </div>
            
            <div className="report-card vehicles">
              <h3>🚗 Veículos Atendidos</h3>
              <div className="value">{reportData.totalVeiculos || 0}</div>
            </div>
            
            <div className="report-card avg-time">
              <h3>⏱️ Tempo Médio</h3>
              <div className="value">{formatTime(reportData.tempoMedioHoras || 0)}</div>
            </div>
            
            <div className="report-card best-day">
              <h3>🏆 Melhor Dia</h3>
              <div className="value">
                Dia {reportData.melhorDia}
                <div style={{ fontSize: '0.7em', marginTop: '5px' }}>
                  {formatCurrency(reportData.melhorReceita || 0)}
                </div>
              </div>
            </div>
          </div>

          <div className="monthly-stats">
            <h3>📊 Estatísticas do Mês</h3>
            <div className="stats-grid">
              <div className="stat-item">
                <span className="stat-label">Receita Média Diária:</span>
                <span className="stat-value">{formatCurrency(reportData.receitaMediaDiaria || 0)}</span>
              </div>
              <div className="stat-item">
                <span className="stat-label">Veículos Média Diária:</span>
                <span className="stat-value">{(reportData.veiculosMediaDiaria || 0).toFixed(1)}</span>
              </div>
              <div className="stat-item">
                <span className="stat-label">Dias no Mês:</span>
                <span className="stat-value">{reportData.diasNoMes}</span>
              </div>
            </div>
          </div>

          <div className="export-section">
            <h3>📥 Exportar Relatório Mensal</h3>
            <div className="export-buttons">
              <button 
                onClick={handleExportPDF} 
                disabled={exporting}
                className="export-btn pdf-btn"
              >
                {exporting ? '⏳ Exportando...' : '📄 Exportar PDF'}
              </button>
            </div>
          </div>

          <div className="report-summary">
            <h3>📋 Resumo de {meses[selectedMonth]} {selectedYear}</h3>
            <p><strong>Período:</strong> {meses[selectedMonth]} de {selectedYear}</p>
            <p><strong>Total de veículos:</strong> {reportData.totalVeiculos || 0}</p>
            <p><strong>Receita gerada:</strong> {formatCurrency(reportData.receitaTotal || 0)}</p>
            <p><strong>Tempo médio de permanência:</strong> {formatTime(reportData.tempoMedioHoras || 0)}</p>
            <p><strong>Melhor dia do mês:</strong> Dia {reportData.melhorDia} com {formatCurrency(reportData.melhorReceita || 0)}</p>
          </div>
        </div>
      )}
    </div>
  );
}

export default MonthlyRevenueReport;
