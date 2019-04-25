package br.ufc.great.tsd;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.ufc.great.tsd.entity.CommentEntity;
import br.ufc.great.tsd.entity.LikesEntity;
import br.ufc.great.tsd.entity.PersonEntity;
import br.ufc.great.tsd.entity.PictureEntity;
import br.ufc.great.tsd.entity.PostEntity;
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
import br.ufc.great.tsd.util.ManipuladorDatas;
import br.ufc.great.tsd.util.Message;

public class PersonResource extends ServerResource{
	private PersonService personService = new PersonService();
	private CommentService commentService = new CommentService();
	private PictureService pictureService = new PictureService();
	private PostService postService = new PostService();
	private LikesService likesService = new LikesService();
    private UsersService userService = new UsersService();
    
    String personId;
    String pictureId;
    String id;
    String listPosts;
	String personLogged;
	String postId; 
	
    @Override
    public void doInit() {
    	this.pictureId = getAttribute("pictureId");
    	this.personId = getAttribute("personId");
    	this.id = getAttribute("id");
    	this.listPosts = getAttribute("listPosts");
    	this.personLogged = getAttribute("personLogged");
    	this.postId = getAttribute("postId");
    }

    /**
     * Faz a busca por id de pessoa ou lista todos os usuários cadastrados
     */
    @Get("json")
    public String toString() {
    	Gson gson = new Gson();
    	String json; 
    	String jsonMessage;
    	Message message = new Message();
    	
    	//Mostrar os dados de uma pessoa
    	if (id != null) {
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
    	
    	//Mostrar os posts de uma pessoa
    	if (id != null && listPosts != null) {
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
    	
    	//Dada uma picture selecionada faz a publicacao da mesma
    	if (personId != null && pictureId != null) {
    		this.createPost(Long.valueOf(pictureId), Long.valueOf(personId));
    		
    		message.setConteudo("Post Criado com sucesso!");
			message.setId(200);
			json = gson.toJson(message);
			return json; 
    	}
    	
    	//Lista todas as pessoas
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
    	
		return json;
    }
	
    @Post
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
    
    @Post
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
    		people.add(person);
    	}
    	
        return people;
    }
    
    public Person getPerson(Long id) {
    	PersonEntity personEntity = this.personService.get(id);
    	Person person = new Person();
    	
    	person.setId(personEntity.getId());
    	person.setCep(personEntity.getCep());
    	
    	if(personEntity.getComments() != null) {
        	//person.setComments(personEntity.getComments());    		
    	}

    	person.setAddress(personEntity.getAddress());
    	person.setLatitude(personEntity.getLatitude());
    	person.setLongitude(personEntity.getLongitude());
    	
    	if (personEntity.getLikes() != null) {
        	//TODO: person.setListLikes(personEntity.getLikes());    		
    	}

    	person.setName(personEntity.getName());
    	
    	if (personEntity.getPictures() != null) {
        	//TODO: person.setPictures(personEntity.getPictures());    		
    	}

    	if (personEntity.getPosts() != null) {
    		List<Posts> posts = new LinkedList<>(); 
    		for (PostEntity elemento : personEntity.getPosts()) {
    			Posts post = new Posts();
    			post.setId(elemento.getId());
    			post.setDate(elemento.getDate());
    		}
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
    	//TODO carregar a lista de likes em myLikes
    	
        return myLikes;

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
    	
    	person.addComment(comment, person);
    	
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
    	
    	person.addLike(like, person);
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
			
			if (elemento.getComments() != null) {
				//post.setComments(elemento.getComments());		
				List<Comment> comments = new LinkedList<Comment>();
				for (CommentEntity comment : elemento.getComments()) {
					Comment myComment = new Comment();
					myComment.setDate(comment.getDate());
					myComment.setDescription(comment.getDescription());
					myComment.setId(comment.getId());
					
					Person myPerson1 = new Person();
					if (comment.getPerson() != null) {
						myPerson1.setAddress(comment.getPerson().getAddress());
						myPerson1.setName(comment.getPerson().getName());
						myPerson1.setId(comment.getPerson().getId());
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

		//Recupera a data corrente do sistema
		new ManipuladorDatas();
		//Salva a data corrente no comment
		comment.setDate(ManipuladorDatas.getDate());

		//Adiciona o comment no repositorio
		this.commentService.save(comment);

		//Adiciona o comment no personComment
		personComment.addComment(comment, personComment);
		
		//Atualiza o comment
		this.commentService.update(comment);

		//Atualiza o personComment
		this.personService.update(personComment);
		
		//Adiciona o comment no post
		post.addComment(comment);

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

		//Adiciona o like no repositorio
		this.likesService.save(like);

		//Adiciona o like no personLike
		personLike.addLike(like, personLike);
		
		like.setPost(post);
		
		//Atualiza o like
		this.likesService.update(like);

		//Atualiza o personLike
		this.personService.update(personLike);
		
		//Adiciona o like no post
		post.addLike(like);

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
	
}
