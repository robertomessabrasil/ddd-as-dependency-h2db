package io.github.robertomessabrasil.dddad.infra.h2db.repository.user;

public interface ITransaction {
    void begin();
    void commit();
    void rollBack();
}
