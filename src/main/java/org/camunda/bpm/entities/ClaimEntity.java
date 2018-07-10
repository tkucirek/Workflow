package org.camunda.bpm.entities;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;


@Entity
public class ClaimEntity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	protected Long id;
	
	
	
	
	@Version
	protected long version;
	
	protected String customerId;
	protected Long damageClassification;
	protected boolean completed;
	
	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String string) {
		this.customerId = string;
	}
	public long getDamageClassification() {
		return damageClassification;
	}
	public void setDamageClassification(Long long1) {
		this.damageClassification = long1;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

}
