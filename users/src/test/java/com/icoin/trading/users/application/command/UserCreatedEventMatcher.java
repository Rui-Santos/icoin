package com.icoin.trading.users.application.command;

import com.icoin.trading.users.domain.event.PasswordEvent;
import com.icoin.trading.users.domain.event.UserCreatedEvent;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/18/14
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserCreatedEventMatcher extends BaseMatcher<UserCreatedEvent> {
    private UserCreatedEvent event;
    private PasswordEncoder passwordEncoder;
    private String rawPassword;

    public UserCreatedEventMatcher(UserCreatedEvent event, PasswordEncoder passwordEncoder, String rawPassword) {
        notNull(event);
        this.event = event;
        this.passwordEncoder = passwordEncoder;
        this.rawPassword = rawPassword;
    }


    @Override
    public boolean matches(Object o) {
        if (o == null) {
            return false;
        }

        UserCreatedEvent payload = (UserCreatedEvent) o;

        return EqualsBuilder.reflectionEquals(event, payload, "password")
                && passwordEncoder.matches(rawPassword, event.getPassword());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("sameInstanceWithPasswordMatched(")
                .appendValue(event)
                .appendText(")");
    }
}
