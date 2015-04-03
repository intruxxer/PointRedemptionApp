package id.bri.switching.app;
import java.io.IOException;

import javax.jms.JMSException;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOException;
import org.jpos.iso.packager.GenericPackager;

import id.bri.switching.app.*;
import id.bri.switching.helper.*;
import id.bri.switching.mq.*;



public class AppPointRedemption {
	
	public static MQServer mqserver;
	
	public static void main(String[] args) {
				
		mqserver = new MQServer();
		//mqserver.openConnection("tcp://127.0.0.1:61616");
		//mqserver.openConnection("tcp://128.199.102.160:61616");
		mqserver.openConnection("tcp://10.207.11.206:61616");
		mqserver.setupMessageConsumer("PSWLinux0Rdm.Request", "PSWLinux0Rdm.Response");
		
	}
  
}
