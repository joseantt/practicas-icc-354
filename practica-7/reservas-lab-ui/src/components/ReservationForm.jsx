import React, { useState } from 'react';
import { createReservation, checkAvailability } from '../services/api';

const ReservationForm = ({ onClose, currentReservations }) => {
    const [formData, setFormData] = useState({
        id: '',
        name: '',
        email: '',
        lab: '',
        date: '',
        time: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [timeAvailability, setTimeAvailability] = useState({});

    const labs = ['Redes', 'Computación', 'Comunicaciones', 'Electrónica'];

    // Generar horas disponibles (8am a 10pm en múltiplos de hora)
    const availableHours = [];
    for (let i = 8; i <= 22; i++) {
        availableHours.push(`${i.toString().padStart(2, '0')}:00`);
    }

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));

        // Verificar disponibilidad de horarios al cambiar fecha o laboratorio
        if (name === 'date' || name === 'lab') {
            if (formData.date && formData.lab) {
                checkTimeAvailability(formData.date, formData.lab);
            }
        }
    };

    const checkTimeAvailability = async (date, lab) => {
        try {
            setLoading(true);
            const availability = await checkAvailability(date, lab);
            setTimeAvailability(availability);
        } catch (err) {
            console.error('Error checking availability:', err);
            setError('No se pudo verificar la disponibilidad. Por favor, intente nuevamente.');
        } finally {
            setLoading(false);
        }
    };

    const validateForm = () => {
        if (!formData.id.trim()) return 'El ID es obligatorio';
        if (!formData.name.trim()) return 'El nombre es obligatorio';
        if (!formData.email.trim()) return 'El correo es obligatorio';
        if (!formData.email.includes('@')) return 'El correo debe ser válido';
        if (!formData.lab) return 'Debe seleccionar un laboratorio';
        if (!formData.date) return 'Debe seleccionar una fecha';
        if (!formData.time) return 'Debe seleccionar una hora';

        // Verificar si la hora seleccionada está disponible
        if (timeAvailability[formData.time] >= 7) {
            return 'Este horario ya está completo (máximo 7 personas)';
        }

        return null;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const validationError = validateForm();
        if (validationError) {
            setError(validationError);
            return;
        }

        try {
            setLoading(true);
            setError(null);

            // Formatear la fecha y hora para enviarla a la API
            const fecha = new Date(`${formData.date}T${formData.time}`).toISOString().split('.')[0];

            await createReservation({
                id: formData.id,
                name: formData.name,
                email: formData.email,
                lab: formData.lab,
                datetime: fecha
            });

            onClose();
        } catch (err) {
            console.error('Error creating reservation:', err);
            setError('No se pudo crear la reserva. Por favor, intente nuevamente.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="modal">
            <div className="modal-content">
                <div className="modal-header">
                    <h2 className="modal-title">Registro Reserva</h2>
                    <button className="modal-close" onClick={onClose}>&times;</button>
                </div>

                {error && <div className="error-message">{error}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="id">ID</label>
                        <input
                            type="text"
                            id="id"
                            name="id"
                            className="form-control"
                            value={formData.id}
                            onChange={handleChange}
                            placeholder="Ingrese su ID estudiantil"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="name">Nombre</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            className="form-control"
                            value={formData.name}
                            onChange={handleChange}
                            placeholder="Ingrese su nombre completo"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="email">Correo</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            className="form-control"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="Ingrese su correo electrónico"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="lab">Laboratorio</label>
                        <select
                            id="lab"
                            name="lab"
                            className="form-control"
                            value={formData.lab}
                            onChange={handleChange}
                        >
                            <option value="">Seleccionar Laboratorio</option>
                            {labs.map(lab => (
                                <option key={lab} value={lab}>{lab}</option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label htmlFor="date">Fecha Reserva</label>
                        <input
                            type="date"
                            id="date"
                            name="date"
                            className="form-control"
                            value={formData.date}
                            onChange={handleChange}
                            min={new Date().toISOString().split('T')[0]}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="time">Hora</label>
                        <select
                            id="time"
                            name="time"
                            className="form-control"
                            value={formData.time}
                            onChange={handleChange}
                            disabled={!formData.date || !formData.lab}
                        >
                            <option value="">Seleccionar Hora</option>
                            {availableHours.map(hour => {
                                const count = timeAvailability[hour] || 0;
                                const isAvailable = count < 7;
                                return (
                                    <option key={hour} value={hour} disabled={!isAvailable}>
                                        {hour} {isAvailable ? `(${count}/7 reservas)` : '(Lleno)'}
                                    </option>
                                );
                            })}
                        </select>
                    </div>

                    <div className="form-actions">
                        <button
                            type="button"
                            className="btn btn-secondary"
                            onClick={onClose}
                            disabled={loading}
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            className="btn btn-primary"
                            disabled={loading}
                        >
                            {loading ? 'Guardando...' : 'Guardar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ReservationForm;