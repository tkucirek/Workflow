package org.camunda.bpm.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class OfferEntity {
	@Id
	@GeneratedValue
	protected int id;
	
	protected double price;
	protected long customerId;
	protected String description;
	protected String customerName;
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public int getOfferId() {
		return id;
	}
	public void setOfferId(int offerId) {
		this.id = offerId;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}


}
