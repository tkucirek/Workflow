package org.camunda.bpm.messages;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.entities.ContractEntity;
import org.camunda.bpm.entities.OfferEntity;

import com.fasterxml.jackson.databind.ObjectMapper;



public class InsuranceOffering implements JavaDelegate {
	
	private String jsonInString;
	private String jsonInString2;
	
	/**
	 * Creates two new offers (semi- and full coverage) based on the committed input variables.
	 * The offer-objects are then mapped into a json- String.
	 * @param test
	 * @throws Exception
	 */
	
	public void execute(DelegateExecution test) throws Exception {
	long fullPrice=(long) test.getVariable("fullPrice");
	long semiPrice=(long) test.getVariable("semiPrice");

	OfferEntity offerEntityFull = new OfferEntity();
	offerEntityFull.setOffer_Id(2);
	offerEntityFull.setPrice(fullPrice);
	offerEntityFull.setName("Full coverage");
	offerEntityFull.setDescription("We cover the total damage amount - you only have to pay the claim-service fee");
	
	OfferEntity offerEntitySemi = new OfferEntity();
	offerEntitySemi.setOffer_Id(1);
	offerEntitySemi.setPrice(semiPrice);
	offerEntitySemi.setName("Semi coverage");
	offerEntitySemi.setDescription("We cover halft of the total damage amount - you will have to pay the other half plus the claim-service fee");

	ObjectMapper mapper = new ObjectMapper();
	jsonInString = mapper.writeValueAsString(offerEntityFull);
	
	System.out.println("Contractoffer for full coverage send to the partner: " + jsonInString);
	
	
	
	ObjectMapper mapper2 = new ObjectMapper();
	jsonInString2 = mapper2.writeValueAsString(offerEntitySemi);
	System.out.println("Contractoffer for semi coverage send to the partner: " + jsonInString2);
	
	test.setVariable("offerhalf", offerEntitySemi);
	test.setVariable("offerfull", offerEntityFull);
	
}
	
	public String getJsonstring1() {
		return jsonInString;
	}
	
	public String getJsonstring2() {
		return jsonInString;
	}
	
}