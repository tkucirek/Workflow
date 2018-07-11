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
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.main.Databasepath;


/**
 * @author Capitol Recieves the signal, that the contract has ended and responds with the corresponding payment information.
 */
public class ContractEndServlet extends HttpServlet{
	
	String process_Id;
	long damage_Amount;
	int claim_Fee;
	int final_Payment;
	int deductible_Amount;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		PrintWriter out = response.getWriter();
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		String jsonString = IOUtils.toString(request.getInputStream());
		
		JSONObject json = new JSONObject(jsonString); 
		
		String customer_id = String.valueOf(json.getString("customer_id")) ;
	
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
				 System.out.println("ProcessId : " + process_Id);
				 
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
		
		map.put("process_id", process_Id);
		
		runtimeService.createMessageCorrelation("contractEnds").processInstanceId(process_Id).setVariables(map).correlateWithResult();
		
		System.out.println("The Contract with the ID " + process_Id + " has expired");
		
		
		JSONObject contractend = new JSONObject();
		
		Connection connection2 = null;
		try {
			// create a database connection
			connection2 = DriverManager.getConnection(Databasepath.databasepath);
			Statement statement = connection2.createStatement();
			statement.setQueryTimeout(30);
			ResultSet result = statement.executeQuery("SELECT SUM(Damage_Amount) from Claim WHERE Bvis_Id='"+customer_id+"'");
				 damage_Amount = result.getLong("sum(Damage_Amount)");
				 
			ResultSet result3 = statement.executeQuery("SELECT SUM(Deductible_Amount) from Claim WHERE Bvis_Id='"+customer_id+"'");
				 deductible_Amount = (int) result3.getLong("sum(Deductible_Amount)");
				  
				 
			ResultSet result2 = statement.executeQuery("SELECT Number_Of_Claims from Customer WHERE Bvis_Id='"+customer_id+"'");
				 claim_Fee = (result2.getInt("Number_Of_Claims")*100);
				 final_Payment = (int) (deductible_Amount + claim_Fee );
				 
				 
				 
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
		
		
		
		
		
		contractend.put("final_Payment", final_Payment);
		contractend.put("claim_Fee", claim_Fee);
		contractend.put("damage_Amount", damage_Amount);
		
		
		
		response.setContentType("application/json");
		out.print(contractend);
		System.out.println("Final Payment: " + final_Payment + " Claim Fee: " + claim_Fee + " Damageamount: " + damage_Amount);
	}
	
	
}
