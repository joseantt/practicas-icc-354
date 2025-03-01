package org.example.server;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.time.LocalDateTime;

public class Mensaje {
    @JsonProperty("idDispositivo")
    private int idDispositivo;

    @JsonProperty("fechaGeneracion")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime fechaGeneracion;

    @JsonProperty("temperatura")
    private Double temperatura;

    @JsonProperty("humedad")
    private Double humedad;

    public Mensaje() {
    }

    public Mensaje(Double temperatura, Double humedad, int idDispositivo) {
        this.fechaGeneracion = LocalDateTime.now();
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.idDispositivo = idDispositivo;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public Double getTemperatura() {
        return temperatura;
    }
    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Double getHumedad() {
        return humedad;
    }
    public void setHumedad(Double humedad) {
        this.humedad = humedad;
    }

    public int getIdDispositivo() {
        return idDispositivo;
    }
    public void setIdDispositivo(int idDispositivo) {
        this.idDispositivo = idDispositivo;
    }
}