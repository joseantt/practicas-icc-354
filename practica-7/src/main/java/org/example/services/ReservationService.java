package org.example.services;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.example.entities.Reservation;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class ReservationService {
    static final int MAX_RESERVATIONS = 7;
    static final int START_HOUR = 8;
    static final int END_HOUR = 21;
    static final String TABLE_NAME = "RESERVATION_TABLE";

    private ReservationService() {}

    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.create();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static boolean isLabAvailable(String lab, LocalDateTime date, LambdaLogger logger) {
        if(lab == null || date == null) {
            return false;
        }
        if(date.getHour() < START_HOUR || date.getHour() > END_HOUR) {
            return false;
        }

        try {
            HashMap<String, AttributeValue> expressionValues = new HashMap<>();
            expressionValues.put(":lab", AttributeValue.builder().s(lab).build());
            expressionValues.put(":fecha", AttributeValue.builder()
                    .s(date.format(formatter))
                    .build());

            var scanRequest = ScanRequest.builder()
                    .tableName(TABLE_NAME)
                    .filterExpression("lab = :lab AND begins_with(fecha, :fecha)")
                    .expressionAttributeValues(expressionValues)
                    .build();

            var response = dynamoDbClient.scan(scanRequest);

            logger.log("Reservations found: " + response.count());
            return response.count() < MAX_RESERVATIONS;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean createReservation(Reservation reservation, LambdaLogger logger) throws RuntimeException {
        if(reservation == null) {
            return false;
        }
        if(!isLabAvailable(reservation.getLab(), reservation.getFecha(), logger)) {
            return false;
        }

        try {
            var itemValues = new HashMap<String, AttributeValue>();
            itemValues.put("reservationId", AttributeValue.builder().s(reservation.getReservationId()).build());
            itemValues.put("lab", AttributeValue.builder().s(reservation.getLab()).build());
            itemValues.put("fecha", AttributeValue.builder().s(reservation.getFecha().format(formatter)).build());
            itemValues.put("nombre", AttributeValue.builder().s(reservation.getNombre()).build());
            itemValues.put("id", AttributeValue.builder().s(reservation.getId()).build());
            itemValues.put("correo", AttributeValue.builder().s(reservation.getCorreo()).build());

            dynamoDbClient.putItem(builder -> builder
                    .tableName(TABLE_NAME)
                    .item(itemValues));

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Reservation> getActiveReservations() throws RuntimeException {
        return getReservationsByDateRange(LocalDateTime.now(), null);
    }

    public static List<Reservation> getReservationsByDateRange(LocalDateTime beginDate, LocalDateTime endDate)
            throws RuntimeException {
        try {
            var scanRequestBuilder = ScanRequest.builder().tableName(TABLE_NAME);
            HashMap<String, AttributeValue> expressionValues = new HashMap<>();
            StringBuilder filterExpression = new StringBuilder();

            if (beginDate != null) {
                filterExpression.append("fecha >= :beginDate");
                expressionValues.put(":beginDate", AttributeValue.builder()
                        .s(beginDate.format(formatter))
                        .build());
            }

            if (endDate != null) {
                if (!filterExpression.isEmpty()) {
                    filterExpression.append(" AND ");
                }
                filterExpression.append("fecha <= :endDate");
                expressionValues.put(":endDate", AttributeValue.builder()
                        .s(endDate.format(formatter))
                        .build());
            }

            if (!filterExpression.isEmpty()) {
                scanRequestBuilder.filterExpression(filterExpression.toString())
                        .expressionAttributeValues(expressionValues);
            }

            var scanResponse = dynamoDbClient.scan(scanRequestBuilder.build());

            return scanResponse.items().stream()
                    .map(item -> new Reservation(
                            item.get("reservationId").s(),
                            item.get("correo").s(),
                            item.get("nombre").s(),
                            item.get("id").s(),
                            item.get("lab").s(),
                            LocalDateTime.parse(item.get("fecha").s())
                    )).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
