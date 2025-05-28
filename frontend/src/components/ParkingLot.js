import React, { useState, useEffect } from 'react';
import ParkingSpot from './ParkingSpot';
import ParkingSummary from './ParkingSummary';
import { fetchSpotsFromAPI, occupySpotInAPI, vacateSpotInAPI } from '../services/api';
import VehicleEntryForm from './VehicleEntryForm';

function ParkingLot({ }) {
  const [spots, setSpots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  const [showVehicleForm, setShowVehicleForm] = useState(false);
  const [selectedSpotId, setSelectedSpotId] = useState(null);

    useEffect(() => {
    const loadSpots = async () => {
        try {
        setLoading(true);
        setError(null);
        const apiSpots = await fetchSpotsFromAPI(); 
        setSpots(apiSpots); 
        } catch (err) {
        } finally {
        setLoading(false);
        }
    };
    loadSpots();
    }, []);

  const handleSpotClick = async (spotId) => {
    const spotToUpdate = spots.find(spot => spot.id === spotId);
    if (!spotToUpdate) return;

    if (spotToUpdate.isOccupied) {
      const confirmVacate = window.confirm(`Desocupar ${spotToUpdate.id} (Placa: ${spotToUpdate.vehiclePlate})?`);
      if (confirmVacate) {
        if (!spotToUpdate.backendPlateId && !spotToUpdate.vehiclePlate) {
            alert("Erro: Não foi possível identificar a placa do veículo para desocupação.");
            return;
        }
        try {
          const plateToVacate = spotToUpdate.backendPlateId || spotToUpdate.vehiclePlate;
          const apiResponse = await vacateSpotInAPI(plateToVacate); 
          setSpots(currentSpots =>
            currentSpots.map(s => (s.id === spotId ? { ...s, isOccupied: false, vehiclePlate: null, entryTime: null, backendPlateId: null } : s))
          );
          console.log(`Vaga ${spotId} desocupada. Detalhes:`, apiResponse);
        } catch (err) {
          console.error("Erro ao desocupar vaga:", err);
          alert(`Erro: ${err.message || "Não foi possível desocupar a vaga."}`);
        }
      }
    } else {
      setSelectedSpotId(spotId);
      setShowVehicleForm(true);
    }
  };

  const handleVehicleEntrySubmit = async (vehicleData) => {
    if (!selectedSpotId) return;
    try {
       const updatedSpotDataFromAPI = await occupySpotInAPI(selectedSpotId, vehicleData);
      
      setSpots(currentSpots =>
        currentSpots.map(s => (s.id === selectedSpotId ? { ...s, ...updatedSpotDataFromAPI } : s))
    );
      setShowVehicleForm(false);
      setSelectedSpotId(null);
    } catch (err) {
      console.error("Erro ao registrar entrada do veículo:", err);
      alert(`Erro ao registrar entrada: ${err.message || "Falha ao comunicar com o servidor."}`);
    }
  };

  const handleCloseVehicleForm = () => {
    setShowVehicleForm(false);
    setSelectedSpotId(null);
  };

  const occupiedSpotsCount = spots.filter(spot => spot.isOccupied).length;
  const totalSpotsCount = spots.length;

  if (loading) return <p>Carregando vagas...</p>;
  if (error) return <p style={{ color: 'red' }}>{error}</p>;

  return (
    <div>
      <ParkingSummary totalSpots={totalSpotsCount} occupiedSpots={occupiedSpotsCount} />
      <div className="parking-lot-container">
        {spots.map(spot => (
          <ParkingSpot
            key={spot.id}
            spotData={spot}
            onSpotClick={() => handleSpotClick(spot.id)} 
          />
        ))}
      </div>
      {showVehicleForm && selectedSpotId && (
        <VehicleEntryForm
          spotId={selectedSpotId}
          onSubmit={handleVehicleEntrySubmit}
          onCancel={handleCloseVehicleForm}
        />
      )}
    </div>
  );
}

export default ParkingLot;
