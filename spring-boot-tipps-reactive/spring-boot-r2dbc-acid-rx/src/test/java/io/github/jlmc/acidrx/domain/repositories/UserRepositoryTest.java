package io.github.jlmc.acidrx.domain.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Note: unfortunately, it does not work as @DataJpaTest
 * which can pick up an embedded H2 for test purpose,
 * with @DataR2dbcTest , we have to use a runtime database, see #issue62.
 *
 * @see <a href="https://github.com/spring-attic/spring-boot-r2dbc/issues/68">#issue62</a>
 */
@DataR2dbcTest(properties = "spring.flyway.enabled=false")
class UserRepositoryTest {

    @Autowired
    DatabaseClient client;

    @Autowired
    UserRepository users;

    @Test
    public void testDatabaseClientExisted() {
        assertNotNull(client);
    }

    @Test
    public void testRepositoryExisted() {
        assertNotNull(users);
    }

    @Test
    public void testInsertAndQuery() {
        UUID userId = new UUID(0, 100);
        String userName = "test-user";

        this.client.sql("delete from t_users where id = :id").bind("id", userId)
                .then().block(Duration.ofSeconds(5));
        this.client.sql(() -> "insert into t_users (id, name) values (:id, :name)")
                .bind("id", userId)
                .bind("name", userName)
                .then().block(Duration.ofSeconds(5));

        users.findUser(userId.toString())
                .as(StepVerifier::create)
                .consumeNextWith(p -> {
                    assertEquals(userId, p.getId());
                    assertEquals(userName, p.getName());
                })
                .verifyComplete();
    }
}
