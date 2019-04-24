package br.ufc.great.tsd.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.ufc.great.tsd.entity.PostEntity;

public class PostService{

	private final EntityManagerFactory entityManagerFactory;
	
	private final EntityManager entityManager;
	
	public PostService(){
		/*CRIANDO O NOSSO EntityManagerFactory COM AS PORPRIEDADOS DO ARQUIVO persistence.xml */
		this.entityManagerFactory = Persistence.createEntityManagerFactory("persistence_unit_db_estudo");
		this.entityManager = this.entityManagerFactory.createEntityManager();
	}

	public PostEntity get(Long postId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(PostEntity post) {
		// TODO Auto-generated method stub
		
	}

	public void update(PostEntity post) {
		// TODO Auto-generated method stub
		
	}
}
