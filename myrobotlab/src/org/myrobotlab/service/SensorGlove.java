package org.myrobotlab.service;

import org.myrobotlab.framework.Service;
import org.myrobotlab.framework.ServiceType;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.service.data.PinData;
import org.myrobotlab.service.interfaces.TextListener;
import org.myrobotlab.service.interfaces.TextPublisher;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

/**
 * ApiAi service for MyRobotLab Uses API.AI to create a ChatBot. 
 * This service is created using the JAVA SDK of API.AI
 *
 * @author Navi
 *
 */



public class SensorGlove extends Service {
	
	transient public Arduino arduino;
	transient public InMoovHand hand;
	
	
	private static final Integer THUMB = 62;
	private static final Integer INDEX = 63;
	private static final Integer MIDDLE = 64;
	private static final Integer RING = 65;
	private static final Integer PINKY = 66;
	

  public SensorGlove(String reservedKey) {
		super(reservedKey);
		arduino = (Arduino) createPeer("arduino");
	}

private static final long serialVersionUID = 1L;

  public final static Logger log = LoggerFactory.getLogger(SensorGlove.class);
  

  /**
   * This static method returns all the details of the class without it having
   * to be constructed. It has description, categories, dependencies, and peer
   * definitions.
   * 
   * @return ServiceType - returns all the data
   * 
   */
  static public ServiceType getMetaData() {

    ServiceType meta = new ServiceType(SensorGlove.class.getCanonicalName());
    meta.addDescription("sensor glove controller on Inmoov hand");
    meta.setAvailable(true); // false if you do not want it viewable in a gui
    // add dependency if necessary
    // meta.addDependency("org.coolproject", "1.0.0");
    meta.addCategory("Gesture");
    return meta;
  }
  
 
  public boolean connect(String port, String type) throws Exception {

	    if (arduino == null) {
	      error("arduino is invalid");
	      return false;
	    }
	    arduino.setBoard(type);
	    arduino.connect(port);

	    if (!arduino.isConnected()) {
	      error("arduino %s not connected", arduino.getName());
	      return false;
	    }
	    
	    broadcastState();
	    return true;
	  }
  
  public void connectHand(InMoovHand hand){
	  if(hand != null){
		  this.hand = hand;
		  hand.thumb.map(0, 180, 184, 305);
		  hand.index.map(0, 180, 211, 299);
		  hand.majeure.map(0, 180, 216, 310);
		  hand.ringFinger.map(0, 180, 244, 305);
		  hand.pinky.map(0, 180, 238, 316);
	  }
  }
  
  public void moveHand(Integer finger, double value){
	  if(hand!=null){
	  switch(finger){
		  case 62:
			  hand.thumb.moveTo(value);
			  break;
		  case 63:
			  hand.index.moveTo(value);
			  break;
		  case 64:
			  hand.majeure.moveTo(value);
			  break;
		  case 65:
			  hand.ringFinger.moveTo(value);
			  break;
		  case 66:
			  hand.pinky.moveTo(value);
			  break;
	  }
	  }
  }
  
  
  public void publishSensorData(PinData[] pinData){
	  Integer numPins = pinData.length;
	  for(int i=0; i<numPins; i++){
		 System.out.println(pinData[i].address+':'+pinData[i].value);
		 if(pinData[i].address >= 62 && pinData[i].address <= 66){
			 moveHand(pinData[i].address,pinData[i].value);
		 }
	  }
  }
  
  public void addArduinoListener(){
	  if(arduino != null){
		  arduino.addListener("publishPinArray",this.getName(),"publishSensorData");
	  }
  }

  public static void main(String[] args) {
    try {

      LoggingFactory.init(Level.INFO);

      Runtime.createAndStart("gui", "SwingGui");

      SensorGlove sg = (SensorGlove) Runtime.createAndStart("sensorglove", "SensorGlove");
      Arduino a = (Arduino) Runtime.create("arduino", "Arduino");
     

    } catch (Exception e) {
      Logging.logError(e);
    }
  }


}
