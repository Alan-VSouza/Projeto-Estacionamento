function ActionModal({ isOpen, onClose, spotData, onVacate, onCancel }) {
  if (!isOpen) return null;

  const handleVacate = () => {
    onVacate();
    onClose();
  };

  const handleCancel = () => {
    onCancel();
    onClose();
  };

  return (
    <>
      <div className="action-modal-overlay" onClick={onClose} />
      <div className="action-modal">
        <div className="action-modal-header">
          <h3>🚗 {spotData.id}</h3>
          <button className="close-button" onClick={onClose}>✕</button>
        </div>
        
        <div className="action-modal-content">
          <div className="vehicle-info">
            <div className="vehicle-icon">
              {spotData.tipoVeiculo === 'MOTO' ? '🏍️' : '🚗'}
            </div>
            <div className="vehicle-details">
              <p><strong>Placa:</strong> {spotData.vehiclePlate}</p>
              <p><strong>Tipo:</strong> {spotData.tipoVeiculo}</p>
              {spotData.entryTime && (
                <p><strong>Entrada:</strong> {spotData.entryTime.toLocaleString('pt-BR')}</p>
              )}
            </div>
          </div>
          
          <div className="action-question">
            <p>O que deseja fazer com este veículo?</p>
          </div>
        </div>
        
        <div className="action-modal-buttons">
          <button 
            className="action-btn vacate-btn"
            onClick={handleVacate}
          >
            🚪 Registrar Saída
            <span className="btn-description">Finaliza a estadia e calcula o valor</span>
          </button>
          
          <button 
            className="action-btn cancel-btn"
            onClick={handleCancel}
          >
            ❌ Cancelar Entrada
            <span className="btn-description">Remove o registro sem cobrança</span>
          </button>
        </div>
      </div>
    </>
  );
}

export default ActionModal;
