package controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.*;

@Path("user/")
public class User {
    @GET
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public String login(){

        return "Success";
    }
}
