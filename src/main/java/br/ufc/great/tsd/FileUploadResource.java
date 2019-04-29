package br.ufc.great.tsd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.resource.Status;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.ufc.great.tsd.entity.PersonEntity;
import br.ufc.great.tsd.entity.PictureEntity;
import br.ufc.great.tsd.entity.UsersEntity;
import br.ufc.great.tsd.http.Person;
import br.ufc.great.tsd.http.Picture;
import br.ufc.great.tsd.http.Posts;
import br.ufc.great.tsd.service.PersonService;
import br.ufc.great.tsd.service.PictureService;
import br.ufc.great.tsd.service.UsersService;
import br.ufc.great.tsd.util.Constantes;
import br.ufc.great.tsd.util.FileSaver;
import br.ufc.great.tsd.util.GlobalEntityManager;
import br.ufc.great.tsd.util.ManipuladorDatas;
import br.ufc.great.tsd.util.Message;

import org.springframework.mock.web.MockMultipartFile;

public class FileUploadResource extends ServerResource {
	private GlobalEntityManager myEntityManager = new GlobalEntityManager();
	private UsersService userService;
	private PersonService personService;
	private PictureService pictureService;
	private FileSaver fileSaver = new FileSaver(); 
	private String id;
	private String userId;
	private String personId;
	private MultipartFile files;

	public FileUploadResource() {
		userService = new UsersService(myEntityManager);
		personService = new PersonService(myEntityManager);
		pictureService = new PictureService(myEntityManager);		// TODO Auto-generated constructor stub
	}
	
    @Override
    public void doInit() {
        this.id = getAttribute("id");
        this.userId = getAttribute("userId");
        this.personId = getAttribute("personId");
        this.files = null;
    }
    
    private Representation httpSuccess() {
        // FIXME StringRepresentation is probably not the right choice as we aren't returning a body
        return new StringRepresentation("200");
    }
	
    @Get("json")
    public String toString() {
    	Gson gson = new Gson();
    	String json = null; 
    	String jsonMessage;
    	Message message = new Message();
    	
    	if (id != null && listMyPictures(Long.valueOf(id)).size() > 0) {
    		//pega os dados da pessoa e retorna a lista de pictures, em formato JSON, da pessoa
    		List<Picture> pictures = new ArrayList<Picture>();
    		List<PictureEntity> list = listMyPictures(Long.valueOf(id));
    		
    		for (PictureEntity elemento : list) {
    			Picture picture = new Picture();
    			picture.setId(elemento.getId());
    			picture.setName(elemento.getName());
    			picture.setPath(elemento.getPath());
    			//set person
    			Person person = new Person();
    			if (elemento.getPerson() != null) {
        			person.setId(elemento.getPerson().getId());
        			person.setName(elemento.getPerson().getName());
        			picture.setPerson(person);
    			}    			
    			//set post
    			Posts post = new Posts();
    			if (elemento.getPost() != null) {
        			post.setId(elemento.getPost().getId());
        			picture.setPost(post);    				
    			}
    			picture.setSystemName(elemento.getSystemName());
    			pictures.add(picture);
    		}
    		json = gson.toJson(pictures);
    		return json;
    	}else {
			message.setConteudo("Erro ao exibir lista de pictures de usuário");
			message.setId(500);
			jsonMessage = gson.toJson(message);
			return jsonMessage; 
    	}    
    	
    }
    
    /**
     * Dado o json que representa um usuário faz o upload de uma picture do mesmo
     * @param userJson
     * @return mensagem informando se funcionou ou não 
     */
    @Post
    public Representation handleUpload(String id, Representation entity){
    	Message message = new Message();
    	Gson gson = new Gson();
    	String json;

//    	if (entity != null && MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
    	if (entity != null) {
    		PictureEntity picture = new PictureEntity();
    		this.files = (MultipartFile) entity;

    		this.uploadPicture(Long.valueOf(personId), picture, files);
    		message.setConteudo("Arquivo inserido com sucesso!");
    		message.setId(200);

    	} else {
    		throw new ResourceException(500);
    	}

    	json = gson.toJson(message);
    	return httpSuccess(); 
    }

    	
	/**
	 * Carrega a página contendo todas a fotos do usuário
	 * @param id Id da pessoa
	 * @param model Model
	 * @return listMypictures
	 */
	//@RequestMapping("/upload/person/{id}/picture")
	public List<PictureEntity> listMyPictures(Long id) {
		PersonEntity person = this.personService.get(id);
		
		if (person.getPictures().size() == 0) {
			return null; 
		}
		
		List<PictureEntity> list = person.getPictures();
				
		return list;
	}
	
	/**
	 * Renomeia um arquivo
	 * @param file arquivo
	 * @param newName novo nome
	 * @return Arquivo com novo nome
	 * @throws IOException
	 */
	public File renameFile(File file, String newName) throws IOException {
		// File (or directory) with new name
		File file2 = new File(newName);

		if (file2.exists())
			throw new java.io.IOException("file exists");

		// Rename file (or directory)
		boolean success = file.renameTo(file2);

		if (success) {
			return file;
		}else {
			return null;
		}

	}

	/**
	 * Converte um MultipartFile em File
	 * @param file
	 * @return
	 */
	public File convert(MultipartFile file)
	{    
	    File convFile = null;
		try {
			convFile = new File(file.getOriginalFilename());
			convFile.createNewFile(); 
			FileOutputStream fos = new FileOutputStream(convFile); 
			fos.write(file.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	    
	    return convFile;
	}
	
	/**
	 * Faz o upload de uma imagem do usuário para o bucket de usuario
	 * @param idUser Id do Usuário
	 * @param model Model
	 * @param files Array de Arquivos
	 * @return Formulário do Usuário
	 */
	//@RequestMapping("/upload/selected/image/users/{idUser}") @RequestParam("photouser") 
	public String upload(Long idUser, MultipartFile files) {
		new Constantes(); 	  
		String idAux = String.valueOf(idUser);		
		String bucketName = Constantes.bucketPrincipal;
		
		File myFile = convert(files);
		File newFile = new File(idAux+".png");			
		
		//Transforma uma imagem qualquer em png para padronizar as imagens do sistema
		try {
			FileChannel src = new FileInputStream(myFile).getChannel();
			FileChannel dest = new FileOutputStream(newFile).getChannel();
			dest.transferFrom(src, 0, src.size());
			System.out.println("Arquivo transformado em .png com sucesso!");
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo nao existe!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Erro ao transformar arquivo em .png.");
			e.printStackTrace();
		}
		
		MultipartFile multipartFileToSend=null;
		try {
			InputStream stream =  new FileInputStream(newFile);
			multipartFileToSend = new MockMultipartFile(idAux+".png", newFile.getName(), MediaType.IMAGE_PNG_VALUE, stream);
			System.out.println("Multipart transformado em arquivo com sucesso!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERRO: no Multipart transformado em arquivo.");
			e.printStackTrace();
		}	    
		
		if (!files.isEmpty()){
			String path = fileSaver.write(multipartFileToSend, "users");				
		}

		UsersEntity user = this.userService.get(idUser);

		//model.addAttribute("successFlash", "Successfully uploaded files " + newFile.getName());
		//model.addAttribute("s3awsurl", new Constantes().s3awsurl);

		return "uploads/formpwd";
	}
	
	/**
	 * Faz o upload de fotos da pessoa para seu album de fotos
	 * @param personId id da Pessoa
	 * @param model Model
	 * @param files arquivos de imagens que serão carregados para o album de fotos
	 * @return carrega listMyPictures.html
	 */
	//@RequestMapping(value="/upload/selected/picture/person/{personId}") @RequestParam("photouser")
	public String uploadPicture(Long personId, PictureEntity picture,  MultipartFile files) {
		new Constantes();
		String uploadFilePath = Constantes.picturesDirectory; 	  
		String idAux = String.valueOf(personId);
		String padrao = "yyyy/MM/dd HH:mm:ss";
		
		new ManipuladorDatas();
		String currentData = ManipuladorDatas.getCurrentDataTime(padrao);
		String dataAux = currentData.replace("/", "-");
		String data1 = dataAux.replaceAll(":", "-").trim();
		String data = data1.replace(" ", "-");
		
		//Define o diretório da imagem e o nome do arquivo que será salvo no filesystem
		String pathImage = uploadFilePath + FileSystems.getDefault().getSeparator() + idAux + "-" + data + ".png";
		String systemName = idAux + "-" + data;
		
    	picture.setPath(pathImage); //TODO: precisa corrigir para pegar a url da image no bucket
    	picture.setSystemName(systemName);
    	
    	//Dono da imagem
    	PersonEntity person = this.personService.get(personId);
    	person.addPicture(picture, person);    	    	
    	personService.save(person);				
		
		File myFile = convert(files);
		File newFile = new File(systemName +".png");			
		
		//Transforma uma imagem qualquer em png para padronizar as imagens do sistema
		try {
			FileChannel src = new FileInputStream(myFile).getChannel();
			FileChannel dest = new FileOutputStream(newFile).getChannel();
			dest.transferFrom(src, 0, src.size());
			System.out.println("Arquivo transformado em .png com sucesso!");
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo nao existe!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Erro ao transformar arquivo em .png.");
			e.printStackTrace();
		}
		
		MultipartFile multipartFileToSend=null;
		try {
			InputStream stream =  new FileInputStream(newFile);
			multipartFileToSend = new MockMultipartFile(systemName + ".png", newFile.getName(), MediaType.IMAGE_PNG_VALUE, stream);
			System.out.println("Multipart transformado em arquivo com sucesso!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERRO: no Multipart transformado em arquivo.");
			e.printStackTrace();
		}	    
		
		if (!files.isEmpty()){
			String path = fileSaver.write(multipartFileToSend, "pictures");				
		}

		
		//List de fotos da pessoa
		List<PictureEntity> list = person.getPictures();

		//model.addAttribute("successFlash", "Successfully uploaded files " + newFile.getName());
		//model.addAttribute("s3awsurl", new Constantes().s3awsurl);

		return "/uploads/listMyPictures";
	}

	/**
	 * Carrega um array de bytes de uma foto
	 * @param imageName identificador interno da foto
	 * @return array de bytes do arquivo da foto
	 * @throws IOException
	 */
	//@RequestMapping(value = "/upload/picture/{pictureName}")
	//@ResponseBody
	public byte[] getPicture(String imageName) throws IOException {
		new Constantes();
		String uploadFilePath = Constantes.picturesDirectory;

		File serverFile = new File(uploadFilePath + FileSystems.getDefault().getSeparator() + imageName + ".png");
		
		if (serverFile.length() > 0) {
			return Files.readAllBytes(serverFile.toPath());
		}else {
			return null;
		}
		
	}

	/**
	 * Carrega um array de bytes de uma imagem de um usuário
	 * @param imageName id do Usuário
	 * @return array de bytes do arquivo da imagem
	 * @throws IOException
	 */	
	//@RequestMapping(value = "/upload/image/users/{imageName}")
	//@ResponseBody
	public byte[] getUserImage(String imageName) throws IOException {
		new Constantes();
		String uploadFilePath = Constantes.uploadUserDirectory;

		File serverFile = new File(uploadFilePath + FileSystems.getDefault().getSeparator() + imageName + ".png");      
		File userPadrao = new File(uploadFilePath + FileSystems.getDefault().getSeparator() + "anonymous2.png");

		if (serverFile.length() > 0) {
			return Files.readAllBytes(serverFile.toPath());	  
		}else {		  
			return Files.readAllBytes(userPadrao.toPath());
		}

	}


	//@RequestMapping(value = "/upload/image/{imageName}")
	//@ResponseBody
	public byte[] getImage(String imageName) throws IOException {
		new Constantes();
		String uploadFilePath = Constantes.uploadDirectory;

		File serverFile = new File(uploadFilePath + FileSystems.getDefault().getSeparator() + imageName + ".png");
		return Files.readAllBytes(serverFile.toPath());
	}

	/**
	 * Dada uma foto selecionada de uma pessoa, remove essa foto
	 * @param pictureId Id da pessoa
	 * @param personId Id da fota
	 * @param ra mensagem de retorno
	 * @return listMyPictures
	 */
	//@RequestMapping(value="/upload/delete/picture/{pictureId}/person/{personId}")
	public String deletePictureFromPerson(String pictureId, String personId) {
		
		PersonEntity person = this.personService.get(Long.parseLong(personId));
		PictureEntity picture = this.pictureService.get(Long.valueOf(pictureId));
		
		if (person.getPictures().remove(picture)) {
			this.personService.save(person);
			this.pictureService.delete(picture.getId());
			//ra.addFlashAttribute("successFlash", "A foto " + pictureId + " foi removida com sucesso!");
		}else {
			//ra.addFlashAttribute("errorFlash", "A foto " + pictureId + " NÁO foi removida.");
		}
		
		return "redirect:/upload/person/"+ personId + "/picture";
	}
	
}