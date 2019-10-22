package controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static Main.Main.db;

@Path("character/")
public class Character{

    @GET
    @Path("getdetails/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public static String GetCharacter(@PathParam("id") int id){
        JSONArray abilities = new JSONArray();
        JSONArray skills = new JSONArray();
        JSONArray feats = new JSONArray();
        JSONObject basics = new JSONObject();
        JSONObject character = new JSONObject();
        try {
            PreparedStatement psa = db.prepareStatement("SELECT characterAbilities.characterId,abilities.abilityName,characterAbilities.abilityPoints,abilityBonus.bonus FROM characterAbilities INNER JOIN abilities ON characterAbilities.abilityId=abilities.abilityId INNER JOIN abilityBonus ON characterAbilities.abilitypoints=abilityBonus.base WHERE characterAbilities.characterId=?");
            PreparedStatement psb = db.prepareStatement("SELECT characterName,class,level,race,alignment,deity,size,age,gender,height,weight,eyes,hair,skin FROM characterSummaryInfo WHERE characterID = ?");
            PreparedStatement pss = db.prepareStatement("SELECT characterskills.charID,skill.skillName, skill.keyAbility, CharacterSkills.points FROM skill INNER JOIN characterskills on skill.id=characterskills.skillID WHERE charid=?");
            PreparedStatement psf = db.prepareStatement("SELECT CharacterFeats.featId, CharacterFeats.characterId, feats.featName from CharacterFeats inner join feats on CharacterFeats.featid = feats.featid WHERE characterId=?");

            psa.setInt(1,id);
            psb.setInt(1,id);
            pss.setInt(1,id);
            psf.setInt(1,id);
            ResultSet rsa = psa.executeQuery();
            ResultSet rsb = psb.executeQuery();
            ResultSet rss = pss.executeQuery();
            ResultSet rsf = psf.executeQuery();
            basics.put("characterName", rsb.getString(1));
            basics.put("class", rsb.getString(2));
            basics.put("level", rsb.getInt(3));
            basics.put("race", rsb.getString(4));
            basics.put("alignment", rsb.getString(5));
            basics.put("deity", rsb.getString(6));
            basics.put("size", rsb.getString(7));
            basics.put("age", rsb.getString(8));
            basics.put("gender", rsb.getString(9));
            basics.put("height", rsb.getInt(10));
            basics.put("weight", rsb.getInt(11));
            basics.put("eyes", rsb.getString(12));
            basics.put("hair", rsb.getString(13));
            basics.put("skin", rsb.getString(14));

            
            while(rsa.next()){
                JSONObject jso = new JSONObject();
                jso.put("characterId",rsa.getInt(1));
                jso.put("ability",rsa.getString(2));
                jso.put("score",rsa.getInt(3));
                jso.put("bonus",rsa.getInt(4));
                abilities.add(jso);
            }

            while(rss.next()){
                JSONObject jso = new JSONObject();
                jso.put("skill",rss.getString(1));
                jso.put("ability",rss.getString(2));
                jso.put("points",rss.getInt(3));
                skills.add(jso);
            }

            while(rsf.next()){
                JSONObject jso = new JSONObject();
                jso.put("featName", rsf.getString(1));
                feats.add(jso);
            }
            character.put("feats",feats);
            character.put("skills",skills);
            character.put("abilities", abilities);
            character.put("basics",basics);

        }catch(Exception e){
            System.out.println(e);
        }
        System.out.println(character.toString());
        return character.toString();
    }


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
        try{
            PreparedStatement psb = db.prepareStatement("SELECT characterName,class,level,race,alignment,deity,size,age,gender,height,weight,eyes,hair,skin FROM characterSummaryInfo WHERE characterID = ?");
            PreparedStatement psa = db.prepareStatement("SELECT characterAbilities.characterId, characterAbilities.abilityPoints FROM characterAbilities INNER JOIN abilities ON characterAbilities.abilityId=abilities.abilityId ");
        }catch(Exception e){

        }
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
