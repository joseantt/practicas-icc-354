import React from 'react';

const Header = ({ onAddReservation, onShowPastReservations }) => {
    return (
        <header className="header">
            <div className="header-title">
                <img src="/logo-pucmm.png" alt="Logo PUCMM" height="40" />
                <h1>Reservas de Laboratorio - EICT</h1>
            </div>
            <div className="header-actions">
                <button
                    className="btn btn-primary"
                    onClick={onAddReservation}
                >
                    Agregar Reserva
                </button>
                <button
                    className="btn btn-secondary"
                    onClick={onShowPastReservations}
                >
                    Registros Pasados
                </button>
            </div>
        </header>
    );
};

export default Header;