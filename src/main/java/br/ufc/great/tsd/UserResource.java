package br.ufc.great.tsd;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.ufc.great.tsd.entity.CommentEntity;
import br.ufc.great.tsd.entity.PersonEntity;
import br.ufc.great.tsd.entity.PictureEntity;
import br.ufc.great.tsd.entity.PostEntity;
import br.ufc.great.tsd.entity.UsersEntity;
import br.ufc.great.tsd.http.Person;
import br.ufc.great.tsd.http.Users;
import br.ufc.great.tsd.service.UsersService;
import br.ufc.great.tsd.util.GeradorSenha;
import br.ufc.great.tsd.util.GlobalEntityManager;
import br.ufc.great.tsd.util.Message;

public class UserResource extends ServerResource {
    String userName;
    String email;
    String senha;
    Object user;
    String userId; 
    
    UsersService userService = new UsersService(new GlobalEntityManager());
    
    @Override
    public void doInit() {
        this.userName = getAttribute("user");
        this.userId = getAttribute("id");
        this.user = null; // Could be a lookup to a domain object.
        this.email = getAttribute("email");
        this.senha = getAttribute("senha");
    }

    /**
     * Faz a busca por id de usuário ou lista todos os usuários cadastrados
     */
    @Get("json")
    public String toString() {
    	Gson gson = new Gson();
    	String json=""; 
    	String jsonMessage;
    	Message message = new Message();
    	
    	//Mostrar os dados de um usuario
    	if (userId != null) {
    		Users user = new Users();
    		user = this.getUser(userId); 
    		
    		try {
    			json = gson.toJson(user);
    		}catch(Exception e) {
    			e.printStackTrace();
    			message.setConteudo("Erro ao exibir informação de usuário");
    			message.setId(500);
    			jsonMessage = gson.toJson(message);
    			return jsonMessage; 
    		}
    		return json;
    	}
    	
    	//Dado um e-mail e senha checa se o usuário existe
    	if (email != null && senha != null) {
    		Users user = new Users();
    		user = this.getUserAutenticado(email, senha);
    		
    		try {
    			json = gson.toJson(user);
    		}catch(Exception e) {
    			e.printStackTrace();
    			message.setConteudo("Erro ao exibir informação de usuário");
    			message.setId(500);
    			jsonMessage = gson.toJson(message);
    			return jsonMessage; 
    		}
    		return json;
    	}
    	
    	//lista todos os usuarios cadastrados
    	if (userId == null && email == null && senha==null) {
        	List<Users> users = new ArrayList<Users>();
        	users = this.getAllUsers();
        	
        	try {
        		json = gson.toJson(users);
        	}catch(Exception je) {
    			je.printStackTrace();
    			message.setConteudo("Erro ao exibir informação de lista de usuários");
    			message.setId(500);
    			jsonMessage = gson.toJson(message);
    			return jsonMessage; 
        	}
        	
        	return json;    		
    	}
    	
		return json;
    }
    
    /**
     * Dado o json que representa um usuário faz a inserção do mesmo
     * @param userJson
     * @return mensagem informando se funcionou ou não 
     */
    @Post
    public String newUser(String userJson) {
    	Message message = new Message();
    	Gson gson = new Gson();
    	String json;
    	
    	Type usuarioType = new TypeToken<Users>(){}.getType();
    	
    	//recupera o json que representa usuario
    	Users user = gson.fromJson(userJson, usuarioType);
    	
    	if (addUser(user)) {
    		message.setConteudo("Usuario inserido com sucesso!");
    		message.setId(200);
    	}else {
    		message.setConteudo("Erro ao inserir usuário!");
    		message.setId(500);    		
    	}
    	
    	json = gson.toJson(message);
    	return json; 
    }
    
    @Put
    public String updateUser(String userJson) { 
    	Message message = new Message();
    	Gson gson = new Gson();
    	String json;
    	
    	Type usuarioType = new TypeToken<Users>(){}.getType();
    	
    	//recupera o json que representa usuario
    	Users user = gson.fromJson(userJson, usuarioType);
    	
    	if (updateUser(this.userId, user)) {
    		message.setConteudo("Usuario alterado com sucesso!");
    		message.setId(200);
    	}else {
    		message.setConteudo("Erro ao alterar usuário!");
    		message.setId(500);    		
    	}
    	
    	json = gson.toJson(message);
    	return json; 
    }
    
	 /**
     * Retorna em um JSON todos os usuarios cadastrados
     * @return código http
     */
    public List<Users> getAllUsers() {
		List<Users> users = new ArrayList<Users>();
		List<UsersEntity> listaEntityUsers = userService.getListAll();
		
		for (UsersEntity entity : listaEntityUsers) {
			Users user = new Users();
			user.setId(entity.getId());
			user.setEnabled(true);
			user.setEmail(entity.getEmail());
			user.setPassword(entity.getPassword());
			user.setUsername(entity.getUsername());
			
			if (entity.getPerson() != null) {
				Person person = new Person();
				person.setId(entity.getPerson().getId());
				person.setName(entity.getPerson().getName());
				user.setPerson(person);				
			}
			
			users.add(user);
		}

		return users;
    }
    
    /**
     * Dado um id retorna o JSON dos dados do usuario
     * @param id
     * @return código http
     */
    public Users getUser(String id){    	
    	Users user = new Users();
    	Message message = new Message();
    	
    	UsersEntity aux = new UsersEntity(); 
    	aux = userService.get(Long.parseLong(id));
    	
    	if (aux==null) {
    		message.setConteudo("Usuario não existe!");
    		message.setId(404);
    		return null; 
    	}
    	
    	user.setId(aux.getId());
    	user.setEmail(aux.getEmail());
    	user.setEnabled(true);
    	user.setPassword(aux.getPassword());
    	user.setUsername(aux.getUsername());
    	
    	if (aux.getPerson() != null) {
        	Person person = new Person();
        	person.setId(aux.getPerson().getId());
        	user.setPerson(person);    		
    	}
    	
    	return (user);
    }
    
    /**
     * Dados os dados de um usuario adiciona um usuario no repositorio
     * @param user
     * @return código http
     * 
     * curl -v --header "Content-Type: application/json" --request POST --data '{"username":"novousuario", "password":"novousuario"}' http://localhost:8080/webservicesystagram/rest/users  
     * 
     */
    public boolean addUser(Users user){
    	Message message = new Message();
    	String senhaCriptografada;
    	List<Users> userAux = userService.getUserByUserName(user.getUsername()); 
    	
    	//checa se o usuário já existe
    	if (userAux.size() > 0) {
    		message.setConteudo("Usuario já existe!");
    		message.setId(404);
    		return false; 
    	}
    	
    	if (user.getPassword().length() > 1){
        	senhaCriptografada = new GeradorSenha().criptografa(user.getPassword());
        	user.setPassword(senhaCriptografada);
        	
        	UsersEntity aux = new UsersEntity();
        	aux.setEmail(user.getEmail());
        	aux.setEnabled(true);
        	aux.setId(user.getId());
        	aux.setPassword(user.getPassword());
        	aux.setUsername(user.getUsername());
        	
        	PersonEntity person = new PersonEntity(); 
        	person.setUser(aux);
        	person.setName(aux.getUsername());
        	aux.setPerson(person);
        	
            userService.save(aux);
            
            URI uri = URI.create("/" + String.valueOf(user.getId()));
            return true;
    	}else{
    		message.setConteudo("Informe uma senha para o usuário");
    		message.setId(400);
    		return false;
    	}
    }
    
    /**
     * Dado um id e os dados do user faz sua atualizacao
     * @param id
     * @param user
     * @return código http
     */
    public boolean updateUser(String id, Users user){
    	Message message = new Message();
    	
    	Users userAux = null; 
    	UsersEntity aux = userService.get(Long.parseLong(id));

    	if (aux==null) {
    		message.setConteudo("Usuário não existe!");
    		message.setId(404);
    		return false;
    	}
    	
    	aux.setEmail(user.getEmail());
    	aux.setEnabled(true);
    	aux.setPassword(user.getPassword());
    	aux.setUsername(user.getUsername());
    	
    	userService.update(aux);
    	return true;
    }

    /**
     * Dado um id de um usuario faz sua remocao do repositorio
     * @param id
     * @return código http
     */
    public String deleteUser(String id){
    	Message message = new Message(); 
    	UsersEntity user = userService.get(Long.parseLong(id));
    	
    	if (user == null){
    		message.setConteudo("Usuário não existe!");
    		message.setId(404);
    		return null;
    	}

    	userService.delete(Long.parseLong(id));
        return "";
    }
    
    /**
     * Dado email e senha retorna o JSON dos dados do usuario
     * @param 
     * @return código http
     */
    public Users getUserAutenticado(String email,String senha){
    	List<Users> user = userService.getUserByEmail(email);
    	Message message = new Message();
    	
    	//consulta o usuário por email e se existe retorna os dados do usuário
    	if (user.size() > 0) { //usuário existe    		
    		Iterator iter = user.iterator();
    		Object first = iter.next();
        		
    		String senhaTeste = ((UsersEntity) first).getPassword(); 
    		boolean checaSenha = new GeradorSenha().comparaSenhas(senha, senhaTeste);
        	if (senha.length() >0 && checaSenha){
        		Users aux = new Users();
        		aux.setEmail(((UsersEntity) first).getEmail());
        		aux.setEnabled(true);
        		aux.setId(((UsersEntity) first).getId());
        		aux.setPassword(((UsersEntity) first).getPassword());
        		aux.setUsername(((UsersEntity) first).getUsername());
                return (aux) ;	
        	}else{        		
        		message.setId(1);
        		message.setConteudo("Senha incorreta!");
                return null;	    		
        	}    	    		
    	}else {
    		message.setId(404);
    		message.setConteudo("Usuario não existe!");
            return null;	    		
    	}    	
    }

}