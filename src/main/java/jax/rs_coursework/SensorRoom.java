package jax.rs_coursework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("rooms")
public class SensorRoom {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Room> getAllRooms() {
        return new ArrayList<>(DataStore.rooms.values());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> createRoom(Room room) {
        DataStore.rooms.put(room.getId(), room);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Room created successfully");
        response.put("room", room);

        return response;
    }

    @GET
    @Path("{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Room getRoomById(@PathParam("roomId") String roomId) {
        return DataStore.rooms.get(roomId);
    }

    @DELETE
    @Path("{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);

        if (room == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Room not found");
            response.put("message", "No room exists with ID: " + roomId);
            response.put("status", 404);

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(response)
                    .build();
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                    "Room " + roomId + " cannot be deleted because it is currently occupied by active hardware."
            );
        }

        DataStore.rooms.remove(roomId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Room deleted successfully");
        response.put("roomId", roomId);
        response.put("status", 200);

        return Response.ok(response).build();
    }
}