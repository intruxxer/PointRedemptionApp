package id.bri.switching.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;

public class Logging {
	public Logging(){
		
	}
	public void saveRedeemHistory(String[] history){
		try {	    	
			int rows = 0;
	    	String db_user = "pointman";
	        String db_pass = "point2015";
	       
	        Class.forName("com.mysql.jdbc.Driver");
	        String url = "jdbc:mysql://128.199.102.160:3306/clcb_module";
	        Connection con = DriverManager.getConnection(url, db_user, db_pass);
	        
	        String logQuery = "INSERT INTO lbhst_redeem (" +
	        				  "CPR_MTI, CPR_PROC_CODE, CPR_TERM_ID, CPR_BATCH_NBR, " +
	        				  "CPR_TRX_DATE, CPR_TRX_TIME, CPR_REC_STATUS, CPR_MERCH_NBR, " +
	        				  "CPR_CARDHOLDER_NBR, CPR_EXP_DATE, CPR_ACCT_NBR, CPR_DESC_1, " +
	        				  "CPR_CLCB_PROG, CPR_B063_SALES_AMT, CPR_B063_REDEEM_AMT, " +
	        				  "CPR_B063_NET_SALES_AMT, CPR_B063_REDEEM_PTS, CPR_B063_BAL_PTS, " +
	        				  "CPR_TRX_SOURCE" + ") VALUES (" + 
	        				  history[0] + ", " + history[1] + ", " + history[2] + ", " + history[3] + ", " + history[4] + ", " +
	        				  history[5] + ", " + history[6] + ", " + history[7] + ", " + history[8] + ", " + history[9] + ", " +
	        				  history[10] + ", " + history[11] + ", " + history[12] + ", " + history[13] + ", " + history[14] + ", " +
	        				  history[15] + ", " + history[16] +  ", " +history[17] + ", " + history[18] + 
	        				  ")";
	        try {
	        	PreparedStatement prepStmt = con.prepareStatement(logQuery);
	        	//System.out.println(String.valueOf(prepStmt));
	        	//prepStmt.setString(1, "");
	            //prepStmt.setString(2, "");
	            rows = prepStmt.executeUpdate();
	        } catch (SQLException e ) {
	        	
	        	e.printStackTrace();
	    	}
	        
	        if(rows > 0){
	        	String msgResponse = "Logging Trx is successful with " + rows +" rows.";
	        	System.out.println("Logging TRX? " + msgResponse);
	        }else{
	        	String msgResponse = "Logging Trx is FAILED.";
	        	System.out.println("Logging TRX? " + msgResponse);
	        	System.out.println(logQuery);
	        }
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
