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
            if(username==null || password == null){
                throw new Exception("Either the username or password form data parameters are missing in the HTTP request.");
            }
            System.out.println("/user/login - Attempt by " + username);

            PreparedStatement ps = db.prepareStatement(
                    "SELECT Username, hash, SessionToken FROM User WHERE Username = ?"
            );
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            PasswordHash ph = generateStrongPasswordHash(password);


            if (rs != null && rs.next()) {
                if(ph.getHash().equals(rs.getString("hash"))){
                    System.out.println("Passwords match");
                }else{
                    return "{\"error\": \"Passwords don't match\"}";
                }

                String token = UUID.randomUUID().toString();
                PreparedStatement statement2 = db.prepareStatement(
                        "UPDATE user SET SessionToken = ? WHERE username = ?"
                );
                statement2.setString(1, token);
                statement2.setString(2, username);
                statement2.executeUpdate();
                return "{\"token\": \"" + token + "\"}";

            } else {
                return "{\"error\": \"Can't find user account.\"}";
            }
        }catch(Exception e){

        }
        return "Success";
    }

    public void logout(@CookieParam("sessionToken")Cookie sessionToken){

    }

    @POST
    @Path("create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String CreateUser(@FormDataParam("username") String username,@FormDataParam("password") String password){
        System.out.println("New user created!!");
        try{

            if(username == null || password == null){
                throw new Exception("Either the username or password form data parameters are missing in the HTTP request.");
            }
            PasswordHash ph = generateStrongPasswordHash(password);
            String token = UUID.randomUUID().toString();
            PreparedStatement ps = db.prepareStatement("Insert into user(username,iterations,salt,hash,sessionToken) VALUES (?,?,?,?,?)");
            ps.setString(1,username);
            ps.setInt(2,ph.getIterations());
            ps.setBytes(3,ph.getSalt());
            ps.setString(4,ph.getHash());
            ps.setString(5,token);
            ps.executeUpdate();
            return "{\"status\": \"OK\"}";
        }catch(Exception e){
            String error = "Database error - can't insert into 'User' table: " + e.getMessage();
            System.out.println(error);
            return "{\"error\": \"" + error + "\"}";
        }
    }

    private static PasswordHash generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        PasswordHash ph = new PasswordHash(iterations,getSalt(),toHex(hash));
        return ph;
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException
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
