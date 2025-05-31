// src/components/parking/ParkingSummary.js
import React from 'react';

function ParkingSummary({ totalSpots, occupiedSpots }) {
  const availableSpots = totalSpots - occupiedSpots;
  const occupancyRate = totalSpots > 0 ? ((occupiedSpots / totalSpots) * 100).toFixed(1) : 0;

  return (
    <div className="parking-summary">
      <h2>📊 Status do Estacionamento</h2>
      <p>
        <span>Total de Vagas:</span>
        <span className="summary-value">{totalSpots}</span>
      </p>
      <p>
        <span>Vagas Ocupadas:</span>
        <span className="summary-value">{occupiedSpots}</span>
      </p>
      <p>
        <span>Vagas Disponíveis:</span>
        <span className="summary-value">{availableSpots}</span>
      </p>
      <p>
        <span>Taxa de Ocupação:</span>
        <span className="summary-value">{occupancyRate}%</span>
      </p>
    </div>
  );
}

export default ParkingSummary;
