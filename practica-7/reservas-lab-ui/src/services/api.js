// URL base de la API de AWS Lambda
// Nota: Deberás reemplazar esta URL con la URL de tu API Gateway de AWS
const API_BASE_URL = 'https://u9kti59cqb.execute-api.us-east-1.amazonaws.com/default/function';

/**
 * Función para obtener las reservas activas (fechas no expiradas)
 * @returns {Promise<Array>} Lista de reservas activas
 */
export const fetchActiveReservations = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}`);

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const data = await response.json();

        // Mapea los datos para que coincidan con los nombres de campo esperados en la UI
        return data.map(item => ({
            id: item.id,
            name: item.nombre,
            email: item.correo,
            lab: item.lab,
            datetime: item.fecha,
            reservationId: item.reservationId
        }));
    } catch (error) {
        console.error('Error fetching active reservations:', error);
        throw error;
    }
};

/**
 * Función para obtener las reservas pasadas dentro de un rango de fechas
 * @param {string} startDate - Fecha de inicio en formato YYYY-MM-DD
 * @param {string} endDate - Fecha fin en formato YYYY-MM-DD
 * @returns {Promise<Array>} Lista de reservas pasadas
 */
export const fetchPastReservations = async (startDate, endDate) => {
    try {
        // Convertir fechas al formato esperado por tu backend
        const beginDate = `${startDate}T00:00:00`;
        const endDateTime = `${endDate}T23:59:59`;

        const response = await fetch(
            `${API_BASE_URL}?beginDate=${beginDate}&endDate=${endDateTime}`
        );

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const data = await response.json();

        // Mapea los datos para que coincidan con los nombres de campo esperados en la UI
        return data.map(item => ({
            id: item.id,
            name: item.nombre,
            email: item.correo,
            lab: item.lab,
            datetime: item.fecha,
            reservationId: item.reservationId
        }));
    } catch (error) {
        console.error('Error fetching past reservations:', error);
        throw error;
    }
};

/**
 * Función para crear una nueva reserva
 * @param {Object} reservation - Datos de la reserva
 * @returns {Promise<Object>} Resultado de la operación
 */
export const createReservation = async (reservation) => {
    try {
        // Adaptar el objeto para que coincida con lo esperado por tu backend
        const reservationData = {
            id: reservation.id,
            nombre: reservation.name,
            correo: reservation.email,
            lab: reservation.lab,
            fecha: reservation.datetime,
        };

        const response = await fetch(`${API_BASE_URL}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(reservationData),
        });

        if (!response.ok) {
            // Intentar obtener detalles del error
            const errorData = await response.json().catch(() => null);
            throw new Error(
                errorData?.error || `Error HTTP: ${response.status}`
            );
        }

        return await response.json();
    } catch (error) {
        console.error('Error creating reservation:', error);
        throw error;
    }
};

/**
 * Función para verificar la disponibilidad de horarios
 * Nota: Esta función tendría que ser implementada en tu backend
 * O adaptarse para usar la consulta actual
 * @param {string} date - Fecha en formato YYYY-MM-DD
 * @param {string} lab - Nombre del laboratorio
 * @returns {Promise<Object>} Objeto con las horas como claves y la cantidad de reservas como valores
 */
export const checkAvailability = async (date, lab) => {
    try {
        // Obtenemos todas las reservas para el laboratorio y fecha
        const beginDate = `${date}T00:00:00`;
        const endDate = `${date}T23:59:59`;

        const response = await fetch(
            `${API_BASE_URL}?beginDate=${beginDate}&endDate=${endDate}`
        );

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const reservations = await response.json();

        // Filtramos las reservas para el laboratorio específico
        const labReservations = reservations.filter(r => r.lab === lab);

        // Creamos un objeto para mantener el conteo de reservas por hora
        const hourlyCount = {};

        // Inicializamos las horas disponibles (8am a 10pm)
        for (let hour = 8; hour <= 22; hour++) {
            const hourStr = `${hour.toString().padStart(2, '0')}:00`;
            hourlyCount[hourStr] = 0;
        }

        // Contamos las reservas por hora
        labReservations.forEach(reservation => {
            const fecha = reservation.fecha;
            const hour = new Date(fecha).getHours();
            const hourStr = `${hour.toString().padStart(2, '0')}:00`;

            if (hour >= 8 && hour <= 22) {
                hourlyCount[hourStr]++;
            }
        });

        return hourlyCount;
    } catch (error) {
        console.error('Error checking availability:', error);
        throw error;
    }
};