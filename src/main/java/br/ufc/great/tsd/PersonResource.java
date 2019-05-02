package br.ufc.great.tsd;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.ufc.great.tsd.entity.CommentEntity;
import br.ufc.great.tsd.entity.LikesEntity;
import br.ufc.great.tsd.entity.PersonEntity;
import br.ufc.great.tsd.entity.PictureEntity;
import br.ufc.great.tsd.entity.PostEntity;
import br.ufc.great.tsd.entity.UsersEntity;
import br.ufc.great.tsd.http.Comment;
import br.ufc.great.tsd.http.Likes;
import br.ufc.great.tsd.http.Person;
import br.ufc.great.tsd.http.Picture;
import br.ufc.great.tsd.http.Posts;
import br.ufc.great.tsd.http.Users;
import br.ufc.great.tsd.service.CommentService;
import br.ufc.great.tsd.service.LikesService;
import br.ufc.great.tsd.service.PersonService;
import br.ufc.great.tsd.service.PictureService;
import br.ufc.great.tsd.service.PostService;
import br.ufc.great.tsd.service.UsersService;
import br.ufc.great.tsd.util.GlobalEntityManager;
import br.ufc.great.tsd.util.ManipuladorDatas;
import br.ufc.great.tsd.util.Message;

public class PersonResource extends ServerResource{
	private GlobalEntityManager myEntityManager = new GlobalEntityManager();
	private PersonService personService;
	private CommentService commentService;
	private PictureService pictureService;
	private PostService postService;
	private LikesService likesService;
    private UsersService userService;
    
    String personId;
    String pictureId;
    String id;
    String listPosts;
	String personLogged;
	String postId; 
	String type;
	
	public PersonResource(){
		this.personService = new PersonService(myEntityManager);
		this.commentService = new CommentService(myEntityManager);
		this.pictureService = new PictureService(myEntityManager);
		this.postService = new PostService(myEntityManager);
		this.likesService = new LikesService(myEntityManager);
		this.userService = new UsersService(myEntityManager);
	}
	
    @Override
    public void doInit() {
    	this.pictureId = getAttribute("pictureId");
    	this.personId = getAttribute("personId");
    	this.id = getAttribute("id");
    	this.listPosts = getAttribute("listPosts");
    	this.personLogged = getAttribute("personLogged");
    	this.postId = getAttribute("postId");
    	this.type = getAttribute("type");
    }

    @Get("json")
    public String toString() {
    	Gson gson = new Gson();
    	String json=""; 
    	Message message = new Message();
    	
    	// /person/{personId}
    	if (personId != null && listPosts == null && pictureId == null) {
    		return(showPerson(this.personId, gson, json, message));
    	}
    	
    	// /person/{personId}/post/listPosts
    	if (personId != null && type != null) {
    		if (type.equals("listPosts")) {
    			return(showPostFromPerson(this.personId, gson, json, message));
    		}else {
    			json = "";
    		}
    	}
    	
    	// /person/{personId}/picture/{pictureId}/post}
    	if (personId != null && pictureId != null) {
    		return(postPictureFromPerson(this.personId, this.pictureId, gson, json, message));
    	}

    	// /person
		if (personId==null && pictureId==null && id==null && listPosts==null) {
			return(showAllPeople(gson, json, message));
		}
		
		return json;
    }

    /**
     * Mostra todas as pessas cadastradas no sistema
     * @param gson
     * @param json
     * @param message
     * @return
     */
	private String showAllPeople(Gson gson, String json, Message message) {
		String jsonMessage;
		List<Person> people = new LinkedList<Person>();
		people = this.getAllPeople();
		
		try {
			json = gson.toJson(people);
		}catch (Exception e){
			e.printStackTrace();
			message.setConteudo("Erro ao exibir informação de pessoa");
			message.setId(500);
			jsonMessage = gson.toJson(message);
			return jsonMessage;     		
		}
		
		return(json);
	}

	/**
	 * Mostra os Posts de uma pessoa em JSON
	 * @param id
	 * @param gson
	 * @param json
	 * @param message
	 * @return
	 */
	private String showPostFromPerson(String id, Gson gson, String json, Message message) {
		String jsonMessage;
		List<Posts> myPosts = new LinkedList<>();
		myPosts =  this.listMyPosts(Long.valueOf(id));

		try {
			json = gson.toJson(myPosts);
		}catch(Exception e) {
			e.printStackTrace();
			message.setConteudo("Erro ao exibir os posts da Pessoa selecionada");
			message.setId(500);
			jsonMessage = gson.toJson(message);
			return jsonMessage; 
		}
		
		return json;
	}

	/**
	 * Mostra os dados de uma pessoa como JSON
	 * @param id
	 * @param gson
	 * @param json
	 * @param message
	 * @return
	 */
	private String showPerson(String id, Gson gson, String json, Message message) {
		String jsonMessage;
		Person person = new Person();
		person = this.getPerson(Long.valueOf(id));

		try {
			json = gson.toJson(person);
		}catch(Exception e) {
			e.printStackTrace();
			message.setConteudo("Erro ao exibir informação de pessoa");
			message.setId(500);
			jsonMessage = gson.toJson(message);
			return jsonMessage; 
		}
		
		return json;
	}

	/**
	 * Dada uma picture selecionada faz a publicacao da mesma 
	 * @param personId
	 * @param pictureId
	 * @param gson
	 * @param json
	 * @param message
	 * @return
	 */
	private String postPictureFromPerson(String personId, String pictureId, Gson gson, String json, Message message) {
		this.createPost(Long.valueOf(pictureId), Long.valueOf(personId));

		message.setConteudo("Post Criado com sucesso!");
		message.setId(200);
		json = gson.toJson(message);
		
		return json; 
	}
	
    @Post
    public String saveContent(String jsonContent) {
    	String jsonResult = null;
    	
    	if (type.equals("comment")) {
    		jsonResult = saveCommentOnPost(jsonContent);
    	}
    	
    	if (type.equals("like")) {
    		jsonResult = saveLikeOnPost(jsonContent);
    	}
    	
    	return jsonResult;
    }
    
    public String saveCommentOnPost(String jsonComment) {
    	Message message = new Message();
    	Gson gson = new Gson();
    	String json;
    	
    	Type commentType = new TypeToken<Comment>(){}.getType();
    	//recupera o json que representa comentario
    	Comment comment = gson.fromJson(jsonComment, commentType);
    	
    	CommentEntity myComment = new CommentEntity();
    	myComment.setDate(comment.getDate());
    	myComment.setDescription(comment.getDescription());
    	//Pessoa que fez o comentário
    	if (comment.getPerson() != null) {
    		PersonEntity personComment = new PersonEntity();
    		personComment.setId(comment.getPerson().getId());
    		personComment.setName(comment.getPerson().getName());
        	myComment.setPerson(personComment);    		
    	}

    	Long personLogged = Long.valueOf(this.personLogged);
    	
    	try {
    		this.saveCommentInSelectedPost(myComment, Long.valueOf(postId), personLogged);
    		message.setConteudo("Comentário inserido com sucesso!");
    		message.setId(200);    		
    	}catch (Exception e) {
    		message.setConteudo("Erro ao inserir comentário no post");
    		message.setId(500);    		
		}
    	
    	json = gson.toJson(message);
    	return json; 
    }
    
    public String saveLikeOnPost(String jsonLike) {
    	Message message = new Message();
    	Gson gson = new Gson();
    	String json;
    	
    	Type likeType = new TypeToken<Likes>(){}.getType();
    	//recupera o json que representa um like
    	Likes like =  gson.fromJson(jsonLike, likeType);
    	
    	LikesEntity myLike = new LikesEntity();
    	myLike.setDate(like.getDate());
    	myLike.setDescription(like.getDescription());
    	myLike.setMylike(like.isMylike());
    	if (like.getPerson() != null) {
    		PersonEntity personLike = new PersonEntity();
    		personLike.setId(like.getPerson().getId());
    		personLike.setName(like.getPerson().getName());
        	myLike.setPerson(personLike);    		
    	}
    	if (like.getPost() != null) {
    		PostEntity postLike = new PostEntity();
    		postLike.setId(like.getPost().getId());
        	myLike.setPost(postLike);
    	}

    	try {
    		this.saveLikeInSelectedPost(myLike, Long.valueOf(postId), Long.valueOf(personLogged));
    		message.setConteudo("Like inserido com sucesso!");
    		message.setId(200);    		
    	}catch (Exception e) {
    		message.setConteudo("Erro ao inserir Like no post");
    		message.setId(500);    		
		}
    	
    	json = gson.toJson(message);
    	return json; 
    }
    
	/**
	 * Lista todas as pessoas cadastradas no sistema
	 * @param model
	 * @return página com a lista de pessoas cadastradas
	 */
    //@RequestMapping(value="/person", method = RequestMethod.GET)
    public List<Person> getAllPeople() {
    	List<Person> people = new ArrayList<Person>();
    	List<PersonEntity> list = this.personService.getAll();
    	
    	for (PersonEntity elemento : list) {
    		Person person = new Person();
    		
    		person.setId(elemento.getId());
    		person.setName(elemento.getName());
    		Users user = new Users();
    		user.setId(elemento.getUser().getId());
    		user.setUsername(elemento.getUser().getUsername());
    		user.setEmail(elemento.getUser().getEmail());
    		person.setUser(user);
    		person.setPosts(this.listMyPosts(elemento.getId()));
    		people.add(person);
    	}
    	
        return people;
    }
    
    public Person getPerson(Long id) {
    	PersonEntity personEntity = this.personService.get(id);
    	Person person = new Person();
    	
    	person.setId(personEntity.getId());
    	person.setCep(personEntity.getCep());
    	//lista de comentários da pessoa
    	if(personEntity.getComments() != null) {
        	person.setComments(this.listMyComments(person.getId()));    		
    	}

    	person.setAddress(personEntity.getAddress());
    	person.setLatitude(personEntity.getLatitude());
    	person.setLongitude(personEntity.getLongitude());
    	//lista de likes da pessoa
    	if (personEntity.getLikes() != null) {
        	person.setListLikes(this.listMyLikes(person.getId()));    		
    	}

    	person.setName(personEntity.getName());
    	
    	//lista de pictures da pessoa
    	if (personEntity.getPictures() != null) {
        	person.setPictures(this.listMyPictures(person.getId()));    		
    	}

    	if (personEntity.getPosts() != null) {
    		List<Posts> posts = new LinkedList<>(); 
    		posts = this.listMyPosts(person.getId());
        	person.setPosts(posts);    		
    	}

    	person.setState(personEntity.getState());
    	
    	if (personEntity.getUser() != null) {
    		Users user = new Users();
    		user.setId(personEntity.getUser().getId());
    		user.setUsername(personEntity.getUser().getUsername());
    		user.setEmail(personEntity.getUser().getEmail());
    	}

    	return person; 
    }
    
    /**
     * Dada uma pessoa, retorna todos os comentários que ela fez
     * @param id Id de pessoa
     * @return listMyComments.html
     */
    //@RequestMapping(value="/person/{id}/comment")
    public List<Comment> listMyComments(Long id) {
    	List<Comment> myComments = new ArrayList<Comment>();
    	PersonEntity person = personService.get(id);
    	
    	List<CommentEntity> list = person.getComments();
    	
    	if (list.size() == 0) {
    		return null;
    	}

    	for (CommentEntity elemento : list) {
    		Comment myComment = new Comment();
    		myComment.setDate(elemento.getDate());
    		myComment.setDescription(elemento.getDescription());
    		myComment.setId(elemento.getId());
			Person person1 = new Person();
    		if (elemento.getPerson() != null) {
    			person.setId(elemento.getPerson().getId());
    			person.setName(elemento.getPerson().getName());
    		}
    		myComment.setPerson(person1);
    		myComments.add(myComment);
    	}
    	
        return myComments;
    }

    /**
     * Dada uma pessoa, retorna todos os likes que ela fez
     * @param id Id de pessoa
     * @param model Model do View
     * @return listMyLikes.html
     */
    //@RequestMapping(value="/person/{id}/likes")
    public List<Likes> listMyLikes(Long id) {
    	List<Likes> myLikes = new LinkedList<Likes>();    	
    	PersonEntity person = personService.get(id);
    	    	
    	List<LikesEntity> list = person.getLikes();
    	
    	if (list.size() == 0) {
    		return null;
    	}
    	
    	for (LikesEntity elemento : list) {
    		Likes myLike = new Likes();
    		myLike.setDate(elemento.getDate());
    		myLike.setDescription(elemento.getDescription());
    		myLike.setMylike(elemento.isMylike());
			
    		Person likePerson = new Person();    		
    		if (elemento.getPerson() != null) {

    			likePerson.setId(elemento.getPerson().getId());
    			likePerson.setName(elemento.getPerson().getName());
    		}
    		myLike.setPerson(likePerson);
    		
    		Posts postLike = new Posts();
    		if (elemento.getPost() != null) {
    			postLike.setId(elemento.getId());
    		}
    		myLike.setPost(postLike);
    		myLikes.add(myLike);
    	}
    	
        return myLikes;
    }
    
    public List<Picture> listMyPictures(Long id){
    	List<Picture> myPictures = new LinkedList<Picture>();
    	PersonEntity person = personService.get(id);
    	
    	List<PictureEntity> list = person.getPictures();
    	
    	if (list.size() == 0) {
    		return null;
    	}
    	
    	for(PictureEntity elemento : list) {
    		Picture myPicture = new Picture();
    		myPicture.setId(elemento.getId());
    		myPicture.setName(elemento.getName());
    		myPicture.setPath(elemento.getPath());
    		myPicture.setSystemName(elemento.getSystemName());
    		Person picturePerson = new Person();
    		if (elemento.getPerson() != null) {
    			picturePerson.setId(elemento.getPerson().getId());
    		}
    		myPicture.setPerson(picturePerson);
    		
    		Posts picturePost = new Posts();
    		if (elemento.getPost() != null) {
    			picturePost.setId(elemento.getPost().getId());
    		}
    		myPicture.setPost(picturePost);
    		myPictures.add(myPicture);
    	}
    	
    	return myPictures;
    }

    /**
     * Retorna todos os comentários de todas as pessoas
     * @param model Model
     * @return listComments.html
     */
    //@RequestMapping(value="/person/comment")
    public List<Comment> listAllComments() {
    	List<Comment> allComments = new ArrayList<Comment>();
    	
    	List<CommentEntity> comments = this.commentService.getAll();
    	//TODO carregar a lista de comments em allComments
    	
    	return allComments;
    }
    
    /**
     * Retorna todos os comentários de todas as pessoas
     * @param model Model
     * @return listComments.html
     */
    //@RequestMapping(value="/person/likes")
    public List<Likes> listAllLikes() {
    	List<Likes> allLikes = new ArrayList<Likes>(); //json http
    	
    	List<LikesEntity> likes = this.likesService.getAll(); //objeto interno
    	//TODO carregar a lista de likes em allLikes
    	
    	return allLikes;
    }

    /**
     * Dada uma pessoa e um comentário preenchido, salva esse comentário na lista de comentários da pessoa
     * @param id Id da Pessoa
     * @param comment Novo comentario
     * @param ra mensagem flash de retorno
     * @return listMyComments.html
     */
    //@RequestMapping(value="/person/{id}/comment/save", method=RequestMethod.POST)
    public String saveMyComment(Long id, CommentEntity comment) {
    	
    	PersonEntity person = this.personService.get(id);
    	comment.setPerson(person);
    	person.addComment(comment);
    	
    	//Atualiza a pessoa com o novo comentario
    	this.personService.update(person);
    	//ra.addFlashAttribute("successFlash", "Comentário salvo com sucesso.");
    	
    	return "redirect:";
    }

    /**
     * Dada uma pessoa e um like, salva esse like na lista de likes da pessoa
     * @param id Id da Pessoa
     * @param like Novo like
     * @param ra mensagem flash de retorno
     * @return listMyLikes.html
     */
    //@RequestMapping(value="/person/{id}/likes/save", method=RequestMethod.POST)
    public String saveMyLike(Long id, LikesEntity like) {
    	
    	PersonEntity person = this.personService.get(id);
    	
    	person.addLike(like);
    	//Atualiza a pessoa com o novo like
    	this.personService.update(person);
    	//ra.addFlashAttribute("successFlash", "Like salvo com sucesso.");
    	
    	return "redirect:";
    }

    
    //@RequestMapping(value="/person/{id}/comment/update", method=RequestMethod.POST)
    public String saveMyCommentEdited(Long id, CommentEntity comment) {
    	
    	PersonEntity person = this.personService.get(id);
    	comment.setPerson(person);
    	
    	this.commentService.update(comment);
    	//ra.addFlashAttribute("successFlash", "Comentário salva com sucesso.");
    	    	
    	return "redirect:";
    }

    
    /**
     * Dada uma pessoa e um comentário selecionado faz a edição do mesmo
     * @param personId id da Pessoa
     * @param commentId id do Comentário selecionado
     * @param model Model
     * @return formEditMyComment.html
     */
    //@RequestMapping(value="/person/{personId}/comment/{commentId}/edit")
    public String editMyComment(Long personId, Long commentId) {
    	PersonEntity person = this.personService.get(personId);
    	CommentEntity comment = person.getMyComment(commentId);
    	    	
        return "person/formEditMyComment";
    }
            
    /**
     * Seleciona uma foto da pessoa para ser adicionada no seu album
     * @param personId id da Pessoa
     * @param model model
     * @return formPicture.html
     */
	//@RequestMapping(value = "/person/{personId}/select/picture")
	public String selectImage(Long personId){
		PersonEntity person = this.personService.get(personId);
		PictureEntity picture = new PictureEntity();
		
		picture.setPerson(person);
		    	
        return "person/formPicture";
	}
    
	//@RequestMapping(value="/person/{personId}/picture/{pictureId}/edit")
    public String editMyPicture() {
    	//TODO
    	return null;
    }
	
    /**
	 * Carrega a página contendo todos os posts do usuário
	 * @param id Id da pessoa
	 * @param model Model
	 * @return listMyPosts.html
	 */
	//@RequestMapping("/person/{id}/post")
	public List<Posts> listMyPosts(Long id) {
		PersonEntity person = this.personService.get(id);
		
		if (person.getPosts().size() == 0) {
			return null;
		}

		List<Posts> myPosts = new LinkedList<Posts>();
		List<PostEntity> list = person.getPosts();
		
		for (PostEntity elemento : list) {
			Posts post = new Posts();
			List<CommentEntity> listComments = elemento.getComments();
			if (listComments != null) {
				List<Comment> comments = new LinkedList<Comment>();
				for (CommentEntity comment : listComments) {
					Comment myComment = new Comment();
					myComment.setDate(comment.getDate());
					myComment.setDescription(comment.getDescription());
					myComment.setId(comment.getId());
					
					Person myPerson1 = new Person();
					PersonEntity personComment = new PersonEntity();
					personComment = comment.getPerson();
					if (personComment != null) {
						myPerson1.setAddress(personComment.getAddress());
						myPerson1.setName(personComment.getName());
						myPerson1.setId(personComment.getId());
						myComment.setPerson(myPerson1);
					}
					comments.add(myComment);
				}
				post.setComments(comments);
			}
			
			post.setDate(elemento.getDate());
			post.setId(elemento.getId());
			post.setLikes(elemento.getLikes());
			
			if (elemento.getListLikes() != null) {
				List<Likes> likes = new LinkedList<>();
				
				for (LikesEntity like : elemento.getListLikes()) {
					Likes myLike = new Likes();
					myLike.setDate(like.getDate());
					myLike.setDescription(like.getDescription());
					myLike.setMylike(like.isMylike());
					
					Person myPerson3 = new Person();
					if (like.getPerson() != null) {
						myPerson3.setAddress(like.getPerson().getAddress());
						myPerson3.setName(like.getPerson().getName());
						myPerson3.setId(like.getPerson().getId());
						myLike.setPerson(myPerson3);
					}
					likes.add(myLike);
				}
				post.setListLikes(likes);				
			}

			if (elemento.getPerson() != null) {
				Person myPerson = new Person();
				myPerson.setAddress(elemento.getPerson().getAddress());
				myPerson.setName(elemento.getPerson().getName());
				myPerson.setId(elemento.getId());
				post.setPerson(myPerson);				
			}
			
			if (elemento.getPicture() != null) {
				Picture picture = new Picture();
				picture.setId(elemento.getPicture().getId());
				picture.setName(elemento.getPicture().getName());
				picture.setPath(elemento.getPicture().getPath());
				picture.setSystemName(elemento.getPicture().getSystemName());
				post.setPicture(picture);				
			}

			myPosts.add(post); 
		}
		return myPosts;
	}

	/**
	 * Dada uma figura selecionada por uma pessoa cria um post nessa figura
	 * @param pictureId Id da figura
	 * @param personId Id da pessoa
	 * @param model Model
	 * @return listMyPosts.html
	 */
	//@RequestMapping("/person/{personId}/picture/{pictureId}/post")
	public String createPost(Long pictureId, Long personId) {
		PictureEntity picture = this.pictureService.get(pictureId);
		PersonEntity person = this.personService.get(personId);
		
		//Cria um novo post e associa a uma pessoa e a uma figura
		PostEntity post = new PostEntity();
		post.setPerson(person);
		person.addPost(post);
		post.setPicture(picture);
		picture.setPost(post);

		//Recupera a data corrente do sistema
		String padrao = "yyyy/MM/dd HH:mm:ss";
		String currentData = ManipuladorDatas.getCurrentDataTime(padrao);
		new ManipuladorDatas();
		
		//Salva a data corrente no post
		post.setDate(ManipuladorDatas.getDate());
		
		//Salva o post no repositório de posts
		this.postService.save(post);
		//Atualiza pessoa com o post
		this.personService.update(person);
		//Atualiza foto com o post
		this.pictureService.update(picture);
				
		return "Post criado com sucesso!"; 
	}
	
	/*
	 * 1. Crie um comentário
	 * 2. Faca o post do comentário passando o id do post 
	 * 
	 * /person/post/'} + ${row.id} + ${'/comment'
	 * 
	 * guarde a pessoa logada que está fazendo o comentário
	 * salve esta pessoa no comentário
	 * pegue a data corrente do comentário
	 * salve a data corrente no comentário
	 * 
	 * salve o comentário no post
	 * 
	 * salve o post no repositório de posts
	 *
	 * redirecione para o listMyPost do usuário selecionado com todas as atualizaçòes
	 */ 
	//@RequestMapping(value="/person/{personLogged}/post/{postId}/comment", method = RequestMethod.POST)
	public List<PostEntity> saveCommentInSelectedPost(CommentEntity comment, Long postId, Long personLogged) {
		//Pega o Post selecionado
		PostEntity post = this.postService.get(postId);
		
		//Pega o Dono do post
		PersonEntity personPost = post.getPerson();
		//Pessoa que vai fazer o comentário no post selecionado
		PersonEntity personComment = this.personService.get(personLogged);

		//salva a pessoa que fez o comentario
		comment.setPerson(personComment);
		//Adiciona o comment no repositorio
		this.commentService.save(comment);

		//Adiciona o comment no personComment
		personComment.addComment(comment);

		//Adiciona o comment no post
		post.addComment(comment);
		
		//Atualiza o personComment
		this.personService.update(personComment);
		
		//Atualiza o post
		this.postService.update(post);
				
		//Gera a lista de posts atualizada com o novo comentário
		List<PostEntity> list = personPost.getPosts();
				
		return list;
	}

	/*
	 * 1. Crie um like
	 * 2. Faca o post do like passando o id do post 
	 * 
	 * /person/post/'} + ${row.id} + ${'/likes'
	 * 
	 * guarde a pessoa logada que está fazendo o like
	 * salve esta pessoa no like
	 * pegue a data corrente do like
	 * salve a data corrente no like
	 * 
	 * salve o like no post
	 * 
	 * salve o post no repositório de posts
	 *
	 * redirecione para o listMyPost do usuário selecionado com todas as atualizaçòes
	 */ 
	//@RequestMapping(value="/person/{personLogged}/post/{postId}/likes", method = RequestMethod.POST)
	public String saveLikeInSelectedPost(LikesEntity like, Long postId, Long personLogged) {
		//Pega o Post selecionado
		PostEntity post = this.postService.get(postId);
		//Pega o Dono do post
		PersonEntity personPost = post.getPerson();
		//Pessoa que vai fazer o like no post selecionado
		PersonEntity personLike = this.personService.get(personLogged);

		//Recupera a data corrente do sistema
		new ManipuladorDatas();
		//Salva a data corrente no like
		like.setDate(ManipuladorDatas.getDate());
		like.setPost(post);
		like.setPerson(personLike);
		
		//Adiciona o like no repositorio
		this.likesService.save(like);

		//Adiciona o like no personLike
		personLike.addLike(like);
				
		//Adiciona o like no post
		post.addLike(like);

		//Atualiza o personLike
		this.personService.update(personLike);
		
		//Atualiza o post
		this.postService.update(post);
								
		return "O like foi salvo com sucesso.";
	}


	//@RequestMapping(value="/person/{id}/post/search", method = RequestMethod.POST)
	public String searchMyPosts(Long id, String dates) {
		
		String[] original = dates.split("-");
		String originalFrom = original[0].trim();
		String originalTo = original[1].trim();
		
		Date from = new Date(originalFrom);
		Date to = new Date(originalTo);
	
		PersonEntity person = this.personService.get(id);
		
		if (person.getPosts().size() == 0) {
			//ra.addFlashAttribute("errorFlash", "O usuário " + person.getName() + " ainda não possui posts!");
			return "redirect:/users/list";
		}
		
		List<PostEntity> list = person.getPostByDateFromTo(from, to);
		
		if (list.size() == 0) {
			//ra.addFlashAttribute("errorFlash", "Não existe(m) post(s) nas datas passadas!");
			return "redirect:/users/list";
		}
		
		CommentEntity comment = new CommentEntity();
		LikesEntity likes = new LikesEntity();
		
		return "/person/listMyPosts";
	}
	
	@Put
    public String updatePerson(String personJson) { 
    	Message message = new Message();
    	Gson gson = new Gson();
    	String json;
    	
    	Type pessoaType = new TypeToken<Person>(){}.getType();
    	
    	//recupera o json que representa usuario
    	Person person = gson.fromJson(personJson, pessoaType);
    	
    	if (updatePerson(this.personId, person)) {
    		message.setConteudo("Pessoa alterada com sucesso!");
    		message.setId(200);
    	}else {
    		message.setConteudo("Erro ao alterar pessoa!");
    		message.setId(500);    		
    	}
    	
    	json = gson.toJson(message);
    	return json; 
    }

	private boolean updatePerson(String personId, Person person) {
	    	Message message = new Message();
	    	 
	    	PersonEntity aux = personService.get(Long.parseLong(personId));

	    	if (aux==null) {
	    		message.setConteudo("Pessoa não existe!");
	    		message.setId(404);
	    		return false;
	    	}
	    	
	    	aux.setAddress(person.getAddress());
	    	aux.setCep(person.getCep());
	    	aux.setCity(person.getCity());
	    	aux.setLatitude(person.getLatitude());
	    	aux.setLongitude(person.getLongitude());
	    	aux.setName(person.getName());
	    	aux.setState(person.getState());
		
		personService.update(aux);
		return true;
	}
	
}