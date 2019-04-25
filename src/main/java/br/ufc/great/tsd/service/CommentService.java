package br.ufc.great.tsd.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.ufc.great.tsd.entity.CommentEntity;
import br.ufc.great.tsd.util.GlobalEntityManager;

public class CommentService{
	EntityManager entityManager;
	
	public CommentService(GlobalEntityManager myEntityManager){
		this.entityManager = myEntityManager.entityManager;
	}

	public List<CommentEntity> getAll() {
		return this.entityManager.createQuery("SELECT c FROM CommentEntity c ORDER BY c.id").getResultList();
	}

	public void save(CommentEntity comment) {
		this.entityManager.getTransaction().begin();
		this.entityManager.persist(comment);
		this.entityManager.getTransaction().commit();		
	}
	
	public void update(CommentEntity comment) {
		this.entityManager.getTransaction().begin();
		this.entityManager.merge(comment);
		this.entityManager.getTransaction().commit();		
	}

}
