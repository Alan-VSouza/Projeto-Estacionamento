const API_BASE_URL = 'http://localhost:8080';

const getToken = () => localStorage.getItem('jwtToken');

export const getDailyRevenueReport = async (date) => {
  const token = getToken();
  try {
    const response = await fetch(`${API_BASE_URL}/api/relatorios/desempenho?data=${date}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
    if (!response.ok) {
      if (response.status === 401) throw new Error('Não autorizado. Faça login novamente.');
      const errorData = await response.json().catch(() => ({ message: 'Erro ao buscar relatório da API' }));
      throw new Error(errorData.message || 'Erro ao buscar relatório da API');
    }
    return await response.json();
  } catch (error) {
    console.error("Erro em getDailyRevenueReport:", error);
    throw error;
  }
};

export const exportReportPDF = async (date) => {
  const token = getToken();
  try {
    const response = await fetch(`${API_BASE_URL}/api/relatorios/desempenho/export/pdf?data=${date}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
    
    if (!response.ok) {
      throw new Error('Erro ao exportar relatório em PDF');
    }
    
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `relatorio-${date}.pdf`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error("Erro em exportReportPDF:", error);
    throw error;
  }
};

export const exportReportCSV = async (date) => {
  const token = getToken();
  try {
    const response = await fetch(`${API_BASE_URL}/api/relatorios/desempenho/export/csv?data=${date}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
    
    if (!response.ok) {
      throw new Error('Erro ao exportar relatório em CSV');
    }

    const arrayBuffer = await response.arrayBuffer();
    const blob = new Blob([arrayBuffer], { 
      type: 'text/csv;charset=utf-8;' 
    });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `relatorio-${date}.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error("Erro em exportReportCSV:", error);
    throw error;
  }
};

export const getAvailableSpots = async () => {
  const token = getToken();
  try {
    const response = await fetch(`${API_BASE_URL}/api/relatorios/vagas-disponiveis`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({ message: 'Erro ao buscar vagas disponíveis' }));
      throw new Error(errorData.message || 'Erro ao buscar vagas disponíveis');
    }
    return await response.json();
  } catch (error) {
    console.error("Erro em getAvailableSpots:", error);
    throw error;
  }
};

export const getVehicleHistory = async (placa) => {
  const token = getToken();
  try {
    const response = await fetch(`${API_BASE_URL}/api/relatorios/historico/${placa}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
    if (!response.ok) {
      if (response.status === 404) {
        throw new Error('Nenhum histórico encontrado para esta placa');
      }
      const errorData = await response.json().catch(() => ({ message: 'Erro ao buscar histórico do veículo' }));
      throw new Error(errorData.message || 'Erro ao buscar histórico do veículo');
    }
    return await response.json();
  } catch (error) {
    console.error("Erro em getVehicleHistory:", error);
    throw error;
  }
};

export const getOccupiedSpots = async () => {
  const token = getToken();
  try {
    const response = await fetch(`${API_BASE_URL}/api/relatorios/vagas-ocupadas`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({ message: 'Erro ao buscar vagas ocupadas' }));
      throw new Error(errorData.message || 'Erro ao buscar vagas ocupadas');
    }
    return await response.json();
  } catch (error) {
    console.error("Erro em getOccupiedSpots:", error);
    throw error;
  }
};

export const getEstatisticasTempoReal = async () => {
  const token = getToken();
  try {
    const response = await fetch(`${API_BASE_URL}/api/relatorios/estatisticas`, {
      headers: { 'Authorization': `Bearer ${token}` },
    });
    if (!response.ok) throw new Error('Erro ao buscar estatísticas em tempo real');
    return await response.json();
  } catch (error) {
    console.error("Erro em getEstatisticasTempoReal:", error);
    throw error;
  }
};

export const getEstatisticasSemanais = async () => {
  const token = getToken();
  try {
    const response = await fetch(`${API_BASE_URL}/api/relatorios/estatisticas/semanal`, {
      headers: { 'Authorization': `Bearer ${token}` },
    });
    if (!response.ok) throw new Error('Erro ao buscar estatísticas semanais');
    return await response.json();
  } catch (error) {
    console.error("Erro em getEstatisticasSemanais:", error);
    throw error;
  }
};
