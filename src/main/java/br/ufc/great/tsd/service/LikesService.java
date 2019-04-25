package br.ufc.great.tsd.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.ufc.great.tsd.entity.LikesEntity;

public class LikesService{
	
	private final EntityManagerFactory entityManagerFactory;
	
	private final EntityManager entityManager;
	
	public LikesService(){
		/*CRIANDO O NOSSO EntityManagerFactory COM AS PORPRIEDADOS DO ARQUIVO persistence.xml */
		this.entityManagerFactory = Persistence.createEntityManagerFactory("persistence_unit_db_estudo");
		this.entityManager = this.entityManagerFactory.createEntityManager();
	}

	public List<LikesEntity> getAll() {
		return this.entityManager.createQuery("SELECT l FROM LikesEntity l ORDER BY l.id").getResultList();
	}

	public void save(LikesEntity like) {
		this.entityManager.getTransaction().begin();
		this.entityManager.persist(like);
		this.entityManager.getTransaction().commit();		
	}

	public void update(LikesEntity like) {
		this.entityManager.getTransaction().begin();
		this.entityManager.merge(like);
		this.entityManager.getTransaction().commit();		
	}
}
