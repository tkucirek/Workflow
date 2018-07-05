package org.camunda.bpm.main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.camunda.bpm.engine.cdi.jsf.TaskForm;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.entities.ClaimEntity;
//import org.camunda.bpm.handler.Customize;


@Stateless
@Named
public class Claim {
	
	// Inject the entity manager
		@PersistenceContext
		private EntityManager entityManager; 

		// Inject task form available through the Camunda cdi artifact
		@Inject
		private TaskForm taskForm;

		private ClaimEntity claimEntity;
		long damageamount;
		
		
		public void mergeOrderAndCompleteTask(ClaimEntity claimEntity) {
			entityManager.merge(claimEntity);
			try {
				// Complete user task from

				taskForm.completeTask();

			} catch (IOException e) {
				// Rollback both transactions on error
				throw new RuntimeException("Cannot complete task", e);
			}

		}

	public void persistClaim (DelegateExecution test) {
		// Get all process variables
				Map<String, Object> variables = test.getVariables();
				//int customerId = (int) variables.get("customer_Id");
				
		createClaim(variables);
		
		/*
		 * Persist order instance and flush. After the flush the id of the order
		 * instance is set.
		 */
		entityManager.persist(claimEntity);
		entityManager.flush();
		
		
		test.removeVariables(variables.keySet());
		
		test.setVariable("claimEntity", claimEntity);
		test.setVariable("customer_Id", claimEntity.getCustomerId());
		test.setVariable("damage_classification", claimEntity.getDamageClassification());
		
	}
	
	public ClaimEntity getClaimEntity() {
		return claimEntity;
	}

	public void setClaimEntity(ClaimEntity claimEntity) {
		this.claimEntity = claimEntity;
	}
	
	/**
	 * Creates the claim entity
	 * @param variables
	 */
	@SuppressWarnings("unchecked")
	public void createClaim(Map<String, Object> variables) {
		// Create new contract instance
		this.setClaimEntity(new ClaimEntity());

		claimEntity.setCustomerId(Long.valueOf((String) variables.get("customer_id")));
		claimEntity.setDamageClassification(Long.valueOf((String) variables.get("damage_classification")));
		
	}
	
	public void checkDamage (DelegateExecution test) throws ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try {
			System.out.println("Databasepath: " + Customize.databasepath);
			// create a database connection
			connection = DriverManager.getConnection(Customize.databasepath);
			System.out.println("Databasepath : " + Customize.databasepath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			
			// set the transmitted information about the claim
			long dbDamageClassification = claimEntity.getDamageClassification();
			long dbCustomerId = claimEntity.getCustomerId();
			
			ResultSet resultmodel = statement.executeQuery("SELECT Model FROM Contract WHERE Bvis_Id = " 
					+ claimEntity.getCustomerId());
			int model1 = resultmodel.getInt(1);
			System.out.println(resultmodel.getInt(1));
			System.out.println("mode l" + model1);
			ResultSet resultnumber = statement.executeQuery("SELECT Number_Of_Vehicles FROM Contract WHERE Bvis_Id = " 
					+ claimEntity.getCustomerId());
			
			int number1 = resultnumber.getInt(1);
			
			damageamount = (model1 * 100) * number1 * (claimEntity.getDamageClassification() * 5);
			
			System.out.println("customerid " + claimEntity.getCustomerId());
			System.out.println("class " + claimEntity.getDamageClassification()*5);
			System.out.println("number " + number1);
			
			System.out.println("damageamount " +damageamount);
			
			// write database entry
			String insertStatement = "INSERT INTO Claim(Damage_Amount, Bvis_Id) VALUES('"
					+ damageamount + "','" + claimEntity.getCustomerId() + "')";
			System.out.println(claimEntity.getCustomerId());
			PreparedStatement ps = connection.prepareStatement(insertStatement);
			ps.executeUpdate();

			System.out.println("The claim should now be recorded in capitol.db");

		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
		
		test.setVariable("Damage_Amount", damageamount);
	}

	public void CheckDamage (DelegateExecution test) {
	
}
	public void CheckDatabase (DelegateExecution test) {
		
	}
	public void DetermineDeductibleAmount (DelegateExecution test) {
		
	}
	public void SendDamageAssessment (DelegateExecution test) {
		
	}
	public void SendPayment (DelegateExecution test) {
		
	}
	
	public void AdjustCustomer (DelegateExecution test) {
	
}
}
