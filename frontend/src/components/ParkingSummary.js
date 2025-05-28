import React from 'react';

function ParkingSummary({ totalSpots, occupiedSpots }) {
  const availableSpots = totalSpots - occupiedSpots;

  return (
    <div className="parking-summary">
      <p>Total de Vagas: {totalSpots}</p>
      <p>Vagas Ocupadas: {occupiedSpots}</p>
      <p>Vagas Disponíveis: {availableSpots}</p>
    </div>
  );
}

export default ParkingSummary;
