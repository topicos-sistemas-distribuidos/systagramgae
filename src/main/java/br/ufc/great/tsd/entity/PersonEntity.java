package br.ufc.great.tsd.entity;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="Person")
public class PersonEntity extends AbstractModel<Long>{
	private static final long serialVersionUID = 1L;
	@JsonBackReference(value="user-person")
	@OneToOne(fetch = FetchType.LAZY)
	private UsersEntity user;
	@Column(length=255)
	private String name;
	@Column(length=255)
	private String address;
	@Column(length=255)
	private String city;
	@Column(length=255)
	private String state;
	@Column(length=8)
	private String cep;
	private double latitude=0;
	private double longitude=0;

	@JsonBackReference(value="person-comment")
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private List<CommentEntity> comments = new LinkedList<>();
	
	@JsonBackReference(value="person-picture")
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private List<PictureEntity> pictures = new LinkedList<>();
	
	@JsonBackReference(value="person-post")
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private List<PostEntity> posts = new LinkedList<>();
	
	@JsonBackReference(value="person-like")
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private List<LikesEntity> likes = new LinkedList<>();

	public PersonEntity() {
	}
	
	public List<PostEntity> getPosts() {
		return posts;
	}

	public void setPosts(List<PostEntity> posts) {
		this.posts = posts;
	}

	
	public List<PictureEntity> getPictures() {
		return pictures;
	}

	public void setPictures(List<PictureEntity> pictures) {
		this.pictures = pictures;
	}

	public List<CommentEntity> getComments() {
		return comments;
	}

	public void setComments(List<CommentEntity> comments) {
		this.comments = comments;
	}

	public List<LikesEntity> getLikes(){
		return likes; 
	}
	
	public void setLikes(List<LikesEntity> likes) {
		this.likes = likes;
	}
	
	public UsersEntity getUser() {
		return user;
	}
	public void setUser(UsersEntity user) {
		this.user = user;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void addComment(CommentEntity comment) {
		this.getComments().add(comment);
	}
	
	public CommentEntity getMyComment(Long id) {
		List<CommentEntity> list = this.getComments();
		
		for (CommentEntity element : list) {
			if (element.getId()==id) {
				return element;
			}
		}
		return null;
	}

	public void addPicture(PictureEntity picture, PersonEntity person) {
		picture.setPerson(person);
		this.getPictures().add(picture);
	}
	
	public PictureEntity getMyPicture(Long id) {
		List<PictureEntity> list = this.getPictures();
		
		for (PictureEntity element : list) {
			if (element.getId()==id) {
				return element;
			}
		}
		return null;
	}

	public void addPost(PostEntity post) {
		this.posts.add(post);
	}
	
	public void addLike(LikesEntity like) {
		this.likes.add(like);
	}

	public List<PostEntity> getPostByDateFromTo(Date from, Date to) {
		// TODO Auto-generated method stub
		return null;
	}
	
}