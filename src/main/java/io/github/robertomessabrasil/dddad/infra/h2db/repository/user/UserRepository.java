package io.github.robertomessabrasil.dddad.infra.h2db.repository.user;

import io.github.robertomessabrasil.dddad.entity.user.UserEntity;
import io.github.robertomessabrasil.dddad.entity.user.UserRoleEnum;
import io.github.robertomessabrasil.dddad.entity.user.UserRoleVO;
import io.github.robertomessabrasil.dddad.infra.h2db.repository.user.entity.UserJPAEntity;
import io.github.robertomessabrasil.dddad.repository.IUserRepository;
import io.github.robertomessabrasil.dddad.repository.event.UserRepositoryEvent;
import io.github.robertomessabrasil.jwatch.exception.InterruptException;
import io.github.robertomessabrasil.jwatch.observer.EventObserver;

import java.util.Optional;

public class UserRepository implements IUserRepository {
    private Transaction transaction;

    @Override
    public UserEntity create(UserEntity userEntity, EventObserver eventObserver) {
        UserJPAEntity userJPAEntity = new UserJPAEntity();
        userJPAEntity.setName(userEntity.getName());
        userJPAEntity.setEmail(userEntity.getEmail());
        userJPAEntity.setRole(userEntity.getRole().getRoleEnum().getValue());

        this.transaction.getEntityManager().persist(userJPAEntity);
        this.transaction.getEntityManager().flush();

        UserEntity createdUserEntity = new UserEntity();

        createdUserEntity.setId(userJPAEntity.getId());
        createdUserEntity.setName(userEntity.getName());
        createdUserEntity.setEmail(userEntity.getEmail());
        createdUserEntity.setRole(userEntity.getRole());

        return createdUserEntity;
    }

    @Override
    public Optional<UserEntity> findById(int userId, EventObserver eventObserver) throws InterruptException {

        UserJPAEntity userJPAEntity = this.transaction.getEntityManager().find(UserJPAEntity.class, userId);
        if (userJPAEntity == null) {
            return Optional.empty();
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userJPAEntity.getId());
        userEntity.setName(userJPAEntity.getName());
        userEntity.setEmail(userJPAEntity.getEmail());

        userEntity.setRole(getUserRole(userJPAEntity.getRole(), eventObserver));

        return Optional.of(userEntity);

    }

    private UserRoleVO getUserRole(int role, EventObserver eventObserver) throws InterruptException {
        if (role == UserRoleEnum.ADMIN.getValue()) {
            return new UserRoleVO(UserRoleEnum.ADMIN);
        }
        if (role == UserRoleEnum.STORE_USER.getValue()) {
            return new UserRoleVO(UserRoleEnum.STORE_USER);
        }
        UserRepositoryEvent userRepositoryEvent = new UserRepositoryEvent(new Exception("Invalid role in database"));
        eventObserver.notify(userRepositoryEvent);
        return null;
    }

    public UserRepository(Transaction transaction) {
        this.transaction = transaction;
    }
}
