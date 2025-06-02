import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { getDailyRevenueReport, exportReportPDF, exportReportCSV } from '../../services/api/reportsApi';

function DailyRevenueReport() {
  const [reportData, setReportData] = useState(null);
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [exporting, setExporting] = useState(false);

  const [flippedCards, setFlippedCards] = useState({
    revenue: false,
    vehicles: false,
    avgTime: false,
    occupancy: false
  });

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

  const handleExportPDF = async () => {
    setExporting(true);
    try {
      await exportReportPDF(selectedDate);
      toast.success('📄 PDF exportado com sucesso!', {
        position: "top-right",
        autoClose: 3000,
      });
    } catch (error) {
      toast.error(`❌ Erro ao exportar PDF: ${error.message}`, {
        position: "top-right",
        autoClose: 4000,
      });
    } finally {
      setExporting(false);
    }
  };

  const handleExportCSV = async () => {
    setExporting(true);
    try {
      await exportReportCSV(selectedDate);
      toast.success('📊 CSV exportado com sucesso!', {
        position: "top-right",
        autoClose: 3000,
      });
    } catch (error) {
      toast.error(`❌ Erro ao exportar CSV: ${error.message}`, {
        position: "top-right",
        autoClose: 4000,
      });
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

  const toggleFlip = (cardType) => {
    setFlippedCards(prev => ({
      ...prev,
      [cardType]: !prev[cardType]
    }));
  };

  return (
    <div className="daily-revenue-report">
      <div className="report-header">
        <h2>📊 Relatório Diário de Receita</h2>
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

      {loading && <div className="loading">⏳ Carregando relatório...</div>}
      
      {error && <div className="error-message">❌ Erro: {error}</div>}

      {reportData && !loading && (
        <div className="report-content">
          <div className="report-cards">
            <div className="flip-card" onClick={() => toggleFlip('revenue')}>
              <div className={`flip-card-inner ${flippedCards.revenue ? 'flipped' : ''}`}>
                <div className="flip-card-front report-card revenue">
                  <h3>💰 Receita Total</h3>
                  <div className="value">{formatCurrency(reportData.receitaTotal || 0)}</div>
                  <small className="flip-hint">💡 Clique para ver a descrição</small>
                </div>
                <div className="flip-card-back report-card revenue">
                  <h3>💰 Receita Total</h3>
                  <div className="description">
                    Soma do valor de todos os pagamentos realizados no dia.
                  </div>
                  <small className="flip-hint">💡 Clique para voltar ao valor</small>
                </div>
              </div>
            </div>

            <div className="flip-card" onClick={() => toggleFlip('vehicles')}>
              <div className={`flip-card-inner ${flippedCards.vehicles ? 'flipped' : ''}`}>
                <div className="flip-card-front report-card vehicles">
                  <h3>🚗 Veículos Atendidos</h3>
                  <div className="value">{reportData.quantidade || 0}</div>
                  <small className="flip-hint">💡 Clique para ver a descrição</small>
                </div>
                <div className="flip-card-back report-card vehicles">
                  <h3>🚗 Veículos Atendidos</h3>
                  <div className="description">
                    Quantidade de veículos que utilizaram o estacionamento no dia.
                  </div>
                  <small className="flip-hint">💡 Clique para voltar ao valor</small>
                </div>
              </div>
            </div>

            <div className="flip-card" onClick={() => toggleFlip('avgTime')}>
              <div className={`flip-card-inner ${flippedCards.avgTime ? 'flipped' : ''}`}>
                <div className="flip-card-front report-card avg-time">
                  <h3>⏱️ Tempo Médio</h3>
                  <div className="value">{formatTime(reportData.tempoMedioHoras || 0)}</div>
                  <small className="flip-hint">💡 Clique para ver a descrição</small>
                </div>
                <div className="flip-card-back report-card avg-time">
                  <h3>⏱️ Tempo Médio</h3>
                  <div className="description">
                    Média de permanência dos veículos no estacionamento no dia.
                  </div>
                  <small className="flip-hint">💡 Clique para voltar ao valor</small>
                </div>
              </div>
            </div>

            <div className="flip-card" onClick={() => toggleFlip('occupancy')}>
              <div className={`flip-card-inner ${flippedCards.occupancy ? 'flipped' : ''}`}>
                <div className="flip-card-front report-card occupancy">
                  <h3>📈 Taxa de Ocupação</h3>
                  <div className="value">{((reportData.ocupacaoMedia || 0) * 100).toFixed(1)}%</div>
                  <small className="flip-hint">💡 Clique para ver a descrição</small>
                </div>
                <div className="flip-card-back report-card occupancy">
                  <h3>📈 Taxa de Ocupação</h3>
                  <div className="description">
                    Porcentagem média de vagas ocupadas ao longo do dia.
                  </div>
                  <small className="flip-hint">💡 Clique para voltar ao valor</small>
                </div>
              </div>
            </div>
          </div>

          <div className="export-section">
            <h3>📥 Exportar Relatório</h3>
            <div className="export-buttons">
              <button 
                onClick={handleExportPDF} 
                disabled={exporting}
                className="export-btn pdf-btn"
              >
                {exporting ? '⏳ Exportando...' : '📄 Exportar PDF'}
              </button>
              <button 
                onClick={handleExportCSV} 
                disabled={exporting}
                className="export-btn csv-btn"
              >
                {exporting ? '⏳ Exportando...' : '📊 Exportar CSV'}
              </button>
            </div>
          </div>

          <div className="report-summary">
            <h3>📋 Resumo do Dia</h3>
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