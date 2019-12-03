package Controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static main.Main.db;

@Path("skills/")
public class CharacterSkills {

    @GET
    @Path("char")
    @Produces(MediaType.APPLICATION_JSON)
    public String skills(){
        System.out.println("skills/char");
        JSONArray list = new JSONArray();
        try{
            PreparedStatement ps = db.prepareStatement("SELECT CharacterSkills.charID, CharacterSkills.skillID, CharacterSkills.points, skill.skillName FROM CharacterSkills INNER JOIN skill ON CharacterSkills.skillID = skill.id");
            //ps.setInt(1,1);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                JSONObject jso = new JSONObject();
                jso.put("charID",rs.getInt(1));
                jso.put("skillID",rs.getInt(2));
                jso.put("points",rs.getInt(3));
                jso.put("name",rs.getString(4));
                list.add(jso);
            }
        }catch(Exception ex){
            System.out.println(ex);
        }
        return list.toString();
    }

}
