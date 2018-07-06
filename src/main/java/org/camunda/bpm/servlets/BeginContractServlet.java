package org.camunda.bpm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.impl.util.json.JSONObject;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.messages.InsuranceOffering;

public class BeginContractServlet extends HttpServlet {
	

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		PrintWriter out = response.getWriter();

		Map<String, Object> map = new HashMap<String, Object>();
		
		
		String jsonString = IOUtils.toString(request.getInputStream());

		JSONObject json = new JSONObject(jsonString); 
		
		
		String name = json.getString("name");
		String customer_id = json.getString("customer_id");
		String customer_type = json.getString("customer_type");
		String rental_duration = json.getString("rental_duration");
		String vehicle_model = json.getString("vehicle_model");
		String number_of_vehicles = json.getString("number_of_vehicles");
		String birth_date = json.getString("birth_date");
		
		System.out.println(json);
		
		map.put("name", name);
		map.put("BvisId", customer_id);
		map.put("Customer_type", customer_type);
		map.put("rental_duration", rental_duration);
		map.put("vehicle_model", vehicle_model);
		map.put("number_of_vehicles", number_of_vehicles);
		map.put("birthdate", birth_date);
		
		
		System.out.println(map);
		

		/*JSONObject jsonObj = new JSONObject(request.getParameter("jsonObj"));

		String name = (String) request.getParameter("name");
		
		System.out.println(request.getParameter("name"));
		map.put("name", name);
		
		System.out.println(map.get(name));
		*/
		out.println("hallo ihr geilen ficker");
		
		
		ProcessInstance processInstance;
		

		processInstance = runtimeService.startProcessInstanceByMessage("instantiationMessageContract", map);
		String prozessid = processInstance.getId();	
		
		/*try {
			wait(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		String offerhalf = (String) runtimeService.getVariable(prozessid, "offerhalf");
		out.println("DIE HALBE OFFER IST: " + offerhalf);
		System.out.println("felix halbe offer ist: " + offerhalf);
		out.println("hallo ihr geilen ficker");
		out.close();
	}

}