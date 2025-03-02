package org.example.server.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "sensores_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorData {

    public SensorData(MensajeDTO mensaje) {
        this.idDispositivo = mensaje.getIdDispositivo();
        this.fechaGeneracion = mensaje.getFechaGeneracion();
        this.temperatura = mensaje.getTemperatura();
        this.humedad = mensaje.getHumedad();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_dispositivo")
    private Integer idDispositivo;

    @Column(name = "fecha_generacion")
    private String fechaGeneracion;

    @Column(name = "temperatura")
    private Double temperatura;

    @Column(name = "humedad")
    private Double humedad;
}