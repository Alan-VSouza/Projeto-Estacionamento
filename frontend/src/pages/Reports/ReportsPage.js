import { useState } from 'react';
import DailyRevenueReport from '../../components/reports/DailyRevenueReport';
import MonthlyRevenueReport from '../../components/reports/MonthlyRevenueReport';

function ReportsPage() {
  const [activeTab, setActiveTab] = useState('daily-revenue');

  return (
    <div className="reports-page">
      <div className="page-header">
        <h1>📊 Relatórios</h1>
        <p>Visualize relatórios e estatísticas do estacionamento</p>
      </div>

      <div className="reports-tabs">
        <button 
          className={`tab-button ${activeTab === 'daily-revenue' ? 'active' : ''}`}
          onClick={() => setActiveTab('daily-revenue')}
        >
          📅 Receita Diária
        </button>
        <button 
          className={`tab-button ${activeTab === 'monthly' ? 'active' : ''}`}
          onClick={() => setActiveTab('monthly')}
        >
          📊 Relatório Mensal
        </button>
      </div>

      <div className="reports-content">
        {activeTab === 'daily-revenue' && <DailyRevenueReport />}
        {activeTab === 'monthly' && <MonthlyRevenueReport />}
      </div>
    </div>
  );
}

export default ReportsPage;
