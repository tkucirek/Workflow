package org.camunda.bpm.entities;

import javax.persistence.Entity;

@Entity
public class PrivateCustomer extends  CustomerEntity {

	protected String dateOfBirth;
	protected Long privateCustomerId;
	
	
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public Long getPrivateCustomerId() {
		return privateCustomerId;
	}
	public void setPrivateCustomerId(Long privateCustomerId) {
		this.privateCustomerId = privateCustomerId;
	}
	
}
