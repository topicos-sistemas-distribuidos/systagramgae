package br.ufc.great.tsd.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.ufc.great.tsd.entity.PictureEntity;

public class PictureService{

	private final EntityManagerFactory entityManagerFactory;
	
	private final EntityManager entityManager;
	
	public PictureService(){
		/*CRIANDO O NOSSO EntityManagerFactory COM AS PORPRIEDADOS DO ARQUIVO persistence.xml */
		this.entityManagerFactory = Persistence.createEntityManagerFactory("persistence_unit_db_estudo");
		this.entityManager = this.entityManagerFactory.createEntityManager();
	}

	
	public PictureEntity get(Long pictureId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void update(PictureEntity picture) {
		// TODO Auto-generated method stub
		
	}
}
