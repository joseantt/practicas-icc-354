import React, { useState, useEffect } from 'react';
import { fetchActiveReservations } from '../services/api';

const ReservationList = ({ refreshTrigger, setReservations }) => {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [activeReservations, setActiveReservations] = useState([]);

    useEffect(() => {
        const getReservations = async () => {
            try {
                setLoading(true);
                const data = await fetchActiveReservations();
                setActiveReservations(data);
                setReservations(data);
                setError(null);
            } catch (err) {
                console.error('Error fetching reservations:', err);
                setError('No se pudieron cargar las reservas. Por favor, intente nuevamente.');
            } finally {
                setLoading(false);
            }
        };

        getReservations();
    }, [refreshTrigger, setReservations]);

    if (loading) {
        return <div className="loading">Cargando reservas...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    if (activeReservations.length === 0) {
        return (
            <div className="empty-state">
                <div className="empty-state-icon">📅</div>
                <h3>No hay reservas activas</h3>
                <p>Haga clic en "Agregar Reserva" para crear una nueva reserva de laboratorio.</p>
            </div>
        );
    }

    return (
        <div className="table-container">
            <table className="table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Correo</th>
                    <th>Laboratorio</th>
                    <th>Fecha y Hora</th>
                </tr>
                </thead>
                <tbody>
                {activeReservations.map((reservation) => (
                    <tr key={reservation.reservationId || reservation.id}>
                        <td>{reservation.id}</td>
                        <td>{reservation.name}</td>
                        <td>{reservation.email}</td>
                        <td>{reservation.lab}</td>
                        <td>{new Date(reservation.datetime).toLocaleString()}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default ReservationList;