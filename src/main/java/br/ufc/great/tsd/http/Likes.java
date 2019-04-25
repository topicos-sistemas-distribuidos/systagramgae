package br.ufc.great.tsd.http;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

public class Likes{
	private String description;
	private Person person;
	private Posts post;
	private Date date;
	private boolean mylike;
	
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Posts getPost() {
		return post;
	}
	public void setPost(Posts post) {
		this.post = post;
	}
	public boolean isMylike() {
		return mylike;
	}
	public void setMylike(boolean mylike) {
		this.mylike = mylike;
	}

}