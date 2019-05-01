package br.ufc.great.tsd.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.Storage.PredefinedAcl;

@Component
public class FileSaver {
	private Storage storage;
	private StorageOptions storageOptions;
	
	private void init() {
		try {
			storageOptions = StorageOptions.newBuilder()
					.setProjectId("systagramgae")
					.setCredentials(GoogleCredentials.getApplicationDefault()).build();
			System.out.println("Configurações do Bucket carregadas com sucesso!");
		} catch (IOException e) {
			System.out.println("Erro ao carregar as configurações do Bucket.");
			e.printStackTrace();
		}
		storage = storageOptions.getService();
	}
	
	private void initDefaultAutentication() {
		try {
			Storage storage = StorageOptions.getDefaultInstance().getService();
		}catch(Exception ioe) {
			System.out.println("Erro ao carregar as configurações default do Bucket.");
			ioe.printStackTrace();			
		}
	}
					
	public String write(MultipartFile file, String type) {
		BlobInfo blobInfo = null;
		System.out.println("Fazendo o upload do arquivo para o Bucket");
		initDefaultAutentication();
		try {
			if (type.equals("users")) {
				blobInfo = storage.create(BlobInfo.newBuilder("systagramgae.appspot.com", "users/"+file.getOriginalFilename()).build(), file.getBytes(), BlobTargetOption.predefinedAcl(PredefinedAcl.PUBLIC_READ));
			}
			if (type.equals("pictures")) {
				byte[] arrayBytes = file.getBytes();
				Long sizeOfFile = (long) arrayBytes.length;
				BlobId blobId = BlobId.of("systagramgae.appspot.com", "users/"+file.getOriginalFilename());
			    
				blobInfo = BlobInfo.newBuilder(blobId)
						.setContentType("image/png")
						.build();
				
				Blob blob = storage.create(blobInfo, arrayBytes, BlobTargetOption.predefinedAcl(PredefinedAcl.PUBLIC_READ));
			}
		} catch (IOException e) {
			System.out.println("Erro ao acessar o Bucket systagram-bucket");
			e.printStackTrace();
		}
		return blobInfo.getMediaLink();
	}
	
}
