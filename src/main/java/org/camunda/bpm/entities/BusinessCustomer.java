package org.camunda.bpm.entities;

import javax.persistence.Entity;

/**
 * @author Capitol Template for the BusinessCustomerEntity.
 */
@Entity
public class BusinessCustomer extends CustomerEntity {
	
	protected Long businessCustomerId;
	
	public Long getBusinessCustomerId() {
		return businessCustomerId;
	}
	public void setBusinessCustomerId(Long businessCustomerId) {
		this.businessCustomerId = businessCustomerId;
	}
}
