package io.github.robertomessabrasil.dddad.infra.h2db.repository.user;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Transaction implements ITransaction {
    private StandardServiceRegistry registry;
    private SessionFactory sessionFactory;
    private org.hibernate.Transaction transaction;
    private Session session;

    @Override
    public void begin() {
        this.transaction = session.beginTransaction();
    }

    @Override
    public void commit() {
        if (this.transaction.isActive()) {
            this.transaction.commit();
        }
    }

    @Override
    public void rollBack() {
        if (this.transaction.isActive()) {
            this.transaction.rollback();
        }
    }

    public void close() {
        if (this.session != null) {
            this.session.close();
        }
        if (this.registry != null) {
            StandardServiceRegistryBuilder.destroy(this.registry);
        }
    }

    public Transaction() {
        try {
            this.registry = new StandardServiceRegistryBuilder().configure().build();
            MetadataSources sources = new MetadataSources(this.registry);
            Metadata metadata = sources.getMetadataBuilder().build();
            this.sessionFactory = metadata.getSessionFactoryBuilder().build();
            this.session = this.sessionFactory.openSession();
        } catch (HibernateException e) {
            if (this.registry != null) {
                StandardServiceRegistryBuilder.destroy(this.registry);
            }
            throw e;
        }
    }

    public Session getSession() {
        return this.session;
    }
}
