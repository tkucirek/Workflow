package org.camunda.bpm.servlets;

import java.io.IOException;



import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.main.Customize;
import org.camunda.bpm.messages.InsuranceOffering;


/**
 * @author Capitol Recieves the necessary information from Bvis and starts a new claim instance at the address ".../contract-process/BeginClaimServlet"
 */

public class BeginClaimServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		PrintWriter out = response.getWriter();
		

		Map<String, Object> map = new HashMap<String, Object>();
		
		
		String jsonString = IOUtils.toString(request.getInputStream());

		JSONObject json = new JSONObject(jsonString); 
		
		String customer_id = json.getString("customer_id") ;
		String damage_classification = String.valueOf(json.getLong("damage_classification"));
		
		
		map.put("customer_id", customer_id);
		map.put("damage_classification", damage_classification);
		System.out.println("CustomerID: " + customer_id + " Damage Classification: " + damage_classification);
		
		ProcessInstance processInstance;
		
		processInstance = runtimeService.startProcessInstanceByMessage("instantiationMessageClaim", map);
		String prozessid = processInstance.getId();
		
		//String deductible_Amount = (String) runtimeService.getVariable(prozessid, "deductible_Amount");
		
		
		out.print("ok");
		
		
}
}
