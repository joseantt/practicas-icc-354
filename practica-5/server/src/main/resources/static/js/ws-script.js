const WS_ENDPOINT = '/ws-sensores';
const TOPIC_SENSORES = '/topic/sensores';

class SensorWebSocket {
    constructor() {
        this.socket = new SockJS(WS_ENDPOINT);
        this.stompClient = Stomp.over(this.socket);
        this.charts = {};
        this.colores = {
            temperatura: [
                { bg: 'rgba(255, 99, 132, 0.2)', border: 'rgb(255, 99, 132)' },
                { bg: 'rgba(153, 102, 255, 0.2)', border: 'rgb(153, 102, 255)' },
                { bg: 'rgba(255, 159, 64, 0.2)', border: 'rgb(255, 159, 64)' }
            ],
            humedad: [
                { bg: 'rgba(54, 162, 235, 0.2)', border: 'rgb(54, 162, 235)' },
                { bg: 'rgba(75, 192, 192, 0.2)', border: 'rgb(75, 192, 192)' },
                { bg: 'rgba(201, 203, 207, 0.2)', border: 'rgb(201, 203, 207)' }
            ]
        };
        this.esperarChart();
    }

    esperarChart() {
        if (typeof Chart === 'undefined') {
            setTimeout(() => this.esperarChart(), 100);
            return;
        }
        this.inicializarCharts();
        this.conectar();
    }

    conectar() {
        this.stompClient.connect({}, (frame) => {
            console.log('Conectado al WebSocket');
            this.suscribirse();
        }, (error) => {
            console.error('Error de conexión:', error);
            setTimeout(() => this.conectar(), 5000);
        });
    }

    suscribirse() {
        this.stompClient.subscribe(TOPIC_SENSORES, (mensaje) => {
            const sensorData = JSON.parse(mensaje.body);
            this.actualizarDatosSensores(sensorData);
        });
    }

    formatearFecha(fechaStr) {
        const [fecha, hora] = fechaStr.split(' ');
        const [dia, mes, anio] = fecha.split('/');
        return new Date(`${anio}-${mes}-${dia}T${hora}`).toLocaleTimeString('es-ES', {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
    }

    actualizarDatosSensores(data) {
        const tiempo = this.formatearFecha(data.fechaGeneracion);
        const deviceLabel = `Dispositivo ${data.idDispositivo}`;

        let tempDataset = this.charts.temperatura.data.datasets.find(ds => ds.label.includes(deviceLabel));
        let humDataset = this.charts.humedad.data.datasets.find(ds => ds.label.includes(deviceLabel));

        if (!tempDataset) {
            tempDataset = this.crearDataset(`Temperatura ${deviceLabel}`, data.idDispositivo, 'temperatura');
            humDataset = this.crearDataset(`Humedad ${deviceLabel}`, data.idDispositivo, 'humedad');
            this.charts.temperatura.data.datasets.push(tempDataset);
            this.charts.humedad.data.datasets.push(humDataset);
        }

        if (!this.charts.temperatura.data.labels.includes(tiempo)) {
            this.charts.temperatura.data.labels.push(tiempo);
            this.charts.humedad.data.labels.push(tiempo);

            if (this.charts.temperatura.data.labels.length > 10) {
                this.charts.temperatura.data.labels.shift();
                this.charts.temperatura.data.datasets.forEach(ds => ds.data.shift());
                this.charts.humedad.data.datasets.forEach(ds => ds.data.shift());
            }
        }

        const index = this.charts.temperatura.data.labels.indexOf(tiempo);
        tempDataset.data[index] = data.temperatura;
        humDataset.data[index] = data.humedad;

        this.charts.temperatura.update('none');
        this.charts.humedad.update('none');
    }

    crearDataset(label, id, tipo) {
        if (!this.colores[tipo]) {
            console.error(`Tipo de sensor '${tipo}' no válido`);
            return {
                label: label,
                data: [],
                backgroundColor: 'rgba(201, 203, 207, 0.2)',
                borderColor: 'rgb(201, 203, 207)',
                borderWidth: 2,
                tension: 0.4,
                fill: true
            };
        }

        const colores = this.colores[tipo];
        const colorIndex = ((id - 1) % colores.length + colores.length) % colores.length;

        return {
            label: label,
            data: [],
            backgroundColor: colores[colorIndex].bg,
            borderColor: colores[colorIndex].border,
            borderWidth: 2,
            tension: 0.4,
            fill: true
        };
    }

    inicializarCharts() {
        const configuracionBase = {
            type: 'line',
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: {
                    intersect: false,
                    mode: 'index'
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            drawBorder: false
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                },
                plugins: {
                    legend: {
                        position: 'top'
                    }
                },
                animation: false
            },
            data: {
                labels: [],
                datasets: []
            }
        };

        const ctxTemp = document.getElementById('temperature-chart').getContext('2d');
        const ctxHum = document.getElementById('humidity-chart').getContext('2d');

        this.charts.temperatura = new Chart(ctxTemp, structuredClone(configuracionBase));
        this.charts.humedad = new Chart(ctxHum, structuredClone(configuracionBase));
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const scriptChart = document.createElement('script');
    scriptChart.src = 'https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js';
    scriptChart.onload = () => new SensorWebSocket();
    document.head.appendChild(scriptChart);
});