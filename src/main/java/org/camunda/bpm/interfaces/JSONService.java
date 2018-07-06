package org.camunda.bpm.interfaces;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/message")
public class JSONService {

    @GET
    @Path("/{param}")
    @Produces("application/json")
    public Response printMessage(@PathParam("param") String msg) {
        String result = "Restful example: " + msg;
        return Response.status(200).entity(result).build();
    }

}