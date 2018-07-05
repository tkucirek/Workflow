package com.group5.BVIS.RestModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InsuranceOffering implements java.io.Serializable {
	
	private static final long serialVersionUID = 42L;
	
	public InsuranceOffering(Long offer_id, String name, float price, String description) {
		super();
		this.offer_id = offer_id;
		this.name = name;
		this.price = price;
		Description = description;
	}
	
	public InsuranceOffering() {
		super();
	}

	//validation missing!!!
	private Long offer_id;
	
	private String name;

	private float price;	
	
	private String Description;

	public Long getOffer_id() {
		return offer_id;
	}

	public void setOffer_id(Long offer_id) {
		this.offer_id = offer_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	@Override
	public String toString() {
		return "InsuranceOffering [offer_id=" + offer_id + ", name=" + name + ", price=" + price + ", Description="
				+ Description + "]";
	}
}
