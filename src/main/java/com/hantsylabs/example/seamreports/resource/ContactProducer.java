package com.hantsylabs.example.seamreports.resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import com.hantsylabs.example.seamreports.model.Contact;

@RequestScoped
public class ContactProducer {

	List<Contact> contacts;

	@PostConstruct
	public void init() {
		fetchContacts();
	}

	@Named("contacts")
	@Produces
	public List<Contact> getContacts() {
		return this.contacts;
	}

	public void onSaved(@Observes Contact p) {
		fetchContacts();
	}

	private void fetchContacts() {
		this.contacts = new ArrayList<Contact>();
		this.contacts.add(new Contact(1L, "hantsy", "bai", "hantsy@tom.com",
				"12345678"));
		this.contacts.add(new Contact(2L, "tomcat", "apache",
				"tomcat@apache.com", "123456789"));
	}
}
