package jax.rs_coursework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    public static final Map<String, Room> rooms = new HashMap<>();
    public static final Map<String, Sensor> sensors = new HashMap<>();
    public static final Map<String, List<SensorReading>> readings = new HashMap<>();

    static {
        rooms.put("LIB-301", new Room("LIB-301", "Library Quiet Study", 40, new ArrayList<>()));
        rooms.put("ENG-201", new Room("ENG-201", "Engineering Lab", 25, new ArrayList<>()));
    }
}