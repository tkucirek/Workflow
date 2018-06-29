package org.camunda.bpm.entities;

public class BusinessCustomer extends CustomerEntity {
	
	protected Long businessCustomerId;
	
	public Long getBusinessCustomerId() {
		return businessCustomerId;
	}
	public void setBusinessCustomerId(Long businessCustomerId) {
		this.businessCustomerId = businessCustomerId;
	}
}
