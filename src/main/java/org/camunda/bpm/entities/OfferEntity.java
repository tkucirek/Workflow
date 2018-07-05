package org.camunda.bpm.entities;

public class OfferEntity {
	protected double price;
	protected long customerId;
	protected String description;
	protected int offerId;
	protected String customerName;
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public int getOfferId() {
		return offerId;
	}
	public void setOfferId(int offerId) {
		this.offerId = offerId;
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
