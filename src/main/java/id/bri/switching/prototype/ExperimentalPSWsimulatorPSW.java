package id.bri.switching.prototype;
import java.io.IOException;

import javax.jms.JMSException;

import id.bri.switching.helper.ISO8583PSWPackager;
import id.bri.switching.helper.LogLoader;
import id.bri.switching.helper.PropertiesLoader;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOException;
import org.jpos.iso.packager.GenericPackager;

import id.bri.switching.app.*;
import id.bri.switching.helper.*;
import id.bri.switching.mq.*;

//AS IF WE ARE PSW
//We set PSWPoint.Request because we dont have message yet from PSW
public class ExperimentalPSWsimulatorPSW {
	// Require:  iptables -A INPUT -i eth0 -p tcp --destination-port [PortNo] -j ACCEPT 
	// e.g. 3306 for MySQL [PortNo], 61616 for ActiveMQ
	public static void main(String[] args) {
		try{
			String isoMessage = "";
			ISO8583PSWPackager packager = new ISO8583PSWPackager();
			//GenericPackager packager = new GenericPackager("packager/iso8583.xml");
			ISOMsg isoMsg = new ISOMsg();
	        isoMsg.setPackager(packager);
	        
	        
	        //Packing an ISO Message
	        isoMsg.setMTI("0200"); //Type: Financial Req  (4)
	        //isoMsg.set(2, "   5188280232469701"); //CH No (19)
			isoMsg.set(3, "303030"); //Trx/Proc code (6)
			isoMsg.set(4, "   126000000"); //Trx Amt 1,25jt (12)
			isoMsg.set(5, "   126000000"); //1,25jt Settlement (to Merchant) (12)
			isoMsg.set(6, "   115520000"); //1,15jt Settlement (to CH) (12)
			isoMsg.set(12,"150324");// Date (6)
			isoMsg.set(14,"0618");// Expiration Date (4)
			isoMsg.set(24, "015"); //NII - Net Int'l Identifier (3)
			isoMsg.set(35, "   5188280232469701"); //CH No (19)
			isoMsg.set(39, "00"); //Response Code (2)
			isoMsg.set(41, "          223452"); // Terminal ID (16)
			isoMsg.set(42, "          12346"); //Merchant ID (15)
			isoMsg.set(49, "IDR"); //Currency Trx (3)
			isoMsg.set(50, "IDR"); //Currency Settlement (3)
			//String bit63full = "";
			//for(int i=0; i<102; i++){
			//	bit63full += " ";
			//}
			//bit63full += "180";
			//isoMsg.set(63, bit63full);
			byte[] result = isoMsg.pack();
			
			// Produce output ISO 8583 Message String to IO stream
	        for (int i = 0; i < result.length; i++) {
	            isoMessage += (char) result[i];
	        }
            System.out.println("1. "+String.valueOf(result)); 
			System.out.println("2. "+isoMessage);
			System.out.println("3. "+isoMsg);
			
			//Publisher to ActiveMQ
				try { 
					// tcp://128.199.102.160:6616 
					// tcp://192.168.2.2:61616
					// tcp://10.107.11.206:61616
					MQClient mqclient = new MQClient();
					//mqclient.openConnection("tcp://10.107.11.206:61616");
					mqclient.openConnection("tcp://128.199.102.160:61616");
					//mqclient.openConnection("tcp://127.0.0.1:61616");
				    mqclient.setupMessageProducer("PSWLinux0Rdm.Request", isoMessage);
				} catch (JMSException e) {
			        	if (e.getLinkedException() instanceof IOException) {
			                // ActiveMQ is not running. Do some logic here.
			                // use the TransportListener to restart the activeMQ connection
			                // when activeMQ comes back up.
			        		
			        	} else if (e.getMessage().contains("Connection refused")) {
			        		LogLoader.setError(MQServer.class.getSimpleName(), "Cannot connect to MQ, connection refused");
			        	} else {
			        		LogLoader.setError(MQServer.class.getSimpleName(), "Cannot connect to MQ, error unknown");
			        	}
			    }
					
			}catch (ISOException e) {
				e.printStackTrace();
			}
	        
			

	}

}
