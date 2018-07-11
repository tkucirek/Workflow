package org.camunda.bpm.main;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.entities.BusinessCustomer;
import org.camunda.bpm.entities.ContractEntity;
import org.camunda.bpm.entities.CustomerEntity;
import org.camunda.bpm.entities.PrivateCustomer;
import org.camunda.bpm.messages.InsuranceOffering;

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

	public ContractEntity contractEntity;
	private CustomerEntity customerEntity;
	private InsuranceOffering insuranceOffering;
	private Integer customerIsPrivate;
	private String BvisId;
	private String name;
	private String phone;
	private String mail;
	private String address;

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
	
	/**
	 * Persists the contract instance.
	 * @param test
	 */

	public void persistContract(DelegateExecution test) {

		Map<String, Object> variables = test.getVariables();// Get all process variables
		name=(String)variables.get("name");
		phone=(String)variables.get("phone");
		mail=(String)variables.get("mail");
		address=(String)variables.get("address");
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
		BvisId = (String) variables.get("BvisId");
		// Set order attributes for contract
		createContract(variables,test);
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
		test.setVariable("name", name);
		
	}

	public void setCustomerEntity(CustomerEntity customerEntity) {
		this.customerEntity = customerEntity;
	}

	public CustomerEntity getCustomer(String customerId) {
		if (customerIsPrivate == 1) {
			return entityManager.find(PrivateCustomer.class, customerId);
		} else
			return entityManager.find(BusinessCustomer.class, customerId);
	}

	public void setContractEntity(ContractEntity contractEntity) {
		this.contractEntity = contractEntity;
	}

	public ContractEntity getContract(Long contractId) {
		return entityManager.find(ContractEntity.class, contractId);
	}
	public ContractEntity getContractEntity() {

		return this.contractEntity;
	}
	
	/**
	 * Creates the contract as a ContractEntity.
	 * @param variables
	 */
	public void createContract(Map<String, Object> variables, DelegateExecution test) {
		this.setContractEntity(new ContractEntity());
		contractEntity.setCustomerId(BvisId);
		contractEntity.setCustomerName(name);
		contractEntity.setDuration(Long.valueOf((String) variables.get("rental_duration"))*0.0000000115741);
		contractEntity.setVehicle_model(Long.valueOf((String) variables.get("vehicle_model")));
		contractEntity.setNumber_of_vehicles(Long.valueOf((String) variables.get("number_of_vehicles")));
		
	}

	/**
	 * Checks whether the customer id already exists in our database
	 * @param test
	 * @throws ClassNotFoundException
	 */
	public void compareToDatabase(DelegateExecution test) throws ClassNotFoundException {
		boolean customerExists = false;

		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try {
			connection = DriverManager.getConnection(Databasepath.databasepath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			ResultSet result = statement.executeQuery("SELECT * from Customer;");
			while (result.next()) {
				if (BvisId.equals(result.getString("Bvis_Id"))) {
					System.out.println("Customer already exists in capitol's customer-database");
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

	/**
	 * Creates a new customer entry in the customer database and in the corresponding sub-database (PrivateCustomer or FirmCustomer).
	 * @param test
	 * @throws ClassNotFoundException
	 */
	public void createNewEntry(DelegateExecution test) throws ClassNotFoundException {

		Class.forName("org.sqlite.JDBC");
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(Databasepath.databasepath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			String dbname = (customerEntity).getName();
			int dbNumberOfClaims = 0;
			System.out.println("Creating new customer entry in the customer database");
			String insertStatement = "INSERT INTO Customer(Bvis_Id,Name,Number_Of_Claims,Private_Customer,Address,Phone,Mail) VALUES('"
					+ BvisId + "','" + dbname + "','" + dbNumberOfClaims + "','" + customerIsPrivate + "','" + address + "','"  + phone  +"','" + mail+"')";
			PreparedStatement ps = connection.prepareStatement(insertStatement);
			ps.executeUpdate();

			if (customerIsPrivate == 1) {
				String dbbirthday = ((PrivateCustomer) customerEntity).getDateOfBirth();
				System.out.println("Creating new customer entry in the private-customer database");
				String insertStatement2 = "INSERT INTO PrivateCustomer(Bvis_Id,Birthday,Name) VALUES('" + BvisId
						+ "','" + dbbirthday + "','" + dbname + "')";
				PreparedStatement ps2 = connection.prepareStatement(insertStatement2);
				ps2.executeUpdate();
			} else if (customerIsPrivate == 0) {
				System.out.println("Creating new customer entry in the firm-customer database");
				String insertStatement3 = "INSERT INTO FirmCustomer(Bvis_Id,Company_Name) VALUES('" + BvisId
						+ "','" + dbname + "')";
				PreparedStatement ps3 = connection.prepareStatement(insertStatement3);
				ps3.executeUpdate();

			}

			System.out.println("Customer entries have been added.");

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

	}
	/**
	 * Assigns the private customer to an insurance class based on his age.
	 * Business customers are simply assigned to a business class.
	 * @param test
	 * @throws ParseException
	 */
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
		} else if (customerIsPrivate == 0) {
			insuranceClass = "Business";
		}
		System.out.println("Insurance class of the customer is " + insuranceClass);
		test.setVariable("insurance class", insuranceClass);
	}
	/**
	 * Calculates the daily rental costs for private customers based on their insurance class and the rented car-model.
	 * Business customers pay the same amount for all models.
	 * @param test
	 */

	public void calculatePremium(DelegateExecution test) {
		long price = 0;
		Map<String, Object> variables = test.getVariables();
		String insuranceClass = (String) variables.get("insurance class");
		long model =contractEntity.getVehicle_model();

		if (insuranceClass.equals("A") & model == 1) {
			price = 15;
		}
		if (insuranceClass.equals("A") & model == 2) {
			price = 20;
		}
		if (insuranceClass.equals("A") & model == 3) {
			price = 25;
		}
		if (insuranceClass.equals("A") & model == 4) {
			price = 30;
		}
		if (insuranceClass.equals("A") & model == 5) {
			price = 35;
		}
		if (insuranceClass.equals("B") & model == 1) {
			price = 10;
		}
		if (insuranceClass.equals("B") & model == 2) {
			price = 15;
		}
		if (insuranceClass.equals("B") & model == 3) {
			price = 20;
		}
		if (insuranceClass.equals("B") & model == 4) {
			price = 25;
		}
		if (insuranceClass.equals("B") & model == 5) {
			price = 30;
		}
		if (insuranceClass.equals("C") & model == 1) {
			price = 20;
		}
		if (insuranceClass.equals("C") & model == 2) {
			price = 25;
		}
		if (insuranceClass.equals("C") & model == 3) {
			price = 30;
		}
		if (insuranceClass.equals("C") & model == 4) {
			price = 35;
		}
		if (insuranceClass.equals("C") & model == 5) {
			price = 40;
		} else if (insuranceClass.equals("Business"))
			price = 20; // model does not matter for business customers

		long fullCoveragePrice = price * 3;
		long semiCoveragePrice = price * 2;
		System.out.println("The daily price for model " + model + " with a full coverage is:" + fullCoveragePrice);
		System.out.println("The daily price for model " + model + " with a semi coverage is:" + semiCoveragePrice);
		test.setVariable("fullPrice", fullCoveragePrice);
		test.setVariable("semiPrice", semiCoveragePrice);

	}
	/**
	 * Creates a new insuranceOffering instance and also transmits the delegateExecution parameter.
	 * @param test
	 */
	public void sendInsuranceOffering(DelegateExecution test) {
		insuranceOffering = new InsuranceOffering();

		try {
			insuranceOffering.execute(test);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Records the contract in our database.
	 * @param test
	 * @throws ClassNotFoundException
	 */
	public void recordContract(DelegateExecution test) throws ClassNotFoundException {
		System.out.println("Recording the contract in the contract database.");
		long finalPriceperday=(long)test.getVariable("finalPriceperday");
		String finalOfferType=(String) test.getVariable("finalOfferType");
		double duration =contractEntity.getDuration();
		long model=contractEntity.getVehicle_model();
		long numberOfVehicle =contractEntity.getNumber_of_vehicles();
		Class.forName("org.sqlite.JDBC");
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(Databasepath.databasepath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			String insertStatement = "INSERT INTO Contract(Bvis_Id,Price,Coverage,Duration,Model,Number_Of_Vehicles) VALUES('"
					+ BvisId + "','" + finalPriceperday + "','" + finalOfferType + "','" + duration + "','" + model + "','" + numberOfVehicle+"')";
			PreparedStatement ps = connection.prepareStatement(insertStatement);
			ps.executeUpdate();


			System.out.println("Contract entry has been created.");

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
	}
}