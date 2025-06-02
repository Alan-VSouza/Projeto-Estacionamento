import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { getDailyRevenueReport, exportReportPDF, exportReportCSV } from '../../services/api/reportsApi';
import ReactCardFlip from 'react-card-flip'; 

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

  const toggleFlip = (card) => {
    setFlippedCards(prev => ({
      ...prev,
      [card]: !prev[card]
    }));
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

            <div onClick={() => toggleFlip('revenue')}>
              <ReactCardFlip isFlipped={flippedCards.revenue} flipDirection="horizontal">
                <div className="report-card revenue">
                  <h3>Receita Total</h3>
                  <div className="value">{formatCurrency(reportData.receitaTotal || 0)}</div>
                  <small style={{fontSize: '0.8em', opacity: 0.8}}>Clique para ver a descrição</small>
                </div>
                <div className="report-card revenue">
                  <h3>Receita Total</h3>
                  <div className="value" style={{fontSize: '0.9em', marginTop: '10px'}}>
                    Soma do valor de todos os pagamentos realizados no dia.
                  </div>
                </div>
              </ReactCardFlip>
            </div>

            <div onClick={() => toggleFlip('vehicles')}>
              <ReactCardFlip isFlipped={flippedCards.vehicles} flipDirection="horizontal">
                <div className="report-card vehicles">
                  <h3>Veículos Atendidos</h3>
                  <div className="value">{reportData.quantidade || 0}</div>
                  <small style={{fontSize: '0.8em', opacity: 0.8}}>Clique para ver a descrição</small>
                </div>
                <div className="report-card vehicles">
                  <h3>Veículos Atendidos</h3>
                  <div className="value" style={{fontSize: '0.9em', marginTop: '10px'}}>
                    Quantidade de veículos que utilizaram o estacionamento no dia.
                  </div>
                </div>
              </ReactCardFlip>
            </div>

            <div onClick={() => toggleFlip('avgTime')}>
              <ReactCardFlip isFlipped={flippedCards.avgTime} flipDirection="horizontal">
                <div className="report-card avg-time">
                  <h3>Tempo Médio</h3>
                  <div className="value">{formatTime(reportData.tempoMedioHoras || 0)}</div>
                  <small style={{fontSize: '0.8em', opacity: 0.8}}>Clique para ver a descrição</small>
                </div>
                <div className="report-card avg-time">
                  <h3>Tempo Médio</h3>
                  <div className="value" style={{fontSize: '0.9em', marginTop: '10px'}}>
                    Média de permanência dos veículos no estacionamento no dia.
                  </div>
                </div>
              </ReactCardFlip>
            </div>

            <div onClick={() => toggleFlip('occupancy')}>
              <ReactCardFlip isFlipped={flippedCards.occupancy} flipDirection="horizontal">
                <div className="report-card occupancy">
                  <h3>Taxa de Ocupação</h3>
                  <div className="value">{((reportData.ocupacaoMedia || 0) * 100).toFixed(1)}%</div>
                  <small style={{fontSize: '0.8em', opacity: 0.8}}>Clique para ver a descrição</small>
                </div>
                <div className="report-card occupancy">
                  <h3>Taxa de Ocupação</h3>
                  <div className="value" style={{fontSize: '0.9em', marginTop: '10px'}}>
                    Porcentagem média de vagas ocupadas ao longo do dia.
                  </div>
                </div>
              </ReactCardFlip>
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