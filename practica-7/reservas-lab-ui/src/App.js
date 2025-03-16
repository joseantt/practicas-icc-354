import React, { useState } from 'react';
import Header from './components/Header';
import ReservationList from './components/ReservationList';
import ReservationForm from './components/ReservationForm';
import PastReservations from './components/PastReservations';
import './App.css';

function App() {
  const [showForm, setShowForm] = useState(false);
  const [showPastReservations, setShowPastReservations] = useState(false);
  const [reservations, setReservations] = useState([]);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const refreshReservations = () => {
    setRefreshTrigger(prev => prev + 1);
  };

  const handleAddReservation = () => {
    setShowForm(true);
    setShowPastReservations(false);
  };

  const handleShowPastReservations = () => {
    setShowPastReservations(true);
    setShowForm(false);
  };

  const handleCloseForm = () => {
    setShowForm(false);
    refreshReservations();
  };

  const handleClosePastReservations = () => {
    setShowPastReservations(false);
  };

  return (
      <div className="app-container">
        <Header
            onAddReservation={handleAddReservation}
            onShowPastReservations={handleShowPastReservations}
        />

        <main>
          {!showForm && !showPastReservations && (
              <ReservationList
                  refreshTrigger={refreshTrigger}
                  setReservations={setReservations}
              />
          )}

          {showForm && (
              <ReservationForm
                  onClose={handleCloseForm}
                  currentReservations={reservations}
              />
          )}

          {showPastReservations && (
              <PastReservations
                  onClose={handleClosePastReservations}
              />
          )}
        </main>
      </div>
  );
}

export default App;