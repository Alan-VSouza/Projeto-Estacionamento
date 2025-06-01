import { useState, useEffect } from 'react';
import { toast } from 'react-toastify'; 
import ParkingSpot from './ParkingSpot';
import ParkingSummary from './ParkingSummary';
import { fetchSpotsFromAPI, occupySpotInAPI, vacateSpotInAPI, cancelEntryInAPI } from '../../services/api/Api';
import VehicleEntryForm from '../forms/VehicleEntryForm';
import ActionModal from '../ActionModal';

function ParkingLot({ }) {
  const [spots, setSpots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  const [showVehicleForm, setShowVehicleForm] = useState(false);
  const [selectedSpotId, setSelectedSpotId] = useState(null);

  const [showActionModal, setShowActionModal] = useState(false);
  const [selectedSpotData, setSelectedSpotData] = useState(null);

  useEffect(() => {
    const loadSpots = async () => {
      try {
        setLoading(true);
        setError(null);
        const apiSpots = await fetchSpotsFromAPI(); 
        setSpots(apiSpots); 
      } catch (err) {
        setError(err.message);
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
      setSelectedSpotData(spotToUpdate);
      setShowActionModal(true);
    } else {
      setSelectedSpotId(spotId);
      setShowVehicleForm(true);
    }
  };

  const handleVacateSpot = async () => {
    if (!selectedSpotData) return;
    
    try {
      const plateToVacate = selectedSpotData.backendPlateId || selectedSpotData.vehiclePlate;
      const apiResponse = await vacateSpotInAPI(plateToVacate);
      
      setSpots(currentSpots =>
        currentSpots.map(s => 
          s.id === selectedSpotData.id 
            ? { ...s, isOccupied: false, vehiclePlate: null, entryTime: null, backendPlateId: null } 
            : s
        )
      );
      
      console.log(`Vaga ${selectedSpotData.id} desocupada. Detalhes:`, apiResponse);

      toast.success(`✅ Vaga ${selectedSpotData.id} desocupada com sucesso!`, {
        position: "top-right",
        autoClose: 3000,
      });
    } catch (err) {
      console.error("Erro ao desocupar vaga:", err);

      toast.error(`❌ Erro: ${err.message || "Não foi possível desocupar a vaga."}`, {
        position: "top-right",
        autoClose: 4000,
      });
    }
  };

  const handleCancelEntry = async () => {
    if (!selectedSpotData) return;
    
    try {
      const plateToCancel = selectedSpotData.backendPlateId || selectedSpotData.vehiclePlate;
      await cancelEntryInAPI(plateToCancel);
      
      setSpots(currentSpots =>
        currentSpots.map(s => 
          s.id === selectedSpotData.id 
            ? { ...s, isOccupied: false, vehiclePlate: null, entryTime: null, backendPlateId: null } 
            : s
        )
      );

      toast.success(`✅ Entrada da vaga ${selectedSpotData.id} cancelada com sucesso!`, {
        position: "top-right",
        autoClose: 3000,
      });
    } catch (err) {
      console.error("Erro ao cancelar entrada:", err);

      toast.error(`❌ Erro: ${err.message || "Não foi possível cancelar a entrada."}`, {
        position: "top-right",
        autoClose: 4000,
      });
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

      toast.error(`❌ Erro ao registrar entrada: ${err.message || "Falha ao comunicar com o servidor."}`, {
        position: "top-right",
        autoClose: 4000,
      });
    }
  };

  const handleCloseVehicleForm = () => {
    setShowVehicleForm(false);
    setSelectedSpotId(null);
  };

  const handleCloseActionModal = () => {
    setShowActionModal(false);
    setSelectedSpotData(null);
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

      <ActionModal
        isOpen={showActionModal}
        onClose={handleCloseActionModal}
        spotData={selectedSpotData}
        onVacate={handleVacateSpot}
        onCancel={handleCancelEntry}
      />
    </div>
  );
}

export default ParkingLot;