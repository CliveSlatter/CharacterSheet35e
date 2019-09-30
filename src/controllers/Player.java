package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static Main.Main.db;

@Path("Player")
public class Player {
    @POST
    @Path("login")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String Login(@FormDataParam("username") String username,
                        @FormDataParam("password") String password){
        try{
            PreparedStatement ps = db.prepareStatement("SELECT username, salt, hash, sessionToken FROM user WHERE username = ?");
            ps.setString(1,username);
            ResultSet rs = ps.executeQuery();
            if (rs != null && rs.next()) {
                if (!password.equals(rs.getString("Password"))) {
                    return "{\"error\": \"Incorrect password\"}";
                }
                String token = UUID.randomUUID().toString();
                PreparedStatement ps2 = db.prepareStatement("UPDATE user SET sessionToken = ? WHERE username = ?");
                ps2.setString(1, token);
                ps2.setString(2, username);
                ResultSet rs2 = ps2.executeQuery();
            }
        }catch(Exception e){
            System.out.println(e);
        }

        return token;
    }
}
