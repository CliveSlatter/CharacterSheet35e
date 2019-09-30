package controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.io.Console;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static Main.Main.db;

@Path("character/")
public class Character{


    public static void AddCharacter(){

    }

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

    public static void RemoveCharacter(int playerID){

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
            PreparedStatement ps = db.prepareStatement("SELECT skill.SkillName,skill.keyAbility, characterSkills.points from skill inner join characterSkills on characterskills.charID = skill.id WHERE CharacterSkills.charID=?");
            ps.setInt(1,1);
            ResultSet rs = ps.executeQuery();
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
