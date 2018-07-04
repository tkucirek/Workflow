package org.camunda.bpm.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class ContractEntity implements Serializable {
	
	private static  final long serialVersionUID = 1L;

	  @Id
	  @GeneratedValue
	  protected Long id;

	  @Version
	  protected long version;
	  

	protected Long customerId;
	protected Long vehicle_model;
	protected Long number_of_vehicles;
	protected int contractId;
	protected double price;
	protected Long duration;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public Long getVehicle_model() {
		return vehicle_model;
	}
	public void setVehicle_model(Long vehicle_model) {
		this.vehicle_model = vehicle_model;
	}
	public Long getNumber_of_vehicles() {
		return number_of_vehicles;
	}
	public void setNumber_of_vehicles(Long number_of_vehicles) {
		this.number_of_vehicles = number_of_vehicles;
	}
	public int getContractId() {
		return contractId;
	}
	public void setContractId(int contractId) {
		this.contractId = contractId;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}


}
