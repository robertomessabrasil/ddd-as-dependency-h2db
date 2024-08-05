package io.github.robertomessabrasil.dddad.infra.h2db.repository.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Transaction implements ITransaction {
    private static final String PERSISTENT_UNIT = "io.github.robertomessabrasil.dddad.infra.h2db";
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENT_UNIT);
    private EntityManager entityManager;

    @Override
    public void begin() {
        this.entityManager.getTransaction().begin();
    }

    @Override
    public void commit() {
        if (this.entityManager.getTransaction().isActive()) {
            this.entityManager.getTransaction().commit();
        }
    }

    @Override
    public void rollBack() {
        if (this.entityManager.getTransaction().isActive()) {
            this.entityManager.getTransaction().rollback();
        }
    }

    public void close() {
        this.entityManagerFactory.close();
    }

    public Transaction() {
        this.entityManager = this.entityManagerFactory.createEntityManager();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
