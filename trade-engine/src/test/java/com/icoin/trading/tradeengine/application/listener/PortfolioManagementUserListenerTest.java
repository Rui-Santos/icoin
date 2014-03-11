package com.icoin.trading.tradeengine.application.listener;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.application.command.portfolio.CreatePortfolioCommand;
import com.icoin.trading.users.domain.model.user.Identifier;
import com.icoin.trading.users.domain.model.user.UserId;
import com.icoin.trading.users.domain.event.UserCreatedEvent;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static com.homhon.util.TimeUtils.currentTime;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-4
 * Time: PM12:52
 * To change this template use File | Settings | File Templates.
 */
public class PortfolioManagementUserListenerTest {
    @Test
    public void checkPortfolioCreationAfterUserCreated() {
        CommandBus commandBus = Mockito.mock(CommandBus.class);
        PortfolioManagementUserListener listener = new PortfolioManagementUserListener();
        listener.setCommandBus(commandBus);

        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");

        UserId userIdentifier = new UserId();
        UserCreatedEvent event =
                new UserCreatedEvent(userIdentifier,
                        "testuser",
                        "Test",
                        "User",
                         identifier,
                        "email@163.com",
                        "testpassword",
                        Constants.DEFAULT_ROLES,
                        currentTime());

        listener.createNewPortfolioWhenUserIsCreated(event);

        Mockito.verify(commandBus).dispatch(Matchers.argThat(new GenericCommandMessageMatcher(userIdentifier)));
    }

    private class GenericCommandMessageMatcher extends ArgumentMatcher<GenericCommandMessage> {

        private UserId userId;

        private GenericCommandMessageMatcher(UserId userId) {
            this.userId = userId;
        }

        @Override
        public boolean matches(Object argument) {
            if (!(argument instanceof GenericCommandMessage)) {
                return false;
            }
            if (!(((GenericCommandMessage) argument).getPayload() instanceof CreatePortfolioCommand)) {
                return false;
            }
            CreatePortfolioCommand createPortfolioCommand = ((GenericCommandMessage<CreatePortfolioCommand>) argument).getPayload();
            return createPortfolioCommand.getUserId().equals(userId);
        }
    }
}
