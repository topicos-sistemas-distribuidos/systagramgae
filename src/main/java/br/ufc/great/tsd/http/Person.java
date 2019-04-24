package br.ufc.great.tsd.http;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Person{
	private Users user;
	private String name;
	private String address;
	private String city;
	private String state;
	private String cep;
	private double latitude=0;
	private double longitude=0;
    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
	
	private List<Comment> comments = new LinkedList<>();
	
	private List<Picture> pictures = new LinkedList<>();
	
	private List<Post> posts = new LinkedList<>();
	
	private List<Likes> listLikes = new LinkedList<>();

	public Person() {
	}
	
	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	
	public List<Picture> getPictures() {
		return pictures;
	}

	public void setPictures(List<Picture> pictures) {
		this.pictures = pictures;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
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

	public void addComment(Comment comment, Person person) {
		comment.setPerson(person);
		this.getComments().add(comment);
	}
	public Comment getMyComment(Long id) {
		List<Comment> list = this.getComments();
		
		for (Comment element : list) {
			if (element.getId()==id) {
				return element;
			}
		}
		return null;
	}

	public void addPicture(Picture picture, Person person) {
		picture.setPerson(person);
		this.getPictures().add(picture);
	}
	public Picture getMyPicture(Long id) {
		List<Picture> list = this.getPictures();
		
		for (Picture element : list) {
			if (element.getId()==id) {
				return element;
			}
		}
		return null;
	}

	public void addPost(Post post) {
		this.posts.add(post);
	}

	public List<Likes> getListLikes() {
		return listLikes;
	}

	public void setListLikes(List<Likes> listLikes) {
		this.listLikes = listLikes;
	}
	
}