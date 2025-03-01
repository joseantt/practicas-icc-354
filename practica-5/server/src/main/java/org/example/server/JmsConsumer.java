package org.example.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JmsConsumer {

    private final SensorDataRepository sensorDataRepository;

    @JmsListener(destination = "sensores.topic", containerFactory = "jmsListenerContainerFactory")
    public void receiveMessage(Mensaje mensaje) {
        try {
            log.info("Mensaje recibido: {}", mensaje);

            // Convertir Mensaje a SensorData y guardar en base de datos
            SensorData sensorData = new SensorData();
            sensorData.setIdDispositivo(mensaje.getIdDispositivo());
            sensorData.setFechaGeneracion(mensaje.getFechaGeneracion());
            sensorData.setTemperatura(mensaje.getTemperatura());
            sensorData.setHumedad(mensaje.getHumedad());

            sensorDataRepository.save(sensorData);
            log.info("Datos del sensor guardados: {}", sensorData);

        } catch (Exception e) {
            log.error("Error al procesar el mensaje", e);
        }
    }
}