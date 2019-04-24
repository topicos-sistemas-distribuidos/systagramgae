package br.ufc.great.tsd;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.restlet.resource.ServerResource;

import br.ufc.great.tsd.entity.CommentEntity;
import br.ufc.great.tsd.entity.LikesEntity;
import br.ufc.great.tsd.entity.PersonEntity;
import br.ufc.great.tsd.entity.PictureEntity;
import br.ufc.great.tsd.entity.PostEntity;
import br.ufc.great.tsd.http.Comment;
import br.ufc.great.tsd.http.Likes;
import br.ufc.great.tsd.http.Person;
import br.ufc.great.tsd.service.CommentService;
import br.ufc.great.tsd.service.LikesService;
import br.ufc.great.tsd.service.PersonService;
import br.ufc.great.tsd.service.PictureService;
import br.ufc.great.tsd.service.PostService;
import br.ufc.great.tsd.util.ManipuladorDatas;

public class PersonResource extends ServerResource{

	private PersonService personService = new PersonService();
	private CommentService commentService = new CommentService();
	private PictureService pictureService = new PictureService();
	private PostService postService = new PostService();
	private LikesService likesService = new LikesService();
			
	/**
	 * Lista todas as pessoas cadastradas no sistema
	 * @param model
	 * @return página com a lista de pessoas cadastradas
	 */
    //@RequestMapping(value="/person", method = RequestMethod.GET)
    public List<Person> index() {
    	List<Person> people = new ArrayList<Person>();
    	
    	List<PersonEntity> list = this.personService.getAll();
    	//TODO carregar a lista de pessoas em people
    	
        return people;
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
    	//TODO carregar a lista de comentarios em myComents
    	
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
     * Dada uma pessoa e um novo comentário mostra o formulário para inserir comentário
     * @param id Id da pessoa
     * @param model Model
     * @return formComment.html
     */
    //@RequestMapping(value="/person/{id}/comment/add")
    public String addMyComment(Long id) {
    	
    	PersonEntity person = this.personService.get(id);
    	List<CommentEntity> comments = person.getComments();
    	    	
    	return "person/formComment";
    }

    /**
     * Dada uma pessoa e um novo like mostra o formulário para inserir comentário
     * @param id Id da pessoa
     * @param model Model
     * @return formLike.html
     */
    //@RequestMapping(value="/person/{id}/likes/add")
    public String addMyLike(Long id) {
    	
    	PersonEntity person = this.personService.get(id);
    	
    	List<LikesEntity> likes = person.getLikes();
    	    	
    	return "person/formLikes";
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
	public String listMyPosts(Long id) {
		PersonEntity person = this.personService.get(id);
		
		if (person.getPosts().size() == 0) {
			//ra.addFlashAttribute("errorFlash", "O usuário " + person.getName() + " ainda não possui posts!");
			return "redirect:/users/list";
		}
		
		List<PostEntity> list = person.getPosts();
		

		CommentEntity comment = new CommentEntity();
		LikesEntity likes = new LikesEntity();
		
		return "/person/listMyPosts";
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
		
		//Carrega os dados do usuário logado
		
		//Salva o post no repositório de posts
		this.postService.save(post);
		//Atualiza pessoa com o post
		this.personService.update(person);
		//Atualiza foto com o post
		this.pictureService.update(picture);
		
		List<PostEntity> list = person.getPosts();
		
		CommentEntity comment = new CommentEntity();
		
		//ra.addFlashAttribute("successFlash", "Post da imagem criado com sucesso!");
		
		return "redirect:/person/" + person.getId() + "/post"; 
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
	public String saveCommentInSelectedPost(CommentEntity comment, Long postId, Long personLogged) {

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
		
		CommentEntity newComment = new CommentEntity();
		
		//ra.addFlashAttribute("successFlash", "O comentário foi salvo com sucesso.");
		
		return "redirect:/person/"+ personPost.getId() + "/post";
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
		//Salva a data corrente no comment
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
		
		//Gera a lista de posts atualizada com o novo like
		List<PostEntity> list = personPost.getPosts();
		
		CommentEntity newComment = new CommentEntity();
		LikesEntity newLike = new LikesEntity();
						
		//ra.addFlashAttribute("successFlash", "O like foi salvo com sucesso.");
		
		return "redirect:/person/"+ personPost.getId() + "/post";
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
