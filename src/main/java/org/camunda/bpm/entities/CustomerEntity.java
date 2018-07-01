package org.camunda.bpm.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
@Entity
public class CustomerEntity {
	@Id
	  @GeneratedValue
	  protected Long id;

	  @Version
	  protected long version;
	
	protected int insuranceClass;
	protected String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getInsuranceClass() {
		return insuranceClass;
	}
	public void setInsuranceClass(int insuranceClass) {
		this.insuranceClass = insuranceClass;
	}
	protected enum customerType {PRIVATE,BUSINESS;}
	
	
}
