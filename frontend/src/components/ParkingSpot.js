import React from 'react';

function ParkingSpot({ spotData, onSpotClick }) {
  const { id, isOccupied, vehiclePlate, tipoVeiculo } = spotData; 

  const renderVehicleIcon = () => {
    if (!isOccupied) return null;

    if (tipoVeiculo && tipoVeiculo.toUpperCase() === 'MOTO') {
      return <div className="spot-vehicle-icon" role="img" aria-label="moto">🏍️</div>;
    }
    return <div className="spot-vehicle-icon" role="img" aria-label="carro">🚗</div>;
  };

  return (
    <div
      className={`parking-spot ${isOccupied ? 'occupied' : 'vacant'}`}
      onClick={() => onSpotClick(id)}
      title={isOccupied ? `Tipo: ${tipoVeiculo || 'N/D'}, Placa: ${vehiclePlate || 'Ocupado'}` : 'Vaga Livre'}
    >
      <div className="spot-id">{id}</div>
      {isOccupied && (
        <>
          {renderVehicleIcon()}
          {vehiclePlate && (
            <div className="spot-plate">{vehiclePlate}</div>
          )}
        </>
      )}
      {!isOccupied && (
        <div style={{ flexGrow: 1 }}></div>
      )}
    </div>
  );
}

export default ParkingSpot;
