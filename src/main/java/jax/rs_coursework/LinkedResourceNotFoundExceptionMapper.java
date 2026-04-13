package jax.rs_coursework;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Linked resource not found");
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("status", 422);

        return Response.status(422)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}