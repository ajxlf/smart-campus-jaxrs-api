package jax.rs_coursework;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public Map<String, Object> getDiscovery() {
        Map<String, Object> response = new HashMap<>();

        response.put("apiName", "Smart Campus Sensor Room Management API");
        response.put("version", "v1");
        response.put("adminContact", "w1772798@my.westminster.ac.uk");

        Map<String, String> resources = new HashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");

        response.put("resources", resources);

        return response;
    }
}