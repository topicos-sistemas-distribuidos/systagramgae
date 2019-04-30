package br.ufc.great.tsd.util;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
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
					.setCredentials(GoogleCredentials.fromStream(new FileInputStream("/Users/armandosoaressousa/git/tsd/systagramgae/systagramgae-e4c769ccac82.json"))).build();
			System.out.println("Configurações do Bucket carregadas com sucesso!");
		} catch (IOException e) {
			System.out.println("Erro ao carregar as configurações do Bucket.");
			e.printStackTrace();
		}
		storage = storageOptions.getService();
	}
				
	public String write(MultipartFile file, String type) {
		BlobInfo blobInfo = null;
		System.out.println("Fazendo o upload do arquivo para o Bucket");
		init();
		try {
			if (type.equals("users")) {
				blobInfo = storage.create(BlobInfo.newBuilder("systagramgae.appspot.com", "users/"+file.getOriginalFilename()).build(), file.getBytes(), BlobTargetOption.predefinedAcl(PredefinedAcl.PUBLIC_READ));
			}
			if (type.equals("pictures")) {
				blobInfo = storage.create(BlobInfo.newBuilder("systagramgae.appspot.com", "uploads/pictures/"+file.getOriginalFilename()).build(), file.getBytes(), BlobTargetOption.predefinedAcl(PredefinedAcl.PUBLIC_READ));
			}
		} catch (IOException e) {
			System.out.println("Erro ao acessar o Bucket systagram-bucket");
			e.printStackTrace();
		}
		return blobInfo.getMediaLink();
	}
	
}
