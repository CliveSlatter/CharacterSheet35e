package controllers;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Character{
    public static void AddCharacter(){

    }

    public static void UpdateCharacter(){

    }

    public static void ListCharacters(int playerID){
        try {
            PreparedStatement ps = db.PreparedStatement("SELECT * FROM character WHERE playerId = ?");
            ps.setInt(1,playerID);
            ResultSet rs = ps.executeQuery();
        }catch (SQLException sql){
            System.out.println(sql);
        }

    }

    public static void RemoveCharacter(int playerID){

    }

    public static void RetrieveCharacter(int playerID){

    }
}
