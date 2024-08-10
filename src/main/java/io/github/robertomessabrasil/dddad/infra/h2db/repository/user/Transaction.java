package io.github.robertomessabrasil.dddad.infra.h2db.repository.user;

import io.github.robertomessabrasil.dddad.domain.exception.InfrastructureException;
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
        this.session = this.sessionFactory.openSession();
        this.transaction = session.beginTransaction();
    }

    @Override
    public void commit() {
        if (this.transaction.isActive()) {
            this.transaction.commit();
        }
        if (this.session != null) {
            this.session.close();
        }
    }

    @Override
    public void rollBack() {
        if (this.transaction.isActive()) {
            this.transaction.rollback();
        }
        if (this.session != null) {
            this.session.close();
        }
    }

    public void close() {
        if (this.registry != null) {
            StandardServiceRegistryBuilder.destroy(this.registry);
        }
    }

    public Transaction() throws InfrastructureException {
        try {
            this.registry = new StandardServiceRegistryBuilder().configure().build();
            MetadataSources sources = new MetadataSources(this.registry);
            Metadata metadata = sources.getMetadataBuilder().build();
            this.sessionFactory = metadata.getSessionFactoryBuilder().build();
        } catch (Exception e) {
            if (this.registry != null) {
                StandardServiceRegistryBuilder.destroy(this.registry);
            }
            throw new InfrastructureException(e);
        }
    }

    public Session getSession() {
        return session;
    }
}
