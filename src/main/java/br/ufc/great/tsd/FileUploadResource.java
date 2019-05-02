package br.ufc.great.tsd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;

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
	String pathImage;
	String systemName;
	private Storage storage;
	private StorageOptions storageOptions;
	String bucket;

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
    
    @Post
    public Representation handleUpload(Representation entity){
    	Message message = new Message();
    	Gson gson = new Gson();
    	String json="";
    	
    	if (entity != null) {
        		PictureEntity picture = new PictureEntity();
        		
        		setPathAndSystemName(Long.valueOf(personId));
        		PersonEntity person = savePictureAndPerson(Long.valueOf(personId), picture);				

        		DiskFileItemFactory factory = new DiskFileItemFactory();
        		factory.setSizeThreshold(10000240);
        		RestletFileUpload upload = new RestletFileUpload(factory);
        		
        		try {
        			FileItemIterator fileIterator = upload.getItemIterator(entity);

        			while (fileIterator.hasNext()) {
        				FileItemStream fi = fileIterator.next();
        				String fieldName = fi.getFieldName();
        				String contentType = fi.getContentType(); 
        				FileItemHeaders metainfo = fi.getHeaders();
        				        		
        				String bucket = new Constantes().bucketPrincipal;
						this.uploadFile(fi, bucket);
        				message.setConteudo("Arquivo inserido com sucesso!");
        				message.setId(200);
        			}
				} catch (FileUploadException | IOException e) {
					e.printStackTrace();
				}
    	} else {
    		throw new ResourceException(500);
    	}

    	json = gson.toJson(message);
    	return httpSuccess(); 
    }

    public Representation handleUploadTeste(Representation entity) throws IOException, ServletException{
    	org.restlet.Request restletRequest = getRequest();
    	HttpServletRequest req = ServletUtils.getRequest(restletRequest);

    	String bucket = "systagramgae.appspot.com"; 
    	Part filePart = req.getPart("file");
    	final String fileName = filePart.getSubmittedFileName();
    	String imageURL = req.getParameter("img_mypicture");
    	
		setPathAndSystemName(Long.valueOf(personId));
		
    	PictureEntity picture=null;
		PersonEntity person = savePictureAndPerson(Long.valueOf(personId), picture);				
		
    	if(fileName != null && !fileName.isEmpty() && fileName.contains(".")) {
    		final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
    		String[] allowedExt = {"jpg", "jpeg", "png", "gif" };
    		for (String s : allowedExt) {
    			if (extension.equals(s)) {
    				//System.out.println(this.uploadFile(filePart, bucket));
    			}
    		}
    		throw new ServletException("O arquivo deve ser uma imagem");
    	}    	
    	return null;
    }
        
    private String uploadFile(FileItemStream fileStream, String bucket) throws IOException{
		final String fileName = this.systemName + ".png";
		
		Storage storage = StorageOptions.getDefaultInstance().getService();
		BlobInfo blobInfo = 
				storage.create(
						BlobInfo
						.newBuilder(bucket, "uploads/pictures/"+fileName)
						.setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
						.setContentType("image/png")
						.build(),
						fileStream.openStream());
		
		return blobInfo.getMediaLink();
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
	 * Dada uma pessoa e os dados de Picture salva tais informacoes no banco
	 * @param personId
	 * @param picture
	 * @return
	 */
	private PersonEntity savePictureAndPerson(Long personId, PictureEntity picture) {
		picture.setPath(pathImage);
    	picture.setSystemName(systemName);    	
    	//Dono da imagem
    	PersonEntity person = this.personService.get(personId);    	
    	picture.setPerson(person);
    	this.pictureService.save(picture);
    	//Associa picture a pessoa
    	person.addPicture(picture);    	    	
    	personService.update(person);
		return person;
	}

	/**
	 * Dado um arquivo transforma em Multipart
	 * @param newFile
	 * @param multipartFileToSend
	 * @return
	 */
	private MultipartFile transformFileToMultipart(File newFile, MultipartFile multipartFileToSend) {
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
		return multipartFileToSend;
	}

	/**
	 * Transforma uma imagem qualquer em png para padronizar as imagens do sistema
	 * @param files
	 * @param newFile
	 */
	private void transformImagetoPNG(File files, File newFile) {
		try {
			FileChannel src = new FileInputStream(files).getChannel();
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
	}

	/**
	 * Atualiza as variaveis pathImage e systemName quando uma nova picture é salva
	 * @param personId
	 */
	private void setPathAndSystemName(Long personId) {
		new Constantes(); 	  
		String idAux = String.valueOf(personId);
		String padrao = "yyyy/MM/dd HH:mm:ss";
		
		new ManipuladorDatas();
		String currentData = ManipuladorDatas.getCurrentDataTime(padrao);
		String dataAux = currentData.replace("/", "-");
		String data1 = dataAux.replaceAll(":", "-").trim();
		String data = data1.replace(" ", "-");
		
		//Define o diretório da imagem e o nome do arquivo que será salvo no filesystem
		pathImage = Constantes.s3awsurl + "uploads/pictures/" + idAux + "-" + data + ".png";
		systemName = idAux + "-" + data;
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