package org.camunda.bpm.UserTask;

import org.camunda.bpm.engine.cdi.BusinessProcess;
import org.camunda.bpm.engine.cdi.jsf.TaskForm;
import org.camunda.bpm.entities.*;
import org.camunda.bpm.main.Contract;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.Serializable;

/*
 * This class is used to handle the taskforms in the contract Process
 */

@Named
@ConversationScoped
public class ContractController implements Serializable {
	private static final long serialVersionUID = 1L;

	// Inject the BusinessProcess to access the process variables
	@Inject
	private BusinessProcess businessProcess;

	// Inject the EntityManager to access the persisted order
	@PersistenceContext
	private EntityManager entityManager;

	// Inject the ContractHandler to update the persisted order
	@Inject
	private Contract contract;

	// Caches the ContractEntity during the conversation
	private ContractEntity contractEntity;

	// Caches the CustomerEntity during the conversation
	private CustomerEntity customerEntity;
	private PrivateCustomer privateCustomer;

	public ContractEntity getContractEntity() {
		if (contractEntity == null) {
			// Load the Contractentity from the database if not already cached
			contractEntity = contract.getContract((Long) businessProcess.getVariable("orderId"));
		}
		return contractEntity;
	}

	public CustomerEntity getCustomerEntity() {
		if (customerEntity == null) {
			// Load the customer entity from the database if not already cached
			customerEntity = contract.getCustomer(
					contract.getContract((Long) businessProcess.getVariable("orderId")).getCustomerId());
		}
		return customerEntity;
	}

	public void submitForm() throws IOException {
		// Persist updated contractentity and complete task form
		contract.mergeOrderAndCompleteTask(contractEntity);
	}

}
