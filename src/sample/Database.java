package sample;

import org.sqlite.core.DB;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private String Path;
    private Connection conn = null;
    private Statement stmt = null;

    Database(String DBPath){
        try {
            Class.forName("org.sqlite.JDBC");
            Path = DBPath;
            conn = DriverManager.getConnection("jdbc:sqlite:" + Path);
            stmt = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Reload(){
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:" + Path);
            stmt = conn.createStatement();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public int GetMaxCount() throws SQLException{
        int count = 0;
        ResultSet rs = stmt.executeQuery("select count(filename) from list");
        while (rs.next()) {
            count = rs.getInt("count(filename)");
        }
        rs.close();
        return count;
    }

    public ResultSet SearchFile(String FileName) throws SQLException {
        return stmt.executeQuery("select * from list where filename = '" + FileName + "'");
    }

    public ResultSet SearchFileByUser(String UserID) throws SQLException{
        return stmt.executeQuery("select * from list where username = '" + UserID + "'");
    }
}
