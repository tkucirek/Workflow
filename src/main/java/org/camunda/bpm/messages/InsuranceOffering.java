package org.camunda.bpm.messages;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.entities.ContractEntity;
import org.camunda.bpm.entities.OfferEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

public class InsuranceOffering implements JavaDelegate {
	
	private String jsonInString;
	private String jsonInString2;
	
	public void execute(DelegateExecution test) throws Exception {
	System.out.println("hole bvis id");
	String bvisId =  (String) test.getVariable("BvisId");
	System.out.println("hole contract");
	long fullPrice=(long) test.getVariable("fullPrice");
	long semiPrice=(long) test.getVariable("semiPrice");
	//ContractEntity contract = (ContractEntity) test.getVariable("contractEntity");
	System.out.println("hole name");
	String customerName=((String) test.getVariable("name"));
	//String processId = test.getProcessInstanceId();

	// Generate ContractOfferEntity to send to the partner
	System.out.println("erstelle neue entitiy");
	OfferEntity offerEntityFull = new OfferEntity();
	offerEntityFull.setOfferId(2);
	offerEntityFull.setCustomerId(bvisId);
	offerEntityFull.setPrice(fullPrice);
	offerEntityFull.setCustomerName(customerName);
	offerEntityFull.setDescription("Full coverage");
	
	OfferEntity offerEntitySemi = new OfferEntity();
	offerEntitySemi.setOfferId(1);
	offerEntitySemi.setCustomerId(bvisId);
	offerEntitySemi.setPrice(semiPrice);
	offerEntitySemi.setCustomerName(customerName);
	offerEntitySemi.setDescription("Semi coverage");

	// Give output to the user
	System.out.println("Offertid: " + offerEntityFull.getOfferId());
	System.out.println("Price: " + offerEntityFull.getPrice());
	System.out.println("BvisId: " + offerEntityFull.getCustomerId());
	System.out.println("Description: " + offerEntityFull.getDescription());
	System.out.println("name: " + offerEntityFull.getCustomerName());
	System.out.println(customerName);

	// Convert the contractOfferEntity to a string representing a json
	// object
	ObjectMapper mapper = new ObjectMapper();
	jsonInString = mapper.writeValueAsString(offerEntityFull);
	
	System.out.println("Contractoffer send to the partner: " + jsonInString);
	
	
	
	ObjectMapper mapper2 = new ObjectMapper();
	jsonInString2 = mapper2.writeValueAsString(offerEntitySemi);
	System.out.println("Contractoffer send to the partner: " + jsonInString2);
	
	test.setVariable("offerhalf", jsonInString);
	test.setVariable("offerfull", jsonInString2);
}
	
	public String getJsonstring1() {
		return jsonInString;
	}
	
	public String getJsonstring2() {
		return jsonInString;
	}
	
}
