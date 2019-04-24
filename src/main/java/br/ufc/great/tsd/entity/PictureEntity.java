package br.ufc.great.tsd.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="Picture")
public class PictureEntity extends AbstractModel<Long>{
	private static final long serialVersionUID = 1L;
	private String name;
	private String path;
	private String systemName;
	
	@JsonBackReference(value="picture-person")
	@OneToOne(fetch = FetchType.LAZY)
	private PersonEntity person;
	
	@JsonBackReference(value="picture-post")
	@OneToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private PostEntity post;
	
	public PictureEntity() {
	}
	
	public PersonEntity getPerson() {
		return person;
	}
	public void setPerson(PersonEntity person) {
		this.person = person;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public PostEntity getPost() {
		return post;
	}

	public void setPost(PostEntity post) {
		this.post = post;
	}
}
