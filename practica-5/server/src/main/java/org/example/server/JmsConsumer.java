package org.example.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Mensaje;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class JmsConsumer {

    private final ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Transactional
    @JmsListener(destination = "sensores.topic", containerFactory = "jmsListenerContainerFactory")
    public void receiveMessage(String jsonMessage) {
        try {
            log.info("Mensaje recibido: {}", jsonMessage);

            // Deserializar JSON a objeto Mensaje
            Mensaje mensaje = objectMapper.readValue(jsonMessage, Mensaje.class);

            // Guardar mensaje en la base de datos
            SensorData sensorData = new SensorData();
            sensorData.setIdDispositivo(mensaje.getIdDispositivo());
            sensorData.setFechaGeneracion(mensaje.getFechaGeneracion());
            sensorData.setTemperatura(mensaje.getTemperatura());
            sensorData.setHumedad(mensaje.getHumedad());

            entityManager.persist(sensorData);
            log.info("Datos del sensor guardados: {}", sensorData);

        } catch (Exception e) {
            log.error("Error al procesar el mensaje", e);
        }
    }
}