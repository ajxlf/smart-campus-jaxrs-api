package jax.rs_coursework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReadings() {
        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Sensor not found");
            errorResponse.put("message", "No sensor exists with ID: " + sensorId);
            errorResponse.put("status", 404);

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse)
                    .build();
        }

        List<SensorReading> sensorReadings = DataStore.readings.get(sensorId);

        if (sensorReadings == null) {
            sensorReadings = new ArrayList<>();
        }

        return Response.ok(sensorReadings).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Sensor not found");
            errorResponse.put("message", "Cannot add reading because sensor '" + sensorId + "' does not exist.");
            errorResponse.put("status", 404);

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse)
                    .build();
        }
        
        if (sensor.getStatus() != null && sensor.getStatus().equalsIgnoreCase("MAINTENANCE")) {
            throw new SensorUnavailableException(
                    "Sensor '" + sensorId + "' is currently in MAINTENANCE mode and cannot accept new readings."
            );
        }

        List<SensorReading> sensorReadings = DataStore.readings.get(sensorId);

        if (sensorReadings == null) {
            sensorReadings = new ArrayList<>();
            DataStore.readings.put(sensorId, sensorReadings);
        }

        sensorReadings.add(reading);

        sensor.setCurrentValue(reading.getValue());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Reading added successfully");
        response.put("sensorId", sensorId);
        response.put("reading", reading);
        response.put("updatedCurrentValue", sensor.getCurrentValue());

        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }
}