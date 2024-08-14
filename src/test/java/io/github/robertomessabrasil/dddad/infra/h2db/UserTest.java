package io.github.robertomessabrasil.dddad.infra.h2db;

import io.github.robertomessabrasil.dddad.entity.user.UserEntity;
import io.github.robertomessabrasil.dddad.entity.user.UserRoleEnum;
import io.github.robertomessabrasil.dddad.entity.user.UserRoleVO;
import io.github.robertomessabrasil.dddad.entity.user.event.UserValidationEvent;
import io.github.robertomessabrasil.dddad.infra.h2db.repository.user.Transaction;
import io.github.robertomessabrasil.dddad.infra.h2db.repository.user.UserRepository;
import io.github.robertomessabrasil.dddad.infra.h2db.repository.user.entity.UserJPAEntity;
import io.github.robertomessabrasil.dddad.listener.ValidationListener;
import io.github.robertomessabrasil.jwatch.exception.InterruptException;
import io.github.robertomessabrasil.jwatch.observer.EventObserver;
import org.h2.tools.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private static EventObserver eventObserver = new EventObserver();
    private static Server server;
    private static Transaction transaction;

    UserTest() {
        this.transaction = new Transaction();
        ValidationListener validationListener = new ValidationListener();
        validationListener.addEvent(UserValidationEvent.class);
        this.eventObserver.subscribe(validationListener);
    }

    @BeforeAll
    public static void start() throws SQLException {
        server = Server.createTcpServer().start();
    }

    @Test
    @Order(1)
    public void givenParameters_createUser() {

        UserEntity createdUserEntity = null;
        UserRepository userRepository = new UserRepository(this.transaction);

        try {
            this.transaction.begin();

            String userName = "Roberto Messa";
            String userEmail = "account@host.com";
            UserRoleVO userRoleVO = new UserRoleVO(UserRoleEnum.STORE_USER);

            UserEntity userEntity = new UserEntity();
            userEntity.setName(userName);
            userEntity.setEmail(userEmail);
            userEntity.setRole(userRoleVO);

            createdUserEntity = userRepository.create(userEntity, this.eventObserver);

            this.transaction.commit();
        } catch (Exception ex) {
            this.transaction.rollBack();
        }

        this.list();

        int userId = 1;
        assertNotNull(createdUserEntity, () -> "createdUser must be not null");
        assertEquals(userId, createdUserEntity.getId(), () -> "createdUser.getId() must be equal to " + userId);

    }

    @Test
    @Order(2)
//    @Disabled
    public void giverUserId_returnUser() throws InterruptException {

        UserRepository userRepository = new UserRepository(this.transaction);
        int userId = 1;
        Optional<UserEntity> userEntity = userRepository.findById(userId, this.eventObserver);

        assertNotNull(userEntity, () -> "userEntity must be not null");
        assertTrue(userEntity.isPresent(), () -> "userEntity must be present");

    }

    @AfterAll
    public static void end() {
        transaction.close();
        if (server != null) {
            server.stop();
        }
    }

    private void list() {
        List<UserJPAEntity> users = this.transaction.getSession().createQuery("from UserJPAEntity", UserJPAEntity.class).list();
        users.forEach(s -> System.out.println(s.getEmail()));
    }

}
