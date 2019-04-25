package br.ufc.great.tsd.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.ufc.great.tsd.entity.LikesEntity;
import br.ufc.great.tsd.util.GlobalEntityManager;

public class LikesService{
	
	EntityManager entityManager;
	
	public LikesService(GlobalEntityManager myEntityManager){
		this.entityManager = myEntityManager.entityManager;
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
