package org.example.functions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.adapters.LocalDateTimeAdapter;
import org.example.entities.Reservation;
import org.example.services.ReservationService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class BookingFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            String httpMethod = (input.getBody() != null && !input.getBody().isEmpty()) ? "POST" : "GET";
            switch (httpMethod){
                case "POST":
                    context.getLogger().log("Creating reservation: " + input.getBody());
                    Reservation reservation = gson.fromJson(input.getBody(), Reservation.class);
                    reservation.setReservationId(UUID.randomUUID().toString());

                    if(!ReservationService.createReservation(reservation, context.getLogger())) {
                        response.setStatusCode(400);
                        response.setBody("{\"error\": \"No existen cupos disponibles.\"}");
                        return response;
                    }

                    response.setStatusCode(201);
                    response.setBody("{\"success\": true}");
                    response.setHeaders(Map.of("Content-Type", "application/json"));

                    return response;

                case "GET":
                    Map<String, String> queryParams = input.getQueryStringParameters();
                    DateRange dateRange = extractDateRange(queryParams);

                    var jsonReservations = (dateRange == null) ?
                            gson.toJson(ReservationService.getActiveReservations()) :
                            gson.toJson(ReservationService.getReservationsByDateRange(dateRange.beginDate, dateRange.endDate));
                    response.setStatusCode(200);
                    response.setBody(jsonReservations);
                    response.setHeaders(Map.of("Content-Type", "application/json"));

                    return response;

                default:
                    break;
            }

            String errorMsg = "Unsupported HTTP method: " + input.getHttpMethod();
            context.getLogger().log(errorMsg);
            response.setStatusCode(400);
            response.setBody("{\"error\": \"" + errorMsg + "\"}");

            return response;
        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());

            response.setStatusCode(500);
            response.setBody("{\"error\": \"" + e.getMessage() + "\"}");
            return response;
        }
    }

    private record DateRange(LocalDateTime beginDate, LocalDateTime endDate) {}

    private DateRange extractDateRange(Map<String, String> queryParams){
        if(queryParams == null || !queryParams.containsKey("beginDate") || !queryParams.containsKey("endDate")){
            return null;
        }

        try{
            LocalDateTime beginDate = LocalDateTime.parse(queryParams.get("beginDate"));
            LocalDateTime endDate = LocalDateTime.parse(queryParams.get("endDate"));
            return new DateRange(beginDate, endDate);
        } catch (Exception e){
            return null;
        }
    }
}
