package com.icoin.trading.users.query;

import com.icoin.trading.api.users.domain.Identifier;
import com.icoin.trading.api.users.domain.UserId;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jettro Coenradie
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration({"classpath:META-INF/spring/users-persistence-mongo.xml"})
public class UserRepositoryIntegrationIT {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserQueryRepository repository;

    @Before
    public void setUp() throws Exception {
        repository.deleteAll();
    }

    @Test
    public void storeUserInRepository() {
        UserEntry userEntry = new UserEntry();
        userEntry.setPrimaryKey(new UserId().toString());
        userEntry.setUsername("User Name");
        userEntry.setFirstName("logon name");
        userEntry.setLastName("logon");
        userEntry.setIdentifier(new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252"));
        userEntry.setPassword("abc");

        repository.save(userEntry);

        final UserEntry one = repository.findOne(userEntry.getPrimaryKey());
    }
}
