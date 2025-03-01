package org.example.server;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MensajeDTO {
    @JsonProperty("idDispositivo")
    private int idDispositivo;

    @JsonProperty("fechaGeneracion")
    private String fechaGeneracion;

    @JsonProperty("temperatura")
    private Double temperatura;

    @JsonProperty("humedad")
    private Double humedad;

    public MensajeDTO() {
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }
    public void setFechaGeneracion(String fechaGeneracion) {
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