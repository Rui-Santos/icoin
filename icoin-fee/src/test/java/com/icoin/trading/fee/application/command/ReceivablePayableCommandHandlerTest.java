package com.icoin.trading.fee.application.command;

import com.icoin.trading.api.fee.command.payable.CancelAccountPayableFeeCommand;
import com.icoin.trading.api.fee.command.payable.ConfirmAccountPayableFeeCommand;
import com.icoin.trading.api.fee.command.payable.CreateAccountPayableFeeCommand;
import com.icoin.trading.api.fee.command.payable.OffsetAccountPayableFeeCommand;
import com.icoin.trading.api.fee.command.receivable.CancelAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.command.receivable.ConfirmAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.command.receivable.CreateAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.command.receivable.OffsetAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeOffsetedEvent;
import com.icoin.trading.api.fee.events.fee.receivable.AccountReceivableFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.receivable.AccountReceivableFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.receivable.AccountReceivableFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.receivable.AccountReceivableFeeOffsetedEvent;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.users.domain.UserId;
import com.icoin.trading.fee.domain.payable.AccountPayableFee;
import com.icoin.trading.fee.domain.receivable.AccountReceivableFee;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-19
 * Time: PM8:59
 * To change this template use File | Settings | File Templates.
 */
public class ReceivablePayableCommandHandlerTest {
    private final FeeTransactionId feeTransactionId = new FeeTransactionId();
    private final TransactionId orderTransactionId = new TransactionId();
    private final PortfolioId portfolioId = new PortfolioId();
    private final OffsetId offsetId = new OffsetId();
    private final UserId userId = new UserId();
    private final Date tradeTime = new Date();
    private final Date dueDate = new Date();
    private final BigMoney sellCommissionAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10);
    private final FeeId receivableFeeId = new FeeId();
    private final FeeId payableFeeId = new FeeId();

    private FixtureConfiguration receivableFixture;
    private FixtureConfiguration payableFixture;
    private ReceivablePayableCommandHandler commandHandler;

    @Before
    public void setUp() throws Exception {
        receivableFixture = Fixtures.newGivenWhenThenFixture(AccountReceivableFee.class);
        payableFixture = Fixtures.newGivenWhenThenFixture(AccountPayableFee.class);

        commandHandler = new ReceivablePayableCommandHandler();

        receivableFixture.registerAnnotatedCommandHandler(commandHandler);
        payableFixture.registerAnnotatedCommandHandler(commandHandler);

        commandHandler.setAccountReceivableFeeRepository(receivableFixture.getRepository());
        commandHandler.setAccountPayableFeeRepository(payableFixture.getRepository());
    }

    @Test
    public void testHandleCreateAccountReceivable() throws Exception {
        CreateAccountReceivableFeeCommand command = new CreateAccountReceivableFeeCommand(
                feeTransactionId,
                receivableFeeId,
                FeeStatus.PENDING,
                sellCommissionAmount,
                FeeType.RECEIVE_MONEY,
                BusinessType.TRADE_EXECUTED,
                tradeTime,
                dueDate,
                portfolioId.toString(),
                userId.toString(),
                orderTransactionId.toString());

        receivableFixture.given()
                .when(command)
                .expectEvents(
                        new AccountReceivableFeeCreatedEvent(
                                receivableFeeId,
                                FeeStatus.PENDING,
                                sellCommissionAmount,
                                FeeType.RECEIVE_MONEY,
                                tradeTime,
                                dueDate,
                                portfolioId.toString(),
                                userId.toString(),
                                BusinessType.TRADE_EXECUTED,
                                orderTransactionId.toString()));
    }

    @Test
    public void testHandleConfirmAccountReceivable() throws Exception {
        ConfirmAccountReceivableFeeCommand command =
                new ConfirmAccountReceivableFeeCommand(receivableFeeId, tradeTime);

        receivableFixture.given(
                new AccountReceivableFeeCreatedEvent(
                        receivableFeeId,
                        FeeStatus.PENDING,
                        sellCommissionAmount,
                        FeeType.RECEIVE_MONEY,
                        tradeTime,
                        dueDate,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()))
                .when(command)
                .expectEvents(
                        new AccountReceivableFeeConfirmedEvent(
                                receivableFeeId,
                                tradeTime));
    }

    @Test
    public void testHandleOffsetAccountReceivable() throws Exception {
        OffsetAccountReceivableFeeCommand command =
                new OffsetAccountReceivableFeeCommand(receivableFeeId, offsetId, tradeTime);

        receivableFixture.given(
                new AccountReceivableFeeCreatedEvent(
                        receivableFeeId,
                        FeeStatus.PENDING,
                        sellCommissionAmount,
                        FeeType.RECEIVE_COIN,
                        tradeTime,
                        dueDate,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()))
                .when(command)
                .expectEvents(
                        new AccountReceivableFeeOffsetedEvent(
                                receivableFeeId,
                                offsetId,
                                tradeTime));
    }

    @Test
    public void testHandleCancelAccountReceivable() throws Exception {
        CancelAccountReceivableFeeCommand command =
                new CancelAccountReceivableFeeCommand(receivableFeeId, CancelledReason.OFFSET_ERROR, tradeTime);

        receivableFixture.given(
                new AccountReceivableFeeCreatedEvent(
                        receivableFeeId,
                        FeeStatus.PENDING,
                        sellCommissionAmount,
                        FeeType.RECEIVE_COIN,
                        tradeTime,
                        dueDate,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()))
                .when(command)
                .expectEvents(
                        new AccountReceivableFeeCancelledEvent(
                                receivableFeeId,
                                CancelledReason.OFFSET_ERROR,
                                tradeTime));
    }

    @Test
    public void testHandleCreateAccountPayable() throws Exception {
        CreateAccountPayableFeeCommand command = new CreateAccountPayableFeeCommand(
                feeTransactionId,
                payableFeeId,
                FeeStatus.PENDING,
                sellCommissionAmount,
                FeeType.RECEIVE_MONEY,
                BusinessType.TRADE_EXECUTED,
                tradeTime,
                dueDate,
                portfolioId.toString(),
                userId.toString(),
                orderTransactionId.toString());

        payableFixture.given()
                .when(command)
                .expectEvents(
                        new AccountPayableFeeCreatedEvent(
                                payableFeeId,
                                FeeStatus.PENDING,
                                sellCommissionAmount,
                                FeeType.RECEIVE_MONEY,
                                tradeTime,
                                dueDate,
                                portfolioId.toString(),
                                userId.toString(),
                                BusinessType.TRADE_EXECUTED,
                                orderTransactionId.toString()));
    }

    @Test
    public void testHandleConfirmAccountPayable() throws Exception {
        ConfirmAccountPayableFeeCommand command =
                new ConfirmAccountPayableFeeCommand(payableFeeId, tradeTime);

        payableFixture.given(
                new AccountPayableFeeCreatedEvent(
                        payableFeeId,
                        FeeStatus.PENDING,
                        sellCommissionAmount,
                        FeeType.RECEIVE_MONEY,
                        tradeTime,
                        dueDate,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()))
                .when(command)
                .expectEvents(
                        new AccountPayableFeeConfirmedEvent(
                                payableFeeId,
                                tradeTime));
    }

    @Test
    public void testHandleOffsetAccountPayable() throws Exception {
        OffsetAccountPayableFeeCommand command =
                new OffsetAccountPayableFeeCommand(payableFeeId, offsetId, tradeTime);

        payableFixture.given(
                new AccountPayableFeeCreatedEvent(
                        payableFeeId,
                        FeeStatus.PENDING,
                        sellCommissionAmount,
                        FeeType.RECEIVE_COIN,
                        tradeTime,
                        dueDate,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()))
                .when(command)
                .expectEvents(
                        new AccountPayableFeeOffsetedEvent(
                                payableFeeId,
                                offsetId,
                                tradeTime));
    }

    @Test
    public void testHandleCancelAccountPayable() throws Exception {
        CancelAccountPayableFeeCommand command =
                new CancelAccountPayableFeeCommand(payableFeeId, CancelledReason.OFFSET_ERROR, tradeTime);

        payableFixture.given(
                new AccountPayableFeeCreatedEvent(
                        payableFeeId,
                        FeeStatus.PENDING,
                        sellCommissionAmount,
                        FeeType.RECEIVE_COIN,
                        tradeTime,
                        dueDate,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()))
                .when(command)
                .expectEvents(
                        new AccountPayableFeeCancelledEvent(
                                payableFeeId,
                                CancelledReason.OFFSET_ERROR,
                                tradeTime));
    }
}
