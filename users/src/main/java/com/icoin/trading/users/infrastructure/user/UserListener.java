package com.icoin.trading.users.infrastructure.user;

import com.icoin.trading.users.domain.event.PasswordChangedEvent;
import com.icoin.trading.users.domain.event.UserAuthenticatedEvent;
import com.icoin.trading.users.domain.event.WithdrawPasswordChangedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-12
 * Time: PM11:51
 * To change this template use File | Settings | File Templates.
 */
@Component
public class UserListener {
    @EventHandler
    public void handle(WithdrawPasswordChangedEvent event) {

    }

    @EventHandler
    public void handle(PasswordChangedEvent event) {

    }

    @EventHandler
    public void handle(UserAuthenticatedEvent event) {

    }
}
