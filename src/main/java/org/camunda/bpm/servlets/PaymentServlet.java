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
import org.camunda.bpm.main.Databasepath;


/**
 * @author Capitol Recieves the payment and concludes our process.
 */
public class PaymentServlet extends HttpServlet{
	
	String process_Id;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		PrintWriter out = response.getWriter();
		
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		String jsonString = IOUtils.toString(request.getInputStream());
		
		JSONObject json = new JSONObject(jsonString); 
		
		String customer_id = String.valueOf(json.getString("customer_id")) ;
		double money = json.getDouble("money");
		System.out.println("Recieved " + money + "€");
		
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
				 System.out.println("PaymentProcessId: " + process_Id);
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
		
		
		
		
		runtimeService.setVariable(process_Id, "money", money);
		
		runtimeService.createMessageCorrelation("paymentRecieved").processInstanceId(process_Id).correlateWithResult();
		System.out.println("Please manually confirm the payment");

	}
}
