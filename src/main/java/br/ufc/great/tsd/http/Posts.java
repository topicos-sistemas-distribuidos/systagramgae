package br.ufc.great.tsd.http;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonBackReference;

@XmlRootElement
public class Posts{
	private static final long serialVersionUID = 1L;
	private Date date;
	private int likes=0;
	private Person person;
	private Picture picture;
	private List<Comment> comments = new LinkedList<>();
	private List<Likes> listLikes = new LinkedList<>();
	
	public Posts() {
	}
	
    private Long id;

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
	public Picture getPicture() {
		return picture;
	}
	public void setPicture(Picture picture) {
		this.picture = picture;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getLikes() {
		return likes;
	}
	public void setLikes(int likes) {
		this.likes = likes;
	}
	
	public void addComment(Comment comment) {
		this.getComments().add(comment);
	}

	public void addLike(Likes likes) {
		this.getListLikes().add(likes);
	}
	
	public List<Likes> getListLikes() {
		return listLikes;
	}

	public void setListLikes(List<Likes> listLikes) {
		this.listLikes = listLikes;
	}

}