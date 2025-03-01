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
    public void receiveMessage(MensajeDTO mensajeDTO) {
        try {
            log.info("Mensaje recibido: {}", mensajeDTO);

            SensorData sensorData = new SensorData(mensajeDTO);

            sensorDataRepository.save(sensorData);
            log.info("Datos del sensor guardados: {}", sensorData);

        } catch (Exception e) {
            log.error("Error al procesar el mensaje", e);
        }
    }
}