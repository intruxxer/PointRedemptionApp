package id.bri.switching.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import id.bri.switching.helper.LogLoader;
import id.bri.switching.helper.MsSqlConnect;
import id.bri.switching.helper.MysqlConnect;
import id.bri.switching.helper.PropertiesLoader;
import id.bri.switching.helper.TextUtil;

public class Inquiry {

	Map<String, String> cardInfo;
	
	public Inquiry(){
		cardInfo = new HashMap<String, String>();
	}
	
	//Table: lbcrdext
	public int inquiryPointCard(String cardNum) throws SQLException {
	    	Statement stm = null; ResultSet rs = null; int currPointBal = 0;
	    	try {	    	
		    	String db_user = "pointman";
		        String db_pass = "point2015";
		       
		        Class.forName("com.mysql.jdbc.Driver");
		        String url = "jdbc:mysql://127.0.0.1:3306/clcb_module";
		        Connection con = DriverManager.getConnection(url, db_user, db_pass);
		        
		        String pointQuery = "SELECT LB_CP_PAS_CURR_BAL FROM lbcrdext WHERE LB_CARD_NMBR = " + cardNum;
		        stm = con.createStatement();
	            rs = stm.executeQuery(pointQuery);
	            while (rs.next()){
	    			currPointBal = rs.getInt("LB_CP_PAS_CURR_BAL");
	    		}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
	            if (stm != null) { stm.close(); }
	        }
			
	    	return currPointBal;
	}
	
	//Table: lbcpcrd
    public Map<String, String> inquiryStatusCard(String cardNum) throws SQLException {
    	Statement stm = null; ResultSet rs = null;
    	try {
	    	String db_user = "pointman"; String db_pass = "point2015";
	        Class.forName("com.mysql.jdbc.Driver");
	        String url = "jdbc:mysql://128.199.102.160:3306/clcb_module";
	        //String url = "jdbc:mysql://127.0.0.1:3306/clcb_module";
	        Connection con = DriverManager.getConnection(url, db_user, db_pass);
	        
	        /*
	         * USING JOIN APPROACH - Combining Status Checking, and if given OK, also retrieve Point Balance
	    	   String statusQuery = "SELECT b.LB_CP_PAS_CURR_BAL FROM lbcpcrd a" 
	    					 	+ " INNER JOIN lbcrdext b ON a.CP_CARDNMBR = b.LB_CARD_NMBR"
	    					 	+ " WHERE a.CP_CARDNMBR = " + cardNum +" AND a.CP_POSTING_FLAG = 'PP'"
	    					 	+ " AND a.CM_STATUS IN ('1', '2') AND a.CP_BLOCK_CODE IN ('V', 'P', 'Q', ' ')";
	    	*/
	        /*
	         * USING ONLY VALID CARD
	        String statusQuery = "SELECT * FROM lbcpcrd WHERE CP_CARDNMBR ='" + cardNum +"' "
	        			   	   + "AND CP_POSTING_FLAG = 'PP' AND CM_STATUS IN ('1', '2') "
	        			   	   + "AND CP_BLOCK_CODE IN ('V', 'P', 'Q', ' ')";
	        */
	        String statusQuery = "SELECT CP_POSTING_FLAG, CP_BLOCK_CODE, CM_STATUS FROM lbcpcrd WHERE CP_CARDNMBR ='" + cardNum +"'";
	        stm = con.createStatement();
	        rs = stm.executeQuery(statusQuery);
	            
	            
            if(rs.next()) {
            	cardInfo.put("cardNumber", cardNum);
            	System.out.println("inquiry:"+statusQuery);
            	System.out.println(rs.getString("CP_POSTING_FLAG"));
            	System.out.println(rs.getString("CP_BLOCK_CODE"));
            	System.out.println(rs.getString("CM_STATUS"));
            	if( !rs.getString("CP_POSTING_FLAG").equals("PP") ) {
	            	cardInfo.put("cardStatus", "-PP");
            	}
            	else if( !(rs.getString("CP_BLOCK_CODE").equals(" ") || rs.getString("CP_BLOCK_CODE").equals("V") || rs.getString("CP_BLOCK_CODE").equals("P") || rs.getString("CP_BLOCK_CODE").equals("Q") ) ) {
	            	cardInfo.put("cardStatus", "-BC");
            	}
            	else if( !(rs.getString("CM_STATUS").equals("1") || rs.getString("CM_STATUS").equals("2") ) ) {
            		cardInfo.put("cardStatus", "-ST");
            	}else {
            		cardInfo.put("cardStatus", "OK");
            	}
             }else {
            	cardInfo.put("cardNumber", cardNum);
             	cardInfo.put("cardStatus", "N/A"); 
             }
            
    	} catch (SQLException e ) {
    		e.printStackTrace();
	    } catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
	            if (stm != null) { stm.close(); }
	        }
    	
		return cardInfo;    	
	}
}
