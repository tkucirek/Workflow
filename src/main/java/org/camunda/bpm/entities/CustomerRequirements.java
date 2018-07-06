package com.group5.BVIS.RestModels;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRequirements {

	public CustomerRequirements() {
		super();
		// TODO Auto-generated constructor stub
	}

	private String customer_id;
	private String customer_type;
	
	private Long rental_duration;
	private Long vehicle_model;
	
	private Long number_of_vehicles;

	private Date birth_date;
	private String name;
	private String address;
	
	public CustomerRequirements(String customer_id, String customer_type, Long rental_duration, Long vehicle_model, Long number_of_vehicles,
			Date birth_date, String name, String address) {
		super();
		this.customer_id = customer_id;
		this.customer_type = customer_type;
		this.rental_duration = rental_duration;
		this.vehicle_model = vehicle_model;
		this.number_of_vehicles = number_of_vehicles;
		this.birth_date = birth_date;
		this.name = name;
		this.address = address;
	}
	
	public String getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	public String getCustomer_type() {
		return customer_type;
	}
	public void setCustomer_type(String customer_type) {
		this.customer_type = customer_type;
	}
	public Long getRental_duration() {
		return rental_duration;
	}
	public void setRental_duration(Long rental_duration) {
		this.rental_duration = rental_duration;
	}
	public Long getVehicle_model() {
		return vehicle_model;
	}
	public void setVehicle_model(Long vehicle_model) {
		this.vehicle_model = vehicle_model;
	}
	public Date getBirth_date() {
		return birth_date;
	}
	public void setBirth_date(Date birth_date) {
		this.birth_date = birth_date;
	}
	public String getFirst_name() {
		return name;
	}
	public void setFirst_name(String first_name) {
		this.name = first_name;
	}

	public String getaddress() {
		return address;
	}
	public void setaddress(String address) {
		this.address = address;
	}

	public Long getNumber_of_vehicles() {
		return number_of_vehicles;
	}

	public void setNumber_of_vehicles(Long number_of_vehicles) {
		this.number_of_vehicles = number_of_vehicles;
	}

	@Override
	public String toString() {
		return "CustomerRequirements [customer_id=" + customer_id + ", customer_type=" + customer_type
				+ ", rental_duration=" + rental_duration + ", vehicle_model=" + vehicle_model + ", number_of_vehicles="
				+ number_of_vehicles + ", birth_date=" + birth_date + ", name=" + name + ", address=" + address + "]";
	}	
}
