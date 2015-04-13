package id.bri.switching.prototype;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import id.bri.switching.helper.ISO8583PSWPackager;
import id.bri.switching.helper.PropertiesLoader;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOException;
import org.jpos.iso.packager.GenericPackager;

import id.bri.switching.app.*;
import id.bri.switching.helper.*;
import id.bri.switching.mq.*;


public class ExperimentalPSWdatabase {

	public static void main(String[] args) {
		//Statement stm = null;
		//ResultSet rs = null;
		int rows = 0;
		try {	    	
	    	//String db_user = "pointman";
	        //String db_pass = "point2015";
	        //String db_user = "csap";
	        //String db_pass = "c5@p";
	        
	    	//Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	    	//String url = "jdbc:sqlserver://"+PropertiesLoader.getProperty("DB_URL")+":"+PropertiesLoader.getProperty("DB_PORT")+";databaseName="+dbName;
	    	//connection = DriverManager.getConnection(url, db_user, db_pass);
	        Connection con = null;
	        //String url = "jdbc:mysql://10.107.11.18:3306/clcb_module";
	        
	        try {
	            System.out.println("Loading driver...");
	            Class.forName("com.mysql.jdbc.Driver");
	            System.out.println("Driver loaded!");
	        } catch (ClassNotFoundException e) {
	            throw new RuntimeException("Cannot find the driver in the classpath!", e);
	        }
	        
	        try {
	            System.out.println("Connecting database...");
	            //con = DriverManager.getConnection(url, db_user, db_pass);
	            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clcb_module?" +
                        "user=csap&password=c5@p");
	            if(con != null){
	            	System.out.println("Database connected!");
	            	System.out.println(String.valueOf(con));
	            }
	        } catch (SQLException e) {
	            throw new RuntimeException("Cannot connect the database!", e);
	        } finally {
	            //System.out.println("Closing the connection.");
	            //if (con != null) try { con.close(); } catch (SQLException ignore) {}
	        }
	        
	        Logging logHistory = new Logging();
			String[] history = {
				"0", "3", "41", "0", "0", "150401", "39",
				"42", "35", "14", "35", "'Test DB'", "'CLCB-PROG'",
				"NULL", "NULL", "NULL", "NULL", "NULL", "0005"
			};
			System.out.println("Saving to history...");
			logHistory.saveRedeemHistory(history);
			System.out.println("History saved!");
	        
	        //String pointQuery = "SELECT LB_CP_PAS_CURR_BAL FROM lbcrdext WHERE LB_CARD_NMBR = 5188280232465600";
	        //stm = con.createStatement();
            //rs = stm.executeQuery(pointQuery);
	        
			/*
            try {
        		while (rs.next()){
        			System.out.println(String.valueOf(rs.getInt("LB_CP_PAS_CURR_BAL")));
        		} 
            }catch (SQLException e) {
        		e.printStackTrace();
        	}
        	*/

    	} //catch (SQLException e) {
    		//e.printStackTrace();
    	//} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//PointRedeem pointRedeem = new PointRedeem();
		//String cardNum = ""; String tblName = "";
		//Integer point = pointRedeem.inquiryPoint(cardNum, tblName);

	}

}
