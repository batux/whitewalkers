package com.tweet.service.sqlite.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.SQLiteConfig;

public class Main {

	public static Connection getConnection(SQLiteConfig sqliteConfig) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Eteration-1\\searchqueriesdb.db;", sqliteConfig.toProperties());
    }
	
	public static void main(String[] args) {
		
        Connection conn = null;
        
        SQLiteConfig sqliteConfig = new SQLiteConfig();
        sqliteConfig.enableLoadExtension(true);
        
        try {
            conn = getConnection(sqliteConfig);
            
            Statement stat = conn.createStatement();
            stat.execute("SELECT load_extension('mod_spatialite.dll')");
            
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("SELECT tweet_id, AsText(tweet_location) AS tweetLocationAsText FROM query_result");
            
            while (rs.next()) {
            	String tweetId = rs.getString("tweet_id");
            	Object tweetLocation = rs.getObject("tweetLocationAsText");
            	System.out.println(tweetId);
            	System.out.println(tweetLocation);
            }
            
            stat.close();
            if (conn != null)
                conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
