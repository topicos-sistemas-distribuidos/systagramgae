package br.ufc.great.tsd.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.ufc.great.tsd.entity.PersonEntity;
import br.ufc.great.tsd.util.GlobalEntityManager;

/**
 * Class the manipulate the repository of Person
 * @author armandosoaressousa
 *
 */
public class PersonService{
	
	EntityManager entityManager;
	
	public PersonService(GlobalEntityManager myEntityManager){
		this.entityManager = myEntityManager.entityManager;
	}

	public PersonEntity get(Long id) {
		return this.entityManager.find(PersonEntity.class, id);
	}
	
	public List<PersonEntity> getAll() {
		return this.entityManager.createQuery("SELECT p FROM PersonEntity p ORDER BY p.id").getResultList();
	}

	public void save(PersonEntity person) {
		this.entityManager.getTransaction().begin();
		this.entityManager.persist(person);
		this.entityManager.getTransaction().commit();		
	}

	public void update(PersonEntity person) {
		this.entityManager.getTransaction().begin();
		this.entityManager.merge(person);
		this.entityManager.getTransaction().commit();		
	}
	
	public void delete(long codigo) {
		PersonEntity person = this.get(codigo);
		
		this.entityManager.getTransaction().begin();
		this.entityManager.remove(person);
		this.entityManager.getTransaction().commit();		
	}
	
}