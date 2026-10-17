package jax.rs_coursework;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import javax.ws.rs.core.Response;
import static org.junit.Assert.*;

public class SensorReadingResourceTest {

    private Sensor sensor;
    private SensorReadingResource resource;

    @Before
    public void resetStore() {
        DataStore.rooms.clear();
        DataStore.sensors.clear();
        DataStore.readings.clear();

        DataStore.rooms.put("LAB-101", new Room("LAB-101", "Laboratory", 20, new ArrayList<String>()));
        sensor = new Sensor("TEMP-1", "Temperature", "ACTIVE", 20.0, "LAB-101");
        DataStore.sensors.put(sensor.getId(), sensor);
        DataStore.readings.put(sensor.getId(), new ArrayList<SensorReading>());
        resource = new SensorReadingResource(sensor.getId());
    }

    @Test
    public void addingReadingUpdatesSensorValueAndHistory() {
        SensorReading reading = new SensorReading("READ-1", 1712870000000L, 23.8);

        Response response = resource.addReading(reading);
        try {
            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            assertEquals(23.8, sensor.getCurrentValue(), 0.0001);
            assertEquals(1, DataStore.readings.get(sensor.getId()).size());
            assertSame(reading, DataStore.readings.get(sensor.getId()).get(0));
        } finally {
            response.close();
        }
    }

    @Test(expected = SensorUnavailableException.class)
    public void rejectsReadingsWhenSensorIsInMaintenance() {
        sensor.setStatus("MAINTENANCE");
        resource.addReading(new SensorReading("READ-2", 1712870000000L, 24.0));
    }

    @Test
    public void returnsNotFoundWhenReadingSensorDoesNotExist() {
        Response response = new SensorReadingResource("MISSING").getAllReadings();
        try {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
            assertTrue(response.getEntity() instanceof Map);
        } finally {
            response.close();
        }
    }
}
