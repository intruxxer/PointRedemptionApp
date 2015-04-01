package id.bri.switching.app;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.HashMap;

import id.bri.switching.helper.LogLoader;
import id.bri.switching.helper.MysqlConnect;
import id.bri.switching.helper.PropertiesLoader;
import id.bri.switching.helper.TextUtil;

public class PointRedeem {
	Connection con;
	MysqlConnect db;
	String response;
	Map<String, String> cardInfo;
	
	public PointRedeem(){
		db = null; con = null;
		response = null; cardInfo = new HashMap<String, String>();
	}
	
	//Table: lbcrdext
    public Map<String, String> debetPoint(String cardNum, String pointAmt) throws SQLException {
		try {
			db = new MysqlConnect(PropertiesLoader.getProperty("DB_NAME"));
			con = db.getConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
		Inquiry inq = new Inquiry();
    	int currPointBal = inq.inquiryPointCard(cardNum);

    	if ( (currPointBal > 0) && (currPointBal >= Integer.parseInt(pointAmt)) ){
    		currPointBal = currPointBal - Integer.parseInt(pointAmt);
    	}
    	
        String query = "UPDATE lbcrdext SET LB_CP_PAS_CURR_BAL = ? WHERE LB_CARD_NMBR = ?";
        int rows = 0;
        try {
        	PreparedStatement prepStmt = con.prepareStatement(query);
        	prepStmt.setString   (1, String.valueOf(currPointBal));
            prepStmt.setString	 (2, cardNum);
            rows = prepStmt.executeUpdate();
        } catch (SQLException e ) {
        	response = "Error SQL exception Update Point : " + e.toString();
        	e.printStackTrace();
    	}
        
        if(rows > 0){
        	cardInfo.put("cardNum", cardNum);
        	cardInfo.put("cardPoint", String.valueOf(currPointBal));
        	String msgResponse = "UPDATE lbcrdext SUCCESS in" + rows +" rows.";
        	System.out.println("Point Redeem updates? " + msgResponse);
        	return cardInfo;
        }else{
        	String msgResponse = "UPDATE lbcrdext FAILED.";
        	System.out.println("Point Redeem updates? " + msgResponse);
        	return cardInfo;
        }
        
	}
    

}
