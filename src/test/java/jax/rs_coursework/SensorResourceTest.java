package jax.rs_coursework;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import javax.ws.rs.core.Response;
import static org.junit.Assert.*;

public class SensorResourceTest {

    @Before
    public void resetStore() {
        DataStore.rooms.clear();
        DataStore.sensors.clear();
        DataStore.readings.clear();
        DataStore.rooms.put("LAB-101", new Room("LAB-101", "Laboratory", 20, new ArrayList<String>()));
    }

    @Test
    public void filtersSensorsByTypeIgnoringCase() {
        DataStore.sensors.put("TEMP-1", new Sensor("TEMP-1", "Temperature", "ACTIVE", 21.0, "LAB-101"));
        DataStore.sensors.put("CO2-1", new Sensor("CO2-1", "CO2", "ACTIVE", 430.0, "LAB-101"));

        assertEquals(1, new SensorResource().getAllSensors("temperature").size());
        assertEquals(2, new SensorResource().getAllSensors(" ").size());
    }

    @Test
    public void createsSensorAndLinksItToExistingRoom() {
        Sensor sensor = new Sensor("TEMP-2", "Temperature", "ACTIVE", 20.5, "LAB-101");

        Response response = new SensorResource().createSensor(sensor);
        try {
            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            assertSame(sensor, DataStore.sensors.get("TEMP-2"));
            assertEquals(Arrays.asList("TEMP-2"), DataStore.rooms.get("LAB-101").getSensorIds());
            assertTrue(DataStore.readings.containsKey("TEMP-2"));
            assertTrue(DataStore.readings.get("TEMP-2").isEmpty());
        } finally {
            response.close();
        }
    }

    @Test(expected = LinkedResourceNotFoundException.class)
    public void rejectsSensorLinkedToMissingRoom() {
        new SensorResource().createSensor(
            new Sensor("TEMP-3", "Temperature", "ACTIVE", 20.5, "UNKNOWN")
        );
    }
}
