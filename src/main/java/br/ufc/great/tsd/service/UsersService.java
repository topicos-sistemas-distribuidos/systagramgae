package br.ufc.great.tsd.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.ufc.great.tsd.entity.UsersEntity;
import br.ufc.great.tsd.http.Users;
import br.ufc.great.tsd.util.GlobalEntityManager;

/**
 * Classe de serviço para consumir o repositório de dados de Usuário
 * @author armandosoaressousa
 *
 */
public class UsersService{
	
	EntityManager entityManager;
	
	public UsersService(GlobalEntityManager myEntityManager){
		this.entityManager = myEntityManager.entityManager;
	}

	public UsersEntity get(long codigo) {
		return this.entityManager.find(UsersEntity.class, codigo);
	}

	public List<UsersEntity> getListAll() {
		return this.entityManager.createQuery("SELECT u FROM UsersEntity u ORDER BY u.username").getResultList();
	}

	public void save(UsersEntity user) {
		this.entityManager.getTransaction().begin();
		this.entityManager.persist(user);
		this.entityManager.getTransaction().commit();		
	}

	public void update(UsersEntity user){
		this.entityManager.getTransaction().begin();
		this.entityManager.merge(user);
		this.entityManager.getTransaction().commit();
	}
	
	public void delete(long codigo) {
		UsersEntity user = this.get(codigo);
		
		this.entityManager.getTransaction().begin();
		this.entityManager.remove(user);
		this.entityManager.getTransaction().commit();		
	}

	public List<Users> getUserByUserName(String username) {
		return this.entityManager.createQuery("SELECT u FROM UsersEntity u where u.username='"+username + "'").getResultList();
	}

	public List<Users> getUserByEmail(String email) {
		return this.entityManager.createQuery("SELECT u FROM UsersEntity u where u.email='"+email+ "'").getResultList();
	}

}