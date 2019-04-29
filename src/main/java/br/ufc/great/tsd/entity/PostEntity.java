package br.ufc.great.tsd.entity;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="PostEntity")
public class PostEntity extends AbstractModel<Long>{
	private static final long serialVersionUID = 1L;
	private Date date;
	private int likes=0;

	@OneToOne(fetch = FetchType.LAZY)
	private PersonEntity person;
	
	@OneToOne(fetch = FetchType.LAZY)
	private PictureEntity picture;
	
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private List<CommentEntity> comments = new LinkedList<>();
	
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private List<LikesEntity> listLikes = new LinkedList<>();
	
	public List<LikesEntity> getListLikes() {
		return listLikes;
	}

	public void setListLikes(List<LikesEntity> listLikes) {
		this.listLikes = listLikes;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public PostEntity() {
	}
	
	public PersonEntity getPerson() {
		return person;
	}
	public void setPerson(PersonEntity person) {
		this.person = person;
	}
	public PictureEntity getPicture() {
		return picture;
	}
	public void setPicture(PictureEntity picture) {
		this.picture = picture;
	}
	public List<CommentEntity> getComments() {
		return comments;
	}
	public void setComments(List<CommentEntity> comments) {
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
	
	public void addComment(CommentEntity comment) {
		this.getComments().add(comment);
	}

	public void addLike(LikesEntity like) {
		this.getListLikes().add(like);
	}
}