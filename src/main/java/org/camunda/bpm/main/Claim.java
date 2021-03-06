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
		
		/**
		 * Persists the claim instance
		 * @param test
		 */
	public void persistClaim (DelegateExecution test) {
		// Get all process variables
		Map<String, Object> variables = test.getVariables();
				
				
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
		
		System.out.println("The customer ID is: " + claimEntity.getCustomerId());
		System.out.println("The damage classification is: " + claimEntity.getDamageClassification());
		
	}
	
	public ClaimEntity getClaimEntity() {
		return claimEntity;
	}

	public void setClaimEntity(ClaimEntity claimEntity) {
		this.claimEntity = claimEntity;
	}
	
	/**
	 * Creates the claim as a ClaimEntity
	 * @param variables
	 */
	public void createClaim(Map<String, Object> variables) {
		this.setClaimEntity(new ClaimEntity());

		claimEntity.setCustomerId((String) variables.get("customer_id"));
		claimEntity.setDamageClassification(Long.valueOf((String) variables.get("damage_classification")));
		
	}
	
	/**
	 * Evaluates the damages and calculates the damage amount
	 * @param test
	 * @throws ClassNotFoundException
	 */
	public void checkDamage (DelegateExecution test) throws ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try {
			connection = DriverManager.getConnection(Databasepath.databasepath);
			System.out.println("Databasepath : " + Databasepath.databasepath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			
			
			
			ResultSet resultmodel = statement.executeQuery("SELECT Model FROM Contract WHERE Bvis_Id = '" 
					+ claimEntity.getCustomerId() + "'");
			int model1 = resultmodel.getInt(1);
			System.out.println("The rented model is of type " + model1);
			ResultSet resultnumber = statement.executeQuery("SELECT Number_Of_Vehicles FROM Contract WHERE Bvis_Id = '" 
					+ claimEntity.getCustomerId() + "'");
			
			int number1 = resultnumber.getInt(1);
			
			damageamount = (model1 * 100) * (claimEntity.getDamageClassification() * 5);
			
			System.out.println("The number of rented vehicles is " + number1);
			
			System.out.println("The damage amount is: " + damageamount);
			

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
		
		test.setVariable("damage_Amount", damageamount);
	}

	/**
	 * Crosschecks the contractid of the claim with our contract database to determine the coverage
	 * @param test
	 * @throws ClassNotFoundException
	 */
	public void CrossCheckWithContract (DelegateExecution test) throws ClassNotFoundException {
		
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		
		String customer_id = claimEntity.getCustomerId();
		
		try {
			// create a database connection
			connection = DriverManager.getConnection(Databasepath.databasepath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			ResultSet result = statement.executeQuery("SELECT Coverage from Contract WHERE Bvis_Id= '" + customer_id + "'");
			
			String coverage = result.getString("Coverage");
			
			System.out.println("The coverage for the customer with the ID: " + customer_id + "is " + coverage);
			
			test.setVariable("coverage", coverage);
			
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
		
		
	
	}
	
	/**
	 * Determines the deductible amount that the customer has to pay
	 * @param test
	 */
	public void DetermineDeductibleAmount (DelegateExecution test) {
		
		
		String coverage = (String) test.getVariable("coverage");
		
		long damage_Assessment;
		String customer_id = claimEntity.getCustomerId();
		
		damage_Assessment = (long) test.getVariable("damage_Amount");
		long deductible_Amount;
		if (coverage.equals("Semi coverage") ) {
			deductible_Amount = (long) test.getVariable("damage_Amount") /2;
		}else {
			deductible_Amount = 0;
		}
		
		Connection connection = null;
		
		try {
			// create a database connection
			connection = DriverManager.getConnection(Databasepath.databasepath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

				
			
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
		
		System.out.println("The damage assessment is: " + damage_Assessment);
		
		test.setVariable("deductible_Amount", deductible_Amount);
		System.out.println("The deductible amount is: " + deductible_Amount);
		
	}

	/**
	 * Adjusts the number of claims for the customer
	 * @param test
	 * @throws ClassNotFoundException
	 */
	public void AdjustCustomer (DelegateExecution test) throws ClassNotFoundException {
		
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		
		String customer_id = claimEntity.getCustomerId();
		
		try {
			// create a database connection
			connection = DriverManager.getConnection(Databasepath.databasepath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			ResultSet result = statement.executeQuery("SELECT Number_Of_Claims from Customer WHERE Bvis_Id= '" + customer_id + "'");
			
			int claimNumber = result.getInt("Number_Of_Claims");
			System.out.println("The old number of claims: " + claimNumber);
			claimNumber = claimNumber +1;
			System.out.println("The new number of claims: " + claimNumber);
			String insertStatement = "UPDATE Customer SET Number_Of_Claims ="+ claimNumber +" WHERE Bvis_Id= '" + customer_id + "'";
			System.out.println("Number of claims has been updated");
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
		}
	
	
	/**
	 * Records the claim in our database.
	 * @param test
	 * @throws ClassNotFoundException
	 */
	public void recordClaim (DelegateExecution test) throws ClassNotFoundException {
		
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection(Databasepath.databasepath);
			System.out.println("Databasepath : " + Databasepath.databasepath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			
			String insertStatement = "INSERT INTO Claim(Damage_Amount, Bvis_Id, Deductible_Amount) VALUES('"
					+ test.getVariable("damage_Amount") + "','" + claimEntity.getCustomerId() + "','" + test.getVariable("deductible_Amount") +"')";
			System.out.println("ID: " + claimEntity.getCustomerId());
			PreparedStatement ps = connection.prepareStatement(insertStatement);
			ps.executeUpdate();
		
			
			
		
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
		
		System.out.println("The claim has been recorded");
		
		
	}
}
