package org.camunda.bpm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.impl.util.json.JSONObject;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.camunda.bpm.engine.runtime.ProcessInstance;

public class ConfirmationServlet extends HttpServlet {
	int finalPrice;
	String offertype;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		PrintWriter out = response.getWriter();
		
		int finalPrice = 0;
		

		Map<String, Object> map = new HashMap<String, Object>();
		
		
		String jsonString = IOUtils.toString(request.getInputStream());

		JSONObject json = new JSONObject(jsonString); 
		
		String customer_id = String.valueOf(json.getLong("customer_id")) ;
		String offer_id = String.valueOf(json.getLong("offer_id"));
		
		map.put("customer_id", customer_id);
		map.put("offer_id", offer_id);
		
		MessageCorrelationResult processInstance;
		ProcessInstance processinstance;
		String prozessid;
		
		
		runtimeService.correlateMessage("contractConfirmed");
		
			
		processinstance = processInstance.getProcessInstance();
		prozessid = processinstance.getId();
		
		
		
		
		
		
		
		if(json.getLong("offer_id")== 1) {
			finalPrice = (int) runtimeService.getVariable(prozessid, "offerfull");
			offertype = "Vollkasko";
			
		}else if (json.getLong("offer_id") == 2) {
			finalPrice = (int) runtimeService.getVariable(prozessid, "offerhalf");
			offertype = "Halbkasko";
		}
		
		runtimeService.setVariable(prozessid, "finalPrice", finalPrice);
		
		runtimeService.setVariable(prozessid, "offertype" , offertype);
		
		
	
	}
}
