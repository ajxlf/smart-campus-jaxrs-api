package jax.rs_coursework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("sensors")
public class SensorResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sensor> getAllSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = new ArrayList<>(DataStore.sensors.values());

        if (type == null || type.trim().isEmpty()) {
            return allSensors;
        }

        List<Sensor> filteredSensors = new ArrayList<>();

        for (Sensor sensor : allSensors) {
            if (sensor.getType() != null && sensor.getType().equalsIgnoreCase(type)) {
                filteredSensors.add(sensor);
            }
        }

        return filteredSensors;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {

        Room linkedRoom = DataStore.rooms.get(sensor.getRoomId());

        if (linkedRoom == null) {
            throw new LinkedResourceNotFoundException(
                "Cannot create sensor because roomId '" + sensor.getRoomId() + "' does not exist."
            );
        }

        DataStore.sensors.put(sensor.getId(), sensor);

        if (linkedRoom.getSensorIds() == null) {
            linkedRoom.setSensorIds(new ArrayList<>());
        }
        linkedRoom.getSensorIds().add(sensor.getId());

        DataStore.readings.put(sensor.getId(), new ArrayList<>());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Sensor created successfully");
        response.put("sensor", sensor);

        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @Path("{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}