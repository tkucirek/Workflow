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
import org.camunda.bpm.main.Databasepath;


/**
 * @author Capitol Recieves the confirmation for the chosen contract and saves the determined price.
 */
public class ConfirmationServlet extends HttpServlet {
	int finalPrice;
	String offertype;
	String process_Id;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		PrintWriter out = response.getWriter();
		
		long finalPriceperday = 0;
		

		Map<String, Object> map = new HashMap<String, Object>();
		
		
		String jsonString = IOUtils.toString(request.getInputStream());

		JSONObject json = new JSONObject(jsonString); 
		
		String customer_id = String.valueOf(json.getString("customer_id")) ;
		System.out.println("Confirmed CustomerID: " + customer_id);
		String offer_id = String.valueOf(json.getLong("offer_id"));
		System.out.println("Confirmed OfferId: " + offer_id);
		
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
			connection = DriverManager.getConnection(Databasepath.databasepath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet result = statement.executeQuery("SELECT Process_Id from Instance WHERE Bvis_Id='"+customer_id+"'");
				 process_Id=result.getString("Process_Id");
				 System.out.println("Confirmed ProcessId: " + process_Id);
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
			finalPriceperday = (long)runtimeService.getVariable(process_Id, "fullPrice");
			offertype = "Full coverage";
			
		}else if (json.getLong("offer_id") == 1) {
			finalPriceperday = (long) runtimeService.getVariable(process_Id, "semiPrice");
			offertype = "Semi coverage";
		}
		
		double duration = (double) runtimeService.getVariable(process_Id, "rental_duration");
		long number_of_vehicles = (long) runtimeService.getVariable(process_Id, "number_of_vehicles");
		
		long finalprice = (long) (finalPriceperday * duration * number_of_vehicles);
		
		map.put("customer_id", customer_id);
		map.put("offer_id", offer_id);
		map.put("finalPriceperday", finalPriceperday);
		map.put("finalprice", finalprice);
		map.put("finalOfferType", offertype);
		System.out.println("The chosen and confirmed Insurancetype is: " + offertype);
		
		runtimeService.createMessageCorrelation("contractConfirmed").processInstanceId(process_Id).setVariables(map)
		.correlateWithResult();

		
	
	}
}
