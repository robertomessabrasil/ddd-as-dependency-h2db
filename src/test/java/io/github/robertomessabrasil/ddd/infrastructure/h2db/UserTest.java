package io.github.robertomessabrasil.ddd.infrastructure.h2db;

import io.github.robertomessabrasil.dddad.domain.entity.user.UserEntity;
import io.github.robertomessabrasil.dddad.domain.entity.user.UserRoleEnum;
import io.github.robertomessabrasil.dddad.domain.entity.user.UserRoleVO;
import io.github.robertomessabrasil.dddad.domain.entity.user.event.UserValidationEvent;
import io.github.robertomessabrasil.dddad.domain.exception.InfrastructureException;
import io.github.robertomessabrasil.dddad.infra.h2db.repository.user.Transaction;
import io.github.robertomessabrasil.dddad.infra.h2db.repository.user.UserRepository;
import io.github.robertomessabrasil.dddad.listener.ValidationListener;
import io.github.robertomessabrasil.jwatch.observer.EventObserver;
import org.h2.tools.Server;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private static EventObserver eventObserver = new EventObserver();
    private static Transaction transaction = new Transaction();
    private static Server server;

    UserTest() {
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
    public void givenParameters_createUser() throws InfrastructureException {

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
            createdUserEntity = userRepository.create(userEntity, this.eventObserver);
            this.transaction.commit();
        } catch (RuntimeException ex) {
            System.out.println("aaaaaaaaaaaaaaaaa= " + ex.getMessage());
            this.transaction.rollBack();
        }

        assertNotNull(createdUserEntity);
//        assertEquals(1, createdUserEntity.getId());

    }

    @Test
    @Order(2)
    @Disabled
    public void giverUserId_returnUser() throws InfrastructureException {

        Optional<UserEntity> userEntity = null;
        UserRepository userRepository = new UserRepository(this.transaction);
        int userId = 1;
        userEntity = userRepository.findById(userId, this.eventObserver);

        assertNotNull(userEntity);
        assertTrue(userEntity.isPresent());

    }

    @AfterAll
    public static void end() {
        transaction.close();
        if (server != null) {
            server.stop();
        }
    }

}
