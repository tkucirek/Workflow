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
import java.util.ArrayList;
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
import org.camunda.bpm.engine.impl.util.json.JSONArray;
import org.camunda.bpm.engine.impl.util.json.JSONObject;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.entities.OfferEntity;
import org.camunda.bpm.main.Customize;
import org.camunda.bpm.messages.InsuranceOffering;

public class BeginContractServlet extends HttpServlet {
	
	String neueprozessid;
	

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
		String phone = json.getString("phone_number");
		String mail = json.getString("e_mail");
		String address = json.getString("address");
		
		System.out.println(json);
		
		map.put("name", name);
		map.put("BvisId", customer_id);
		map.put("Customer_type", customer_type);
		map.put("rental_duration", rental_duration);
		map.put("vehicle_model", vehicle_model);
		map.put("number_of_vehicles", number_of_vehicles);
		map.put("birthdate", birth_date);
		map.put("phone", phone);
		map.put("mail", mail);
		map.put("address", address);
		
		
		System.out.println(map);
		

		/*JSONObject jsonObj = new JSONObject(request.getParameter("jsonObj"));

		String name = (String) request.getParameter("name");
		
		System.out.println(request.getParameter("name"));
		map.put("name", name);
		
		System.out.println(map.get(name));
		*/
		
		
		
		ProcessInstance processInstance;
		
		processInstance = runtimeService.startProcessInstanceByMessage("instantiationMessageContract", map);
		String prozessid = processInstance.getId();
		runtimeService.setVariable(prozessid, "prozessid", prozessid);
		neueprozessid = prozessid;
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Connection connection = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection(Customize.databasepath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			String insertStatement = "INSERT INTO Instance(Bvis_Id,Process_Id) VALUES('"
					+ customer_id + "','" + prozessid + "')";
			PreparedStatement ps = connection.prepareStatement(insertStatement);
			ps.executeUpdate();

				
			
		}

		catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
		
		/*try {
			wait(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		OfferEntity offerhalf = (OfferEntity) runtimeService.getVariable(prozessid, "offerhalf");
		OfferEntity offerfull = (OfferEntity) runtimeService.getVariable(prozessid, "offerfull");
		
		ArrayList <OfferEntity> offers =new ArrayList<OfferEntity>();
		
		offers.add(offerhalf);
		offers.add(offerfull);
			
		
		JSONObject offerss = new JSONObject();
		
		offerss.put("Offers", offers);
		
		
		response.setContentType("application/json");
		out.print(new JSONArray(offers));
		System.out.println("Contract offers: " + offers);
		
		
		out.close();
	}
	
	public String getprozessid() {
		return neueprozessid;
	}

}