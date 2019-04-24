package br.ufc.great.tsd.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="Likes")
public class LikesEntity extends AbstractModel<Long>{
	private static final long serialVersionUID = 1L;
	private String description;
	@OneToOne
	private PersonEntity person;
	@OneToOne
	private PostEntity post;
	
	private Date date;
	
	@Column(nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean mylike;
	
	public PersonEntity getPerson() {
		return person;
	}
	public void setPerson(PersonEntity person) {
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
	public PostEntity getPost() {
		return post;
	}
	public void setPost(PostEntity post) {
		this.post = post;
	}
	public boolean isMylike() {
		return mylike;
	}
	public void setMylike(boolean mylike) {
		this.mylike = mylike;
	}

}