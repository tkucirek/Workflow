package org.camunda.bpm.main;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.entities.BusinessCustomer;
import org.camunda.bpm.entities.ContractEntity;
import org.camunda.bpm.entities.CustomerEntity;
import org.camunda.bpm.entities.PrivateCustomer;
import org.camunda.bpm.engine.cdi.jsf.TaskForm;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.cdi.jsf.TaskForm;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

@Stateless
@Named
public class Contract {
	@PersistenceContext
	  private EntityManager entityManager;
	@Inject
	  private TaskForm taskForm;
	
	  private ContractEntity contractEntity;
	  private CustomerEntity customerEntity;
	  private Integer customerIsPrivate;
	  private Long BvisId;
	  
	  public void mergeOrderAndCompleteTask(ContractEntity contractEntity) {
			// Merge detached order entity with current persisted state
			entityManager.merge(contractEntity);
			try {
				// Complete user task from
				taskForm.completeTask();
			} catch (IOException e) {
				// Rollback both transactions on error
				throw new RuntimeException("Cannot complete task", e);
			}
		}
	  
	public void persistContract (DelegateExecution test) {
		
	    Map<String, Object> variables = test.getVariables();// Get all process variables
	    //BvisId=(Long) variables.get("bvisProcessId"); // Get Bvis process Id
	   
	    // Create customer and set order attributes for customer
	    if (variables.get("Customer_type").equals("private")) {
			customerIsPrivate=1;
			this.setCustomerEntity(new PrivateCustomer());
			((PrivateCustomer) customerEntity).setName((String) variables.get("name"));
			((PrivateCustomer) customerEntity).setDateOfBirth((String) variables.get("birthdate"));
		} else {
			customerIsPrivate=0;
			this.setCustomerEntity(new BusinessCustomer());
			((BusinessCustomer) customerEntity).setName((String) variables.get("name"));
		}
	    
	    // Set order attributes for contract
	    this.setContractEntity(new ContractEntity());
	    contractEntity.setCustomerId(customerEntity.getId());
	    //contractEntity.setDuration((Long)variables.get("rental_duration"));
	    //contractEntity.setVehicle_model((Long) variables.get("vehicle_model"));
	    //contractEntity.setNumber_of_vehicles((Long) variables.get("number_of_vehicles"));
	    
	    
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
	
	public CustomerEntity getCustomer(Long customerId) {
		// Load customer entity from database
		if (customerIsPrivate==1) {
			return entityManager.find(PrivateCustomer.class, customerId);
		} else
			return entityManager.find(BusinessCustomer.class, customerId);
	}
	
	public void setContractEntity(ContractEntity contractEntity) {
		this.contractEntity = contractEntity;
	}
	
	public ContractEntity getContract(Long contractId) {
		// Load contract entity from database
		return entityManager.find(ContractEntity.class, contractId);
	}


	public void compareToDatabase (DelegateExecution test) throws ClassNotFoundException {
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

		
		test.setVariable("isPrivate", customerIsPrivate);
		test.setVariable("customerExists", customerExists);
	}

	public void createNewEntry (DelegateExecution test) throws ClassNotFoundException {
		System.out.println("Creating new user entry");

		Map<String, Object> variables = test.getVariables();
		boolean customerExists = false;

		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/Felix Laptop/git/Workflow/Datenbank.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); 

			String dbname = (customerEntity).getName();
			int dbNumberOfClaims = 0;
			int dbIsPrivate = 1; // only privateCustomers are inserted here
			
			//set the information from the customerEntity
			

			//insert values into the database
			String insertStatement = "INSERT INTO Customer(name,numberOfClaims,business_customer) VALUES('" + dbname
					+ "','" + dbNumberOfClaims + "','" + dbIsPrivate + "')";
			PreparedStatement ps = connection.prepareStatement(insertStatement);
			ps.executeUpdate();

			// get the id of the just created customer (largest Id because of
			// auto increment)
			ResultSet rs_current = statement.executeQuery(
					"SELECT Id FROM Customer WHERE Id = (SELECT MAX(Id) FROM Customer)");
			int dbCustomerId = rs_current.getInt("Id");
			System.out.println("zweite");
			if (customerIsPrivate==1) {
				String dbbirthday = ((PrivateCustomer) customerEntity).getDateOfBirth();
			
			String insertStatement2 = "INSERT INTO PrivateCustomer(Id,Birthday,Name) VALUES('"
					+ dbCustomerId + "','" + dbbirthday + "','" + dbname + "')";
			PreparedStatement ps2 = connection.prepareStatement(insertStatement2);
			ps2.executeUpdate();
			}
			else {
				String insertStatement3 = "INSERT INTO Businesscustomer(Id,Company name) VALUES('"
						+ dbCustomerId +"','" + dbbirthday + "','" + dbname + "')";
				PreparedStatement ps3 = connection.prepareStatement(insertStatement3);
				ps3.executeUpdate();
				
			}

			System.out.println("Customer entry has been created.");

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

		
		//System.out.println("The current contractId is " + dbContractId + ".");
}
	public void updateDatabase (DelegateExecution test) {
		
	}
	public void evaluateCustomer (DelegateExecution test) {
		
	}
	public void calculateFees (DelegateExecution test) {
		
	}
	public void sendOffering (DelegateExecution test) {
		
	}
	
	public void recordContract (DelegateExecution test) {
	
}
}