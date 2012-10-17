package com.hantsylabs.example.seamreports.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.solder.core.Veto;

/**
 * Entity implementation class for Entity: Person
 * 
 */
@Entity
@Veto
@Table(name = "contacts")
public class Contact implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@NotNull
	@NotEmpty
	@Column(name = "first_name")
	private String firstName;

	@NotNull
	@NotEmpty
	@Column(name = "last_name")
	private String lastName;

	@NotNull
	@Email
	@Column(name = "email_addr")
	private String emailAddress;

	@Column(name = "phone_num")
	private String phoneNumber;

	public Contact() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Transient
	public String getName() {
		return (this.firstName == null ? "" : this.firstName)
				+ (this.lastName == null ? "" : " " + this.lastName);
	}

}
