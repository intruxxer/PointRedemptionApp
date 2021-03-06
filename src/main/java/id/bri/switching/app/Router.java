/**
 * Router
 *
 * Class yang berfungsi untuk memvalidasi tipe message dan mengarahkan kepada
 * class sub proses transaksi, seperti point inquiry atau point payment/redemption
 *
 * @package		id.bri.switching.app
 * @author		PSD Team
 * @copyright           Copyright (c) 2013, PT. Bank Rakyat Indonesia (Persero) Tbk,
 */

// ---------------------------------------------------------------------------------

/*
 * ------------------------------------------------------
 *  Memuat package dan library
 * ------------------------------------------------------
 */

package id.bri.switching.app;

import id.bri.switching.helper.ISO8583PSWPackager;
import id.bri.switching.helper.LogLoader;
import id.bri.switching.helper.ResponseCode;
import id.bri.switching.helper.TextUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Map;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

//  Class Router
public class Router {
    
    /* 
     * Property
     * ---------------------------------------------------------------------
     */
    protected static ResponseCode rc = new ResponseCode();
    
    /**
     * startRouter
     * ------------------------------------------------------------------------
     * 
     * Function to start the router.
     * 
     * @access      public
     * @param       String
     * @return      String
     */
    
    public static synchronized String processISOMessage(String requestString) {
        //  Menampilkan pesan masuk
        LogLoader.setInfo(Router.class.getSimpleName(), "Msg is to be verified.. ");
        String response = "";
        //  try catch ISOMsg
        try {
            //  ISO 8583 ProSwitching Packager & ISOMsg
            ISO8583PSWPackager packager = new ISO8583PSWPackager();
            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.unpack(requestString.getBytes());
            
            //  Hanya melayani MTI 200 Inquiry, Trx & 800 Reversal
            //  [1] Response dari PSW == 0200
            if(isoMsg.getMTI().equals("0200")){
            	LogLoader.setInfo(Router.class.getSimpleName(), "Verifying the message...");
            	// Business logic; Bit 3 defines TRX vs INQ
            	String mti = isoMsg.getMTI();
            	String cardNum = isoMsg.getString(2).trim();
            	String procCode = isoMsg.getString(3).trim();
            	String trxOriAmt = isoMsg.getString(4).trim();
            	String trxChAmt = isoMsg.getString(5).trim();
            	String trxNetAmt = isoMsg.getString(6).trim();
            	String trxTime = isoMsg.getString(12).trim();
            	String expDate = isoMsg.getString(14).trim();
            	//String trxNii = isoMsg.getString(24).trim();
            	//String rCode = isoMsg.getString(39).trim();
            	String tId = isoMsg.getString(41).trim();
            	String mId = isoMsg.getString(42).trim();
            	//String curr = isoMsg.getString(49).trim();
            	
            	// TRANSACTION WITH POINT
        		// Tabels = "lbcpcrd" [status] & "lbcrdext" [point];
        		// Read: DB
        		// DO: (1) Find Point Balance then Decrease it, 
            	//     (2) Form a reply to CardLink (via PSW)
            	if(procCode.equals("101010")) {
            		
            		// MODULE 2
            		LogLoader.setInfo(Router.class.getSimpleName(), " with Bit[3]=101010.");
            	}
            	// INQUIRY
        		// Tabels = "lbcpcrd" [status] & "lbcrdext" [point];
        		// Read: DB
        		// Find: (1) Status/Eligibility of Card, (2) Point Balance
            	else if(procCode.equals("303030")) {
            		Inquiry inq = new Inquiry();
            		Map<String, String> resInquiry = inq.inquiryStatusCard(cardNum);
            		System.out.println("Card Number:"+cardNum);
            		System.out.println("Card Status:"+(String) resInquiry.get("cardStatus"));
            		if((String) resInquiry.get("cardStatus") == "OK"){
            			//Get the current point when cardStatus == OK
            			int pointOfCard = inq.inquiryPointCard(cardNum);
            			int bit63fulllength = 105; String bit63full = "";
            			int bit63datalength = String.valueOf(pointOfCard).length();
            			int bit63padding = bit63fulllength - bit63datalength;
            			for(int i=0; i<bit63padding; i++){
            				bit63full += " ";
            			}
            			bit63full += String.valueOf(pointOfCard);
            			
            			//Set ISOMsg back with Card's Status & Point. 
            			//Obtain them via Map resInquiry Object & PointOfCard
            			isoMsg.set(63, bit63full);
            			//Logging TRX of INQ here
            			Logging log = new Logging();
            			String[] history = {
            				mti, procCode, tId, "0", "2015", "240030", "1",
            				mId, cardNum, expDate, cardNum, "'Inquiry PC303030'", "'CLCB_PROG'",
            				"NULL", "NULL", "NULL", "NULL", "NULL", "00005"
            			};
            			log.saveRedeemHistory(history);
                        resInquiry.clear();
            		}else if((String) resInquiry.get("cardStatus") == "N/A"){
            			//Do something if there's  "NOTOK", categorized as:
            			//(1) Card is not found
            			isoMsg.set(39, "53");
            			resInquiry.clear();
            		}else if((String) resInquiry.get("cardStatus") == "-PP"){
            			//Do something if there's  "NOTOK", categorized as:
            			//(2) Not authorized as it wasn't Primary Card
            			isoMsg.set(39, "05");
            			resInquiry.clear();
            		}else if((String) resInquiry.get("cardStatus") == "-BC"){
            			//Do something if there's  "NOTOK", categorized as:
            			//(3) Not authorized as it wasn't in permitted Block
            			isoMsg.set(39, "05");
            			resInquiry.clear();
            		}else if((String) resInquiry.get("cardStatus") == "-ST"){
            			//Do something if there's  "NOTOK", categorized as:
            			//(4) Not authorized as it wasn't in permitted Status
            			isoMsg.set(39, "05");
            			resInquiry.clear();
            		}
            		
            		LogLoader.setInfo(Router.class.getSimpleName(), " with Bit[3]=303030.");
                	
            	}
            	
            	//Set response Code, set response MTI accordingly
            	//You can set other bits accordingly such as: isoMsg.set(39, "10");
            	isoMsg.setResponseMTI();
                response = new String(isoMsg.pack());

            }
            //[2] Response dari PSW == 0800
        	// Contoh, bila ada request REVERSAL
            else if (isoMsg.getMTI().equals("0800")){
            	LogLoader.setInfo(Router.class.getSimpleName(), "Test connection response received. MTI: " + isoMsg.getMTI());            	
            }
            
            LogLoader.setInfo(Router.class.getSimpleName(), "Response msg that's sent: " + response);
        } catch (SQLException e) {
        	e.printStackTrace();
        } catch (ISOException e) {
        	LogLoader.setError(Router.class.getSimpleName(), e);
        }
        
        return response;
    }
    
    
    /**
     * listenerRouter
     * ------------------------------------------------------------------------
     * 
     * Fungsi memulai listener Router. Routing response transaksi
     * 
     * @access      public
     * @param       String
     * @return      String
     */
    
    public synchronized String listenerRouter(String requestString){
        //  Menampilkan pesan masuk
    	// LogLoader.setInfo(Router.class.getSimpleName(), "Msg received: " + requestString);
        String response = "";
        //  try catch ISOMsg
        try {
            //  ISO 8583 Proswitching Packager & ISOMsg
            ISO8583PSWPackager packager = new ISO8583PSWPackager();
            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.unpack(requestString.getBytes());
            
            //  Hanya melayani MTI 210 & 800
            if(isoMsg.getMTI().equals("0200")) {
                
            }
            else if(isoMsg.getMTI().equals("0210")){ 
            	return TextUtil.formattedResultWithSaldo(isoMsg.getString(39), isoMsg.getString(37), isoMsg.getString(54), new String(isoMsg.pack()));         	
            }
            else if(isoMsg.getMTI().equals("0810")){
            	// Response dari MQ
            	LogLoader.setInfo(Router.class.getSimpleName(), "Test connection response received. MTI: " + isoMsg.getMTI());
            }
            else if(isoMsg.getMTI().equals("0800")){
                isoMsg.set(39, "00");
            }
            else {
                isoMsg.set(39, "79");
                isoMsg.set(48, rc.getResponseDescription("79"));
                System.out.println("Transaction rejected. MTI : " + isoMsg.getMTI());
            }
            
            //isoMsg.setResponseMTI();
            response = new String(isoMsg.pack());
            
        } catch (ISOException e) {
        	response = TextUtil.formattedResult("ER", "-1", e.getMessage());
        	LogLoader.setError(Router.class.getSimpleName(), e);
        }
        
        return response;
    }
    
}
