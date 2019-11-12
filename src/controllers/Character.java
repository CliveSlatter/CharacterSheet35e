package controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static Main.Main.db;

@Path("character/")
public class Character{

    private static String error;
    private static PreparedStatement ps;
    private static ResultSet rs;

    @GET
    @Path("getCharacter/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public static String GetCharacter(@PathParam("id") int id){
        JSONObject character = new JSONObject();
        try {
            character.put("feats", getFeats(id));
            character.put("skills", getSkills(id));
            character.put("abilities", getAbilities(id));
            character.put("basics",getBasics(id));
        }catch(Exception e){
            System.out.println(e);
        }
        System.out.println(character.toString());
        return character.toString();
    }


    /*public static String AddCharacter(){
        JSONObject basics = new JSONObject();
        try{

        }catch(Exception sql){

        }
    }*/

    public static void UpdateCharacter(){

    }

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String ListCharacters(){
        System.out.println("character/list");
        JSONArray list = new JSONArray();
        try {
            PreparedStatement ps = db.prepareStatement("SELECT characterId, characterName, class  FROM characterSummaryInfo WHERE playerId=?");
            ps.setInt(1,1);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                JSONObject jso = new JSONObject();
                jso.put("characterID", rs.getInt(1));
                jso.put("characterName", rs.getString(2));
                jso.put("class", rs.getString(3));
                list.add(jso);
            }

        }catch (Exception sql){
            System.out.println(sql);
        }
        return list.toString();
    }


    @POST
    @Path("remove/{cid}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public static String RemoveCharacter(@PathParam("cid")Integer playerID){
        try{
            if(playerID==null){
                System.out.println("Invalid or missing player ID");
            }
            PreparedStatement ps = db.prepareStatement("DELETE FROM characterSummaryInfo WHERE characterID = ?");
            ps.setInt(1, playerID);
            ps.executeUpdate();
            return "{\"status\": \"OK\"}";
        }catch(Exception e){
            System.out.println("Database error: " + e.getMessage());
            return "{\"error\": \"Unable to delete item, please see server console for more info.\"}";
        }
    }

    public static String getBasics(int charId){
       JSONObject basics = new JSONObject();
       try{
           ps = db.prepareStatement("SELECT characterName,class,level,race,alignment,deity,size,age,gender,height,weight,eyes,hair,skin FROM characterSummaryInfo WHERE characterID = ?");
           ps.setInt(1, charId);
           rs = ps.executeQuery();
           basics.put("characterName", rs.getString(1));
           basics.put("class", rs.getString(2));
           basics.put("level", rs.getInt(3));
           basics.put("race", rs.getString(4));
           basics.put("alignment", rs.getString(5));
           basics.put("deity", rs.getString(6));
           basics.put("size", rs.getString(7));
           basics.put("age", rs.getString(8));
           basics.put("gender", rs.getString(9));
           basics.put("height", rs.getInt(10));
           basics.put("weight", rs.getInt(11));
           basics.put("eyes", rs.getString(12));
           basics.put("hair", rs.getString(13));
           basics.put("skin", rs.getString(14));
           return basics.toString();
       }catch(Exception sql){
           error = "Database error - can't select by id from 'Skills' table: " + sql.getMessage();
       }
        return "{'error': '" + error + "'}";
    }

    private static String getAbilities(int charId){
        JSONArray abilities = new JSONArray();

        try{
            ps = db.prepareStatement("SELECT characterAbilities.characterId,abilities.abilityName,characterAbilities.abilityPoints,abilityBonus.bonus FROM characterAbilities INNER JOIN abilities ON characterAbilities.abilityId=abilities.abilityId INNER JOIN abilityBonus ON characterAbilities.abilitypoints=abilityBonus.base WHERE characterAbilities.characterId=?");
            ps.setInt(1,charId);
            rs = ps.executeQuery();
            while(rs.next()){
                JSONObject jso = new JSONObject();
                jso.put("characterId",rs.getInt(1));
                jso.put("ability",rs.getString(2));
                jso.put("score",rs.getInt(3));
                jso.put("bonus",rs.getInt(4));
                abilities.add(jso);
            }
            return abilities.toString();
        }catch(Exception sql){
            error = "Database error - can't select by id from 'Skills' table: " + sql.getMessage();
        }

        return "{'error': '" + error + "'}";
    }

    private static String getFeats(int charId){
        JSONArray feats = new JSONArray();
        try{
            ps = db.prepareStatement("SELECT CharacterFeats.featId, CharacterFeats.characterId, feats.featName from CharacterFeats inner join feats on CharacterFeats.featid = feats.featid WHERE characterId=?");
            ps.setInt(1,charId);
            rs = ps.executeQuery();
            while(rs.next()){
                JSONObject jso = new JSONObject();
                jso.put("featName", rs.getString(1));
                feats.add(jso);
            }
            return feats.toString();
        }catch(Exception sql){
            error = "Database error - can't select by id from 'Skills' table: " + sql.getMessage();
        }
        return "{'error': '" + error + "'}";
    }

    private static String getSkills(int charId){
        JSONArray skills = new JSONArray();
        try{
            ps = db.prepareStatement("SELECT characterskills.charID,skill.skillName, skill.keyAbility, CharacterSkills.points FROM skill INNER JOIN characterskills on skill.id=characterskills.skillID WHERE charid=?");
            ps.setInt(1,charId);
            rs = ps.executeQuery();
            while(rs.next()){
                JSONObject jso = new JSONObject();
                jso.put("skill",rs.getString(1));
                jso.put("ability",rs.getString(2));
                jso.put("points",rs.getInt(3));
                skills.add(jso);
            }
            return skills.toString();
        }catch(Exception sql){
            error = "Database error - can't select by id from 'Skills' table: " + sql.getMessage();
        }
        return "{'error': '" + error + "'}";
    }

    @GET
    @Path("basic")
    @Produces(MediaType.APPLICATION_JSON)
    public String RetrieveCharacter(){
        System.out.println("/character/basic");
        JSONArray list = new JSONArray();
        try{
            PreparedStatement ps = db.prepareStatement("SELECT characterName,class,level,race,alignment,deity,size,age,gender,height,weight,eyes,hair,skin FROM characterSummaryInfo WHERE characterID = ?");
            ps.setInt(1,1);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                JSONObject jso = new JSONObject();
                jso.put("characterName",rs.getString(1));
                jso.put("class",rs.getString(2));
                jso.put("level",rs.getString(3));
                jso.put("race",rs.getString(4));
                jso.put("alignment",rs.getString(5));
                jso.put("deity",rs.getString(6));
                jso.put("size",rs.getString(7));
                jso.put("age",rs.getString(8));
                jso.put("gender",rs.getString(9));
                jso.put("height",rs.getString(10));
                jso.put("weight",rs.getString(11));
                jso.put("eyes",rs.getString(12));
                jso.put("hair",rs.getString(13));
                jso.put("skin",rs.getString(14));
                list.add(jso);
            }
        }catch(Exception ex){
            System.out.println(ex);
        }

        return list.toString();
    }

    @GET
    @Path("skills")
    @Produces(MediaType.APPLICATION_JSON)
    public String Skills(){
        System.out.println("character/skills");
        JSONArray list = new JSONArray();
        try{
            ps = db.prepareStatement("SELECT skill.SkillName,skill.keyAbility, characterSkills.points from skill inner join characterSkills on characterskills.charID = skill.id WHERE CharacterSkills.charID=?");
            ps.setInt(1,1);
            rs = ps.executeQuery();
            while(rs.next()){
                JSONObject jso = new JSONObject();
                jso.put("skillName",rs.getString(1));
                jso.put("keyAbility",rs.getString(2));
                jso.put("points", rs.getInt(3));
                list.add(jso);
            }
        }catch(Exception ex){
            System.out.println(ex);
        }
        return list.toString();
    }
}
