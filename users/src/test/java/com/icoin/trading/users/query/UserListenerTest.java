package com.icoin.trading.users.query;

import com.icoin.trading.users.domain.event.UserCreatedEvent;
import com.icoin.trading.users.domain.model.user.Identifier;
import com.icoin.trading.users.domain.model.user.UserId;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-5
 * Time: AM10:27
 * To change this template use File | Settings | File Templates.
 */
public class UserListenerTest {
    @Test
    public void testHandleUserCreated() throws Exception {
        final UserId userId = new UserId();
        final String firstName = "buyer";
        final String lastName = "abc";
        final String username = "buyerAbc";
        final String email = "buyerAbc@163.com";
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");
        final String password = "sjfsudm9ei8r899e9e87745jkdkjfhd";

        final UserQueryRepository repository = mock(UserQueryRepository.class);

        final UserCreatedEvent event = new UserCreatedEvent(userId, username, firstName, lastName, identifier, email, password);
        final UserListener listener = new UserListener();
        listener.setUserRepository(repository);

        listener.handleUserCreated(event);

        final ArgumentCaptor<UserEntry> captor = ArgumentCaptor.forClass(UserEntry.class);
        verify(repository).save(captor.capture());
        final UserEntry userEntry = captor.getValue();

        assertThat(userEntry, notNullValue());
        assertThat(userEntry.getPrimaryKey(), equalTo(userId.toString()));
        assertThat(userEntry.getName(), equalTo(lastName + " " + firstName));
        assertThat(userEntry.getFullName(), equalTo(lastName + " " + firstName));
        assertThat(userEntry.getUsername(), equalTo(username));
        assertThat(userEntry.getPassword(), equalTo(password));
        assertThat(userEntry.getIdentifier(), equalTo(identifier));
        assertThat(userEntry.getId(), equalTo(username));
        assertThat(userEntry.getFirstName(), equalTo(firstName));
        assertThat(userEntry.getLastName(), equalTo(lastName));
        assertThat(userEntry.getEmail(), equalTo(email));
    }
}
