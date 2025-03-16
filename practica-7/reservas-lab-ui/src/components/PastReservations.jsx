import React, { useState } from 'react';
import { fetchPastReservations } from '../services/api';

const PastReservations = ({ onClose }) => {
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [searched, setSearched] = useState(false);

    const handleStartDateChange = (e) => {
        setStartDate(e.target.value);
    };

    const handleEndDateChange = (e) => {
        setEndDate(e.target.value);
    };

    const handleSearch = async () => {
        if (!startDate || !endDate) {
            setError('Ambas fechas son requeridas');
            return;
        }

        if (new Date(startDate) > new Date(endDate)) {
            setError('La fecha de inicio debe ser anterior a la fecha final');
            return;
        }

        try {
            setLoading(true);
            setError(null);

            const data = await fetchPastReservations(startDate, endDate);
            setReservations(data);
            setSearched(true);
        } catch (err) {
            console.error('Error fetching past reservations:', err);
            setError('No se pudieron cargar los registros pasados. Por favor, intente nuevamente.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="modal">
            <div className="modal-content" style={{ width: '80%', maxWidth: '900px' }}>
                <div className="modal-header">
                    <h2 className="modal-title">Registros Pasados</h2>
                    <button className="modal-close" onClick={onClose}>&times;</button>
                </div>

                {error && <div className="error-message">{error}</div>}

                <div className="date-filter">
                    <div className="form-group">
                        <label htmlFor="startDate">Fecha Inicial</label>
                        <input
                            type="date"
                            id="startDate"
                            className="form-control"
                            value={startDate}
                            onChange={handleStartDateChange}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="endDate">Fecha Final</label>
                        <input
                            type="date"
                            id="endDate"
                            className="form-control"
                            value={endDate}
                            onChange={handleEndDateChange}
                        />
                    </div>

                    <button
                        className="btn btn-primary"
                        onClick={handleSearch}
                        disabled={loading}
                    >
                        {loading ? 'Buscando...' : 'Buscar'}
                    </button>
                </div>

                {loading ? (
                    <div className="loading">Cargando registros...</div>
                ) : searched ? (
                    reservations.length > 0 ? (
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
                                {reservations.map((reservation) => (
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
                    ) : (
                        <div className="empty-state">
                            <div className="empty-state-icon">📅</div>
                            <h3>No se encontraron registros</h3>
                            <p>No hay reservas para el período seleccionado.</p>
                        </div>
                    )
                ) : (
                    <div className="empty-state">
                        <div className="empty-state-icon">🔍</div>
                        <h3>Seleccione un rango de fechas</h3>
                        <p>Utilice los campos de fecha para buscar registros pasados.</p>
                    </div>
                )}

                <div className="form-actions" style={{ marginTop: '20px' }}>
                    <button
                        type="button"
                        className="btn btn-secondary"
                        onClick={onClose}
                    >
                        Cerrar
                    </button>
                </div>
            </div>
        </div>
    );
};

export default PastReservations;