package controllers;

import models.PasswordHash;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static main.Main.db;

@Path("user/")
public class User {
    @POST
    @Path("login")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String login(@FormDataParam("username") String username, @FormDataParam("password") String password){
        try{

            // Checks if either of the two parameters are empty

            if(username==null || password == null){
                throw new Exception("Either the username or password form data parameters are missing in the HTTP request.");
            }
            System.out.println("/user/login - Attempt by " + username);

            // Gets the relevant information from the User class i.e. the salt and the hash for the password
            // The salt is needed to add to the submitted password when creating the hash again

            PreparedStatement ps = db.prepareStatement(
                    "SELECT Username, salt, hash, SessionToken FROM User WHERE Username = ?"
            );
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();



            // Compares the stored hash against the newly recreated hash which should match

            if (rs != null && rs.next()) {
                PasswordHash ph = generateStrongPasswordHash(password, rs.getBytes(2)); // Creates the hash of the submitted password
                if (!ph.getHash().equals(rs.getString("hash"))) {
                    return "{\"error\": \"Incorrect password\"}";
                }

                // Creates the new session token before updating the current user's details
                String token = UUID.randomUUID().toString();
                PreparedStatement statement2 = db.prepareStatement("UPDATE user SET SessionToken = ? WHERE username = ?");
                statement2.setString(1, token);
                statement2.setString(2, username);
                statement2.executeUpdate();

                // returns the current token to the calling method
                return "{\"token\": \"" + token + "\"}";

            } else {
                return "{\"error\": \"Can't find player account.\"}";
            }
        }catch(Exception e){
            String error = "Database error - can't process login: " + e.getMessage();
            System.out.println(error);
            return "{\"error\": \"" + error + "\"}";
        }
    }

    @POST
    @Path("logout")
    public void logout(@CookieParam("sessionToken")Cookie sessionToken){
        System.out.println("/admin/logout - Logging out user");

        if (sessionToken != null) {
            String token = sessionToken.getValue();
            try {
                PreparedStatement statement = db.prepareStatement("Update user SET SessionToken = NULL WHERE SessionToken = ?");
                statement.setString(1, token);
                statement.executeUpdate();
            } catch (Exception resultsException) {
                String error = "Database error - can't update 'User' table: " + resultsException.getMessage();
                System.out.println(error);
            }
        }
    }

    @POST
    @Path("create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String CreatePlayer(@FormDataParam("username") String username,@FormDataParam("password") String password){
        System.out.println("New user created!!");
        try{

            if(username == null || password == null){
                throw new Exception("Either the username or password form data parameters are missing in the HTTP request.");
            }
            PasswordHash ph = generateStrongPasswordHash(password, getSalt());
            String token = UUID.randomUUID().toString();
            PreparedStatement ps = db.prepareStatement("Insert into user(username,salt,hash,sessionToken) VALUES (?,?,?,?)");
            ps.setString(1,username);
            ps.setBytes(2,ph.getSalt());
            ps.setString(3,ph.getHash());
            ps.setString(4,token);
            ps.executeUpdate();
            return "{\"status\": \"OK\"}";
        }catch(Exception e){
            String error = "Database error - can't insert into 'User' table: " + e.getMessage();
            System.out.println(error);
            return "{\"error\": \"" + error + "\"}";
        }
    }

    private static PasswordHash generateStrongPasswordHash(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException
    {


        int iterations = 1000; // the number of times the password is hashed producing a symmetric key
        char[] chars = password.toCharArray(); // converts the password into an array so that it can be used by PBEKeySpec

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded(); // produces the final hash to be stored
        // creates and returns an object with the hash information needed to be stored in the table User
        return new PasswordHash(salt,toHex(hash));
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        // Generates a random salt to be used with the password for hashing

        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array)
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }

    public static String validateCurrentPlayer(Cookie sessionCookie){
        if (sessionCookie != null) {
            String token = sessionCookie.getValue();
            try {
                PreparedStatement statement = db.prepareStatement("SELECT Username FROM User WHERE SessionToken = ?");
                statement.setString(1, token);
                ResultSet results = statement.executeQuery();
                if (results != null && results.next()) {
                    return results.getString("Username");
                }
            } catch (Exception resultsException) {
                String error = "Database error - can't select by id from 'Admins' table: " + resultsException.getMessage();
                System.out.println(error);
            }
        }
        return null;
    }


}
