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
