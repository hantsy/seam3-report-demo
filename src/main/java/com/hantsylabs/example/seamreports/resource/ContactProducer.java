package com.hantsylabs.example.seamreports.resource;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import com.hantsylabs.example.seamreports.model.Contact;

@RequestScoped
public class ContactProducer {

	@Inject
	EntityManager em;

	List<Contact> contacts;
	
	@PostConstruct
	public void init(){
		fetchContacts();
	}
	
	@Named("contacts")
	@Produces
	public List<Contact> getContacts() {
		return this.contacts;
	}
	
	public void onSaved(@Observes Contact p){
		fetchContacts();
	}
	
	private void fetchContacts(){
		this.contacts=em.createQuery("from Contact t order by t.firstName",
				Contact.class).getResultList();
	}
}
