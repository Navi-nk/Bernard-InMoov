package org.myrobotlab.service;

import org.myrobotlab.framework.Service;
import org.myrobotlab.framework.ServiceType;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.logging.LoggingFactory;
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



public class ApiAI extends Service implements TextListener, TextPublisher {

  private static final long serialVersionUID = 1L;

  public final static Logger log = LoggerFactory.getLogger(ApiAI.class);
  
  private AIRequest request;
  private AIResponse response;
  private AIConfiguration configuration;

  AIDataService dataService;


  public ApiAI(String n) {
    super(n);
  }

  /**
   * This static method returns all the details of the class without it having
   * to be constructed. It has description, categories, dependencies, and peer
   * definitions.
   * 
   * @return ServiceType - returns all the data
   * 
   */
  static public ServiceType getMetaData() {

    ServiceType meta = new ServiceType(ApiAI.class.getCanonicalName());
    meta.addDescription("ChatBot Service using Api.AI platform");
    meta.setAvailable(true); // false if you do not want it viewable in a gui
    // add dependency if necessary
    // meta.addDependency("org.coolproject", "1.0.0");
    meta.addCategory("intelligence");
    return meta;
  }
  
  public void configureAIBot(String token){
	  configuration = new AIConfiguration(token);
	  dataService = new AIDataService(configuration);
  }
  
  public AIResponse getAIResponse(String input){
	  if(!configuration.equals(null) || !dataService.equals(null)){
		  log.info(String.format("Api.AI request: %s", input ));
		  try {
			  request = new AIRequest(input);
			  response = dataService.request(request);
		  }
		  catch (Exception ex) {
			  ex.printStackTrace();
		  }
	  }
	  else{
		  log.error("Please Configure Bot with API.AI access token");
		  response=null;
	  }
	  return response;
  }
  
  public String getParsedRespose(AIResponse aiResponse){
	  String strResponse;
	  if (aiResponse.getStatus().getCode() == 200) {
		  strResponse = aiResponse.getResult().getFulfillment().getSpeech();
        } else {
          strResponse = aiResponse.getStatus().getErrorDetails();
        }
	  log.info(String.format("Api.AI response: %s",  strResponse));
	  invoke("publishText", strResponse);
	  return strResponse;
  }

  public static void main(String[] args) {
    try {

      LoggingFactory.init(Level.INFO);

      Runtime.createAndStart("gui", "SwingGui");

      ApiAI ai = (ApiAI) Runtime.createAndStart("bernard", "ApiAI");
      ai.configureAIBot("4f0a1116671b4e2ab5e38f3f5c17a840");
      String res = ai.getParsedRespose(ai.getAIResponse("Hi there"));
      
      log.info(res);

    } catch (Exception e) {
      Logging.logError(e);
    }
  }

@Override
public String publishText(String text) {
	return text;
}

@Override
public void addTextListener(TextListener service) {
	// TODO Auto-generated method stub
	addListener("publishText", service.getName(), "onText");
}

@Override
public void onText(String text) {
	getParsedRespose(getAIResponse(text));
}

}
