package org.camunda.bpm.main;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.entities.BusinessCustomer;
import org.camunda.bpm.entities.ContractEntity;
import org.camunda.bpm.entities.CustomerEntity;
import org.camunda.bpm.entities.PrivateCustomer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

	public void persistContract(DelegateExecution test) {

		Map<String, Object> variables = test.getVariables();// Get all process variables

		// Create customer and set order attributes for customer
		if (variables.get("Customer_type").equals("private")) {
			customerIsPrivate = 1;
			this.setCustomerEntity(new PrivateCustomer());
			((PrivateCustomer) customerEntity).setName((String) variables.get("name"));
			((PrivateCustomer) customerEntity).setDateOfBirth((String) variables.get("birthdate"));
		} else {
			customerIsPrivate = 0;
			this.setCustomerEntity(new BusinessCustomer());
			((BusinessCustomer) customerEntity).setName((String) variables.get("name"));
		}
		BvisId = Long.valueOf((String) variables.get("bvisProcessId"));
		// Set order attributes for contract
		this.setContractEntity(new ContractEntity());
		contractEntity.setCustomerId(customerEntity.getId());
		contractEntity.setDuration(Long.valueOf((String) variables.get("rental_duration")));
		contractEntity.setVehicle_model(Long.valueOf((String) variables.get("vehicle_model")));
		contractEntity.setNumber_of_vehicles(Long.valueOf((String) variables.get("number_of_vehicles")));

		// Persist order instance and flush. After the flush the
		// id of the order instance is set.
		entityManager.persist(customerEntity);
		entityManager.persist(contractEntity);
		entityManager.flush();

		// Remove no longer needed process variables
		test.removeVariables(variables.keySet());

		// Add newly created order id as process variable
		test.setVariable("rental_duration", contractEntity.getDuration());
		test.setVariable("vehicle_model", contractEntity.getVehicle_model());
		test.setVariable("number_of_vehicles", contractEntity.getNumber_of_vehicles());
		test.setVariable("contractId", contractEntity.getId());
		test.setVariable("BvisId", BvisId);
		test.setVariable("contractEntity", contractEntity);
		test.setVariable("customerEntity", customerEntity);
	}

	public void setCustomerEntity(CustomerEntity customerEntity) {
		this.customerEntity = customerEntity;
	}

	public CustomerEntity getCustomer(Long customerId) {
		// Load customer entity from database
		if (customerIsPrivate == 1) {
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

	public void compareToDatabase(DelegateExecution test) throws ClassNotFoundException {
		System.out.println("User is now compared to the database");

		Map<String, Object> variables = test.getVariables();
		boolean customerExists = false;

		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/Felix Laptop/git/Workflow/Datenbank2.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			ResultSet result = statement.executeQuery("SELECT * from Customer;");
			while (result.next()) {
				System.out.println("Check whether customer with name: " + result.getString("Name") + "exists.");
				if ((customerEntity).getName().equals(result.getString("Name"))) {
					System.out.println("Customer already exists in database");
					customerExists = true;
				}
			}
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

		test.setVariable("isPrivate", customerIsPrivate);
		test.setVariable("customerExists", customerExists);
	}

	public void createNewEntry(DelegateExecution test) throws ClassNotFoundException {
		System.out.println("Creating new user entry");

		Map<String, Object> variables = test.getVariables();
		boolean customerExists = false;

		Class.forName("org.sqlite.JDBC");
		Connection connection = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/Felix Laptop/git/Workflow/Datenbank2.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			String dbname = (customerEntity).getName();
			int dbNumberOfClaims = 0;

			// set the information from the customerEntity

			// insert values into the database
			String insertStatement = "INSERT INTO Customer(Bvis_Id,Name,Number_Of_Claims,Private_Customer) VALUES('"
					+ BvisId + "','" + dbname + "','" + dbNumberOfClaims + "','" + customerIsPrivate + "')";
			PreparedStatement ps = connection.prepareStatement(insertStatement);
			ps.executeUpdate();

			// get the id of the just created customer (largest Id because of
			// auto increment)
			ResultSet rs_current = statement.executeQuery(
					"SELECT Bvis_Id FROM Customer WHERE Capitol_Id = (SELECT MAX(Capitol_Id) FROM Customer)");
			int dbCustomerId = rs_current.getInt("Bvis_Id");
			if (customerIsPrivate == 1) {
				String dbbirthday = ((PrivateCustomer) customerEntity).getDateOfBirth();

				String insertStatement2 = "INSERT INTO PrivateCustomer(Bvis_Id,Birthday,Name) VALUES('" + dbCustomerId
						+ "','" + dbbirthday + "','" + dbname + "')";
				PreparedStatement ps2 = connection.prepareStatement(insertStatement2);
				ps2.executeUpdate();
			} else if (customerIsPrivate == 0) {
				String insertStatement3 = "INSERT INTO FirmCustomer(Bvis_Id,Company_Name) VALUES('" + dbCustomerId
						+ "','" + dbname + "')";
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

		// System.out.println("The current contractId is " + dbContractId + ".");
	}

	public void evaluateCustomer(DelegateExecution test) throws ParseException {
		String insuranceClass = null;
		int age;
		if (customerIsPrivate == 1) {

			SimpleDateFormat myFormatter = new SimpleDateFormat("dd-MM-yyyy");
			Calendar dateToday = Calendar.getInstance();
			Calendar dateBirth = Calendar.getInstance();
			int todayDayOfYear = dateToday.get(Calendar.DAY_OF_YEAR);
			int birthDateDayOfYear = dateBirth.get(Calendar.DAY_OF_YEAR);
			int todayMonth = dateToday.get(Calendar.MONTH);
			int birthDateMonth = dateBirth.get(Calendar.MONTH);
			int todayDayOfMonth = dateToday.get(Calendar.DAY_OF_MONTH);
			int birthDateDayOfMonth = dateBirth.get(Calendar.DAY_OF_MONTH);
			dateBirth.setTime(myFormatter.parse(((PrivateCustomer) customerEntity).getDateOfBirth()));
			age = dateToday.get(Calendar.YEAR) - dateBirth.get(Calendar.YEAR);

			// If birth date is greater than todays date (after 2 days adjustment of leap
			// year) then decrement age one year
			if ((birthDateDayOfYear - todayDayOfYear > 3) || (birthDateMonth > todayMonth)) {
				age--;
			}
			// If birth date and todays date are of same month and birth day of month is
			// greater than todays day of month then decrement age
			else if ((birthDateMonth == todayMonth) && (birthDateDayOfMonth > todayDayOfMonth)) {
				age--;
			}

			System.out.println("Age of the private customer is " + age);

			if (age >= 18 & age < 25) {
				insuranceClass = "A";
			}

			else if (age >= 25 & age < 65) {
				insuranceClass = "B";
			}

			else if (age >= 65) {
				insuranceClass = "C";
			}
		}
		else if (customerIsPrivate==0){
		insuranceClass = "Business";
		}
		test.setVariable("insurance class", insuranceClass);
	}

	public void calculateFees(DelegateExecution test) {

	}

	public void sendOffering(DelegateExecution test) {

	}

	public void recordContract(DelegateExecution test) {

	}
}