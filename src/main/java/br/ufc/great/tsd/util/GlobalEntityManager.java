package br.ufc.great.tsd.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class GlobalEntityManager {
	private final EntityManagerFactory entityManagerFactory;
	
	public EntityManager entityManager;
	
	public GlobalEntityManager(){
		/*CRIANDO O NOSSO EntityManagerFactory COM AS PORPRIEDADOS DO ARQUIVO persistence.xml */
		this.entityManagerFactory = Persistence.createEntityManagerFactory("persistence_unit_db_estudo");
		this.entityManager = this.entityManagerFactory.createEntityManager();
	}

}
