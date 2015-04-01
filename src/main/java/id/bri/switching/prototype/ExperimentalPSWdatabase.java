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
	    	String db_user = "pointman";
	        String db_pass = "point2015";
	        
	    	//Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	    	//String url = "jdbc:sqlserver://"+PropertiesLoader.getProperty("DB_URL")+":"+PropertiesLoader.getProperty("DB_PORT")+";databaseName="+dbName;
	    	//connection = DriverManager.getConnection(url, db_user, db_pass);
	       
	        Class.forName("com.mysql.jdbc.Driver");
	        //String url = "jdbc:mysql://128.199.102.160:3306/clcb_module";
	        String url = "jdbc:mysql://127.0.0.1:3306/clcb_module";
	        Connection con = DriverManager.getConnection(url, db_user, db_pass);
	        
	        //String pointQuery = "SELECT LB_CP_PAS_CURR_BAL FROM lbcrdext WHERE LB_CARD_NMBR = 5188280232465600";
	        //stm = con.createStatement();
            //rs = stm.executeQuery(pointQuery);
	        Logging log = new Logging();
			String[] history = {
				"0", "3", "41", "0", "0", "150401", "39",
				"42", "35", "14", "35", "'Inquiry PC303030'", "'CLCB-PROG'",
				"NULL", "NULL", "NULL", "NULL", "NULL", "0005"
			};
			
			log.saveRedeemHistory(history);
            
			/*
            try {
        		while (rs.next()){
        			System.out.println(String.valueOf(rs.getInt("LB_CP_PAS_CURR_BAL")));
        		} 
            }catch (SQLException e) {
        		e.printStackTrace();
        	}
        	*/

    	} catch (SQLException e) {
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//PointRedeem pointRedeem = new PointRedeem();
		//String cardNum = ""; String tblName = "";
		//Integer point = pointRedeem.inquiryPoint(cardNum, tblName);

	}

}
