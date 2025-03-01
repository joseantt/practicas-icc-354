package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Random;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String BROKER_URL = System.getenv("BROKER_URL") != null ?
            System.getenv("BROKER_URL") :
            "tcp://localhost:61616";
    private static final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
    private static Session session = null;
    private static MessageProducer productor = null;
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final int idDispositivo = System.getenv("DEVICE_ID") != null ?
            Integer.parseInt(System.getenv("DEVICE_ID")) :
            new Random().nextInt(1000000);

    public static void main(String[] args) {
        try{
            Connection connection = factory.createConnection("admin", "admin");
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("sensores.topic");
            productor = session.createProducer(topic);
            logger.info("Conexión establecida con el broker ActiveMQ");
        } catch (Exception e) {
            logger.error("Error al crear la conexión", e);
            System.exit(1);
        }

        while(true){
            try{
                TextMessage textMessage = prepareTextMessage(sensorEmiteMensaje());
                productor.send(textMessage);
                logger.info("Mensaje enviado -> {}", textMessage.getText());
                Thread.sleep(60000);
            } catch (Exception e) {
                logger.error("Error al enviar el mensaje", e);
            }
        }
    }

    private static Mensaje sensorEmiteMensaje(){
        Random random = new Random();

        double mediaTemperatura = 27.5;
        double desviacionEstandarTemperatura = 5.0;
        double temperatura = mediaTemperatura + desviacionEstandarTemperatura * random.nextGaussian();

        double mediaHumedad = 50.0;
        double desviacionEstandarHumedad = 15.0;
        double humedad = mediaHumedad + desviacionEstandarHumedad * random.nextGaussian();

        return new Mensaje(temperatura, humedad, idDispositivo);
    }

    private static TextMessage prepareTextMessage(Mensaje mensaje) throws Exception {
        String jsonMessage = objectMapper.writeValueAsString(mensaje);
        TextMessage textMessage = session.createTextMessage(jsonMessage);
        textMessage.setStringProperty("_type", "Mensaje");

        return textMessage;
    }
}