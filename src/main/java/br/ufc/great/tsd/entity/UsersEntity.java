package br.ufc.great.tsd.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Classe modelo de Usuário
 * @author armandosoaressousa
 *
 */
@Entity
@Table(name="UsersEntity")
public class UsersEntity extends AbstractModel<Long>{	
	private static final long serialVersionUID = 1L;
	@Column(length=50)
	private String username;
	@Column(length=255)
	private String password;
	@Column(nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean enabled;
	@Column(length=255)
	private String email;		
	@JsonBackReference(value="user-person")
	@OneToOne(cascade=CascadeType.ALL)
	private PersonEntity person;
	
	public UsersEntity() {
	}
	
	public PersonEntity getPerson() {
		return person;
	}

	public void setPerson(PersonEntity person) {
		this.person = person;
	}

	public UsersEntity(String username, String password, String email) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}