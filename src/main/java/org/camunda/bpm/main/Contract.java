package org.camunda.bpm.main;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.entities.BusinessCustomer;
import org.camunda.bpm.entities.ContractEntity;
import org.camunda.bpm.entities.CustomerEntity;
import org.camunda.bpm.entities.PrivateCustomer;
import org.camunda.bpm.engine.cdi.jsf.TaskForm;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class Contract {
	  private EntityManager entityManager;
	  private ContractEntity contractEntity;
	  private CustomerEntity customerEntity;
	  private Integer customerIsPrivate;
	  private Long BvisId;
	  
	public void PersistContract (DelegateExecution test) {
		
	    Map<String, Object> variables = test.getVariables();// Get all process variables
	    BvisId=(Long) variables.get("bvisProcessId"); // Get Bvis process Id
	    
	    // Create customer and set order attributes for customer
	    if (variables.get("Customer_type").equals("private")) {
			customerIsPrivate=1;
			this.setCustomerEntity(new PrivateCustomer());
			((PrivateCustomer) customerEntity).setName((String) variables.get("name"));
			((PrivateCustomer) customerEntity).setDateOfBirth((String) variables.get("birthdate"));
		} else {
			customerIsPrivate=0;
			this.setCustomerEntity(new BusinessCustomer());
			((BusinessCustomer) customerEntity).setName((String) variables.get("firstName"));
		}
	    
	    // Set order attributes for contract
	    contractEntity.setCustomerId(customerEntity.getId());
	    contractEntity.setDuration((Long)variables.get("rental_duration"));
	    contractEntity.setVehicle_model((Long) variables.get("vehicle_model"));
	    contractEntity.setNumber_of_vehicles((Long) variables.get("number_of_vehicles"));
	    
	    
	    // Persist order instance and flush. After the flush the
	    // id of the order instance is set.
	    entityManager.persist(customerEntity);
	    entityManager.persist(contractEntity);
	    entityManager.flush();

	    // Remove no longer needed process variables
	    test.removeVariables(variables.keySet());

	    // Add newly created order id as process variable
	    test.setVariable("orderId", contractEntity.getId());
		test.setVariable("BvisId", BvisId);
		test.setVariable("contractEntity", contractEntity);
		test.setVariable("customerEntity", customerEntity);
	}

	public void setCustomerEntity(CustomerEntity customerEntity) {
		this.customerEntity = customerEntity;
	}

	public void CompareToDatabase (DelegateExecution test) throws ClassNotFoundException {
		System.out.println("User is now compared to the database");

		Map<String, Object> variables = test.getVariables();
		boolean customerExists = false;

		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/Felix Laptop/git/Workflow/Datenbank.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); 
			

			// Private Customers' uniqueness is checked via driverLicenseId
			if (customerIsPrivate==1) {
				ResultSet rs_private = statement.executeQuery("SELECT * from PrivateCustomer;");
				while (rs_private.next()) {
					System.out.println(
							"Check whether customer with name: " + rs_private.getString("Name") + "exists.");
					if (((PrivateCustomer) customerEntity).getName().equals(rs_private.getString("Name"))) {
						System.out.println("Customer already exists in database");
						customerExists = true;
						
					}
				}
			}
			// Business Customers' uniqueness is checked via their name
			// (assumption: two companies do not have the same name)
			else {
				ResultSet rs_business = statement.executeQuery("SELECT * from BusinessCustomer;");
				while (rs_business.next()) {
					System.out.println("Check whether customer with name: " + rs_business.getString("Name") + " exists.");
					if (((BusinessCustomer) customerEntity).getName().equals(rs_business.getString("Name"))) {
						System.out.println("Customer already exists in database");
						customerExists = true;
					}
				}

			}

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

		test.setVariable("customerExists", customerExists);
		test.setVariable("isPrivate", customerIsPrivate);
	}

	public void CreateNewEntry (DelegateExecution test) {
	
}
	public void UpdateDatabase (DelegateExecution test) {
		
	}
	public void EvaluateCustomer (DelegateExecution test) {
		
	}
	public void CalculateFees (DelegateExecution test) {
		
	}
	public void SendOffering (DelegateExecution test) {
		
	}
	
	public void RecordContract (DelegateExecution test) {
	
}
}