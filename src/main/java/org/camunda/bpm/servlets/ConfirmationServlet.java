package org.camunda.bpm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.camunda.bpm.main.Customize;

public class ConfirmationServlet extends HttpServlet {
	int finalPrice;
	String offertype;
	String process_Id;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		PrintWriter out = response.getWriter();
		
		long finalPrice = 0;
		

		Map<String, Object> map = new HashMap<String, Object>();
		
		
		String jsonString = IOUtils.toString(request.getInputStream());

		JSONObject json = new JSONObject(jsonString); 
		
		String customer_id = String.valueOf(json.getString("customer_id")) ;
		System.out.println("id ist:"+customer_id);
		String offer_id = String.valueOf(json.getLong("offer_id"));
		
		//map.put("customer_id", customer_id);
		//map.put("offer_id", offer_id);
		
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
			System.out.println("before crash");
			ResultSet result = statement.executeQuery("SELECT Process_Id from Instance WHERE Bvis_Id='"+customer_id+"'");
			System.out.println("after crash");
				 process_Id=result.getString("Process_Id");
				 System.out.println(process_Id);
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
		
		if(json.getLong("offer_id")== 2) {
			finalPrice = (long)runtimeService.getVariable(process_Id, "fullPrice");
			offertype = "Vollkasko";
			
		}else if (json.getLong("offer_id") == 1) {
			finalPrice = (long) runtimeService.getVariable(process_Id, "semiPrice");
			offertype = "Halbkasko";
		}
		map.put("customer_id", customer_id);
		map.put("offer_id", offer_id);
		map.put("finalPrice", finalPrice);
		map.put("finalOfferType", offertype);
		
		runtimeService.createMessageCorrelation("contractConfirmed").processInstanceId(process_Id).setVariables(map)
		.correlateWithResult();

		
		
	
		
		
		
	
		
		
	
	}
}
