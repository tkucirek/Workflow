package org.camunda.bpm.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

public class CustomerEntity {
	@Id
	  @GeneratedValue
	  protected Long id;

	  @Version
	  protected long version;
	
	protected int insuranceClass;
	protected enum customerType {PRIVATE,BUSINESS;}
	
	
}
