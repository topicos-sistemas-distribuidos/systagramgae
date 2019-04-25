package br.ufc.great.tsd.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.ufc.great.tsd.entity.PersonEntity;
import br.ufc.great.tsd.entity.PictureEntity;
import br.ufc.great.tsd.entity.UsersEntity;

public class PictureService{

	private final EntityManagerFactory entityManagerFactory;
	
	private final EntityManager entityManager;
	
	public PictureService(){
		/*CRIANDO O NOSSO EntityManagerFactory COM AS PORPRIEDADOS DO ARQUIVO persistence.xml */
		this.entityManagerFactory = Persistence.createEntityManagerFactory("persistence_unit_db_estudo");
		this.entityManager = this.entityManagerFactory.createEntityManager();
	}

	public PictureEntity get(Long pictureId) {
		return this.entityManager.find(PictureEntity.class, pictureId);
	}
	
	public List<PictureEntity> getAll() {
		return this.entityManager.createQuery("SELECT p FROM PictureEntity p ORDER BY p.id").getResultList();
	}
	
	public void save(PictureEntity picture) {
		this.entityManager.getTransaction().begin();
		this.entityManager.persist(picture);
		this.entityManager.getTransaction().commit();		
	}

	public void update(PictureEntity picture) {
		this.entityManager.getTransaction().begin();
		this.entityManager.merge(picture);
		this.entityManager.getTransaction().commit();		
	}

	public void delete(Long id) {
		PictureEntity picture = this.get(id);
		
		this.entityManager.getTransaction().begin();
		this.entityManager.remove(picture);
		this.entityManager.getTransaction().commit();		
	}
}
