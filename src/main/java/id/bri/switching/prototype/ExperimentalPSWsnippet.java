package id.bri.switching.prototype;
import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import id.bri.switching.app.*;
import id.bri.switching.helper.*;
import id.bri.switching.mq.*;

public class ExperimentalPSWsnippet {

	public static void main(String[] args) {
	    String Str = new String("Tutorials" );
	    System.out.print("String Length 0,4 :" );
	    System.out.println(Str.substring(0, Str.length()-2));
	    
	    Map<String, String>cardInfo = new HashMap<String, String>();
	    if(cardInfo.isEmpty())
	    	System.out.println("Empty Map is initialized.");
	    cardInfo.put("cardNumber", "cardNum");
		cardInfo.put("cardPoint", "cardPoint");
    	cardInfo.put("cardStatus", "OK");
	    System.out.println(cardInfo);
	    
	    String cardNum = "5188280232469701";
	    Inquiry inq = new Inquiry();
	    int pointOfCard = 0;
		try {
			pointOfCard = inq.inquiryPointCard(cardNum);
		} catch (SQLException e) {
			e.printStackTrace();
		};
	 
	    int bit63fulllength = 105; String bit63full = "";
		int bit63datalength = String.valueOf(pointOfCard).length();
		int bit63padding = bit63fulllength - bit63datalength;
		for(int i=0; i<bit63padding; i++){
			bit63full += " ";
		}
		bit63full += String.valueOf(pointOfCard);
		System.out.println(bit63full);
	}

}
