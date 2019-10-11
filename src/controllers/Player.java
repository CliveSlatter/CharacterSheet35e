package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static Main.Main.db;

@Path("player/")
public class Player {
    @POST
    @Path("login")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String Login(@FormDataParam("username") String username,
                        @FormDataParam("password") String password){
        try{
            if(username==null || password==null) throw new Exception("Username or password is missing in the HTTP request.");
            System.out.println("player/login - Attempt by " + username + " " + password);

            PreparedStatement ps = db.prepareStatement("SELECT username, salt, hash, sessionToken FROM user WHERE username = ?");
            ps.setString(1,username);
            ResultSet rs = ps.executeQuery();
            String temp = GenerateHash(password,rs.getBytes("salt"));

            if (rs != null && rs.next()) {
                if (!temp.equals(rs.getString("hash"))) {
                    return "{\"error\": \"Incorrect password\"}";
                }
                String token = UUID.randomUUID().toString();
                System.out.println(token + " " + temp);
                PreparedStatement ps2 = db.prepareStatement("UPDATE user SET sessionToken = ? WHERE username = ?");
                ps2.setString(1, token);
                ps2.setString(2, username);
                ps2.executeUpdate();
                return "{\"token\": \"" + token + "\"}";
            }else {
                return "{\"error\": \"Can't find user account.\"}";
            }
        }catch(Exception e){
            System.out.println(e);
            return "{\"error\": \"" + e + "\"}";
        }


    }

    @POST
    @Path("new")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String addPlayer(  @FormDataParam("username") String username,
                             @FormDataParam("firstname") String firstname,
                             @FormDataParam("lastname") String lastname,
                             @FormDataParam("password1") String password1)
                             {

        try {

            if (username == null) {
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }

            System.out.println("/player/new username=" + username + " - Adding new player to database");

            //String currentUsername = ValidateSessionCookie(sessionCookie);
            /*if (currentUsername == null) {
                return "{\"error\": \"Not logged in as valid player\"}";
            }*/

            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            String token = UUID.randomUUID().toString();
            System.out.println(salt);
            String hash = GenerateHash(password1,salt);

            PreparedStatement  ps = db.prepareStatement("INSERT INTO user (username, salt, hash, sessionToken) VALUES (?, ?, ?, ?)");
            ps.setString(1, username);
            ps.setBytes(2,salt);
            ps.setString(3,hash);
            ps.setString(4,token);
            ps.executeUpdate();

            return "{\"status\": \"OK\"}";

        } catch (Exception resultsException) {
            String error = "Database error - can't insert into 'User' table: " + resultsException.getMessage();
            System.out.println(error);
            return "{\"error\": \"" + error + "\"}";
        }

    }
    public static String ValidateSessionCookie(Cookie sessionCookie) {
        if (sessionCookie != null) {
            String token = sessionCookie.getValue();
            try {
                PreparedStatement statement = db.prepareStatement("SELECT Username FROM User WHERE SessionToken = ?"
                );
                statement.setString(1, token);
                ResultSet results = statement.executeQuery();
                if (results != null && results.next()) {
                    return results.getString("Username").toLowerCase();
                }
            } catch (Exception resultsException) {
                String error = "Database error - can't select by id from 'Admins' table: " + resultsException.getMessage();

                System.out.println(error);
            }
        }
        return null;
    }

    public static String GenerateHash(String password, byte[] salt) {
        String hashSource = password + salt;
        try {
            MessageDigest hasher = MessageDigest.getInstance("MD5");
            hasher.update(hashSource.getBytes());
            System.out.println(DatatypeConverter.printHexBinary(hasher.digest()).toUpperCase());
            return DatatypeConverter.printHexBinary(hasher.digest()).toUpperCase();
        } catch (NoSuchAlgorithmException nsae) {
            return nsae.getMessage();
        }

    }

    @GET
    @Path("listCharacters/{playerID}")
    @Produces(MediaType.APPLICATION_JSON)
    public String listCharacters(@PathParam("playerID")int playerID){
        JSONArray list = new JSONArray();
        try {
            PreparedStatement ps = db.prepareStatement("SELECT characterId, characterName,class FROM characterSummaryInfo WHERE playerId=?");
            ps.setInt(1,playerID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                JSONObject jso = new JSONObject();
                jso.put("characterID", rs.getInt(1));
                jso.put("characterName", rs.getString(2));
                jso.put("class",rs.getString(3));
                list.add(jso);
            }

        }catch(Exception e){
            return e.toString();
        }
        return list.toString();
    }
}
