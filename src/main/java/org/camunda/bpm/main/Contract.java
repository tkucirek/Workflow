package org.camunda.bpm.main;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.entities.ContractEntity;
import org.camunda.bpm.engine.cdi.jsf.TaskForm;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.Map;

public class Contract {
	  private EntityManager entityManager;
	  
	public void PersistContract (DelegateExecution test) {
		 // Create new order instance
	    ContractEntity contractEntity = new ContractEntity();

	    // Get all process variables
	    Map<String, Object> variables = test.getVariables();

	    // Set order attributes
	    contractEntity.setCustomer((String) variables.get("customer"));
	    contractEntity.setAddress((String) variables.get("address"));
	    contractEntity.setPizza((String) variables.get("pizza"));

	    // Persist order instance and flush. After the flush the
	    // id of the order instance is set.
	    entityManager.persist(contractEntity);
	    entityManager.flush();

	    // Remove no longer needed process variables
	    test.removeVariables(variables.keySet());

	    // Add newly created order id as process variable
	    test.setVariable("orderId", contractEntity.getId());
	}

	public void CheckInformation (DelegateExecution test) {
		
	}
	public void CompareToDatabase (DelegateExecution test) {
		
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