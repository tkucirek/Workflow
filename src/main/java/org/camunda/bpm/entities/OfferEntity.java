package org.camunda.bpm.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class OfferEntity {
	@Id
	@GeneratedValue
	protected int id;
	
	protected double price;
	protected String customerId;
	protected String description;
	protected String name;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getOffer_Id() {
		return id;
	}
	public void setOffer_Id(int offer_Id) {
		this.id = offer_Id;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String bvisId) {
		this.customerId = bvisId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}


}
