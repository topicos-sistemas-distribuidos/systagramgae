package br.ufc.great.tsd.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import br.ufc.great.tsd.entity.PostEntity;
import br.ufc.great.tsd.util.GlobalEntityManager;

public class PostService{
	EntityManager entityManager;
	
	public PostService(GlobalEntityManager myEntityManager){
		this.entityManager = myEntityManager.entityManager;
	}
	
	public List<PostEntity> getAll() {
		return this.entityManager.createQuery("SELECT p FROM PostEntity p ORDER BY p.id").getResultList();
	}

	public PostEntity get(Long postId) {
		return this.entityManager.find(PostEntity.class, postId);
	}

	public void save(PostEntity post) {
		this.entityManager.getTransaction().begin();
		this.entityManager.persist(post);
		this.entityManager.getTransaction().commit();		
	}

	public void update(PostEntity post) {
		this.entityManager.getTransaction().begin();
		this.entityManager.merge(post);
		this.entityManager.getTransaction().commit();
	}
}
