package br.ufc.great.tsd.http;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe modelo de Usuário
 * @author armandosoaressousa
 *
 */
@XmlRootElement
public class Users{	
	private String username;
	private String password;
	private boolean enabled;
	private String email;		
	private Person person;
    private Long id;
	
	public Users() {
	}

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Users(String username, String password, String email) {
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