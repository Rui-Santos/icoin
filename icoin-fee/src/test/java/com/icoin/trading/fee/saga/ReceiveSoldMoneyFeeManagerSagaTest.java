package com.icoin.trading.fee.saga;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.command.offset.CancelOffsetCommand;
import com.icoin.trading.api.fee.command.offset.CreateOffsetCommand;
import com.icoin.trading.api.fee.command.offset.OffsetFeesCommand;
import com.icoin.trading.api.fee.command.receivable.CancelAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.command.receivable.ConfirmAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.command.receivable.CreateAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.command.receivable.OffsetAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.command.received.CancelReceivedFeeCommand;
import com.icoin.trading.api.fee.command.received.ConfirmReceivedFeeCommand;
import com.icoin.trading.api.fee.command.received.CreateReceivedFeeCommand;
import com.icoin.trading.api.fee.command.received.OffsetReceivedFeeCommand;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.domain.offset.CancelledReason;
import com.icoin.trading.api.fee.domain.offset.FeeItem;
import com.icoin.trading.api.fee.domain.offset.FeeItemType;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.domain.offset.OffsetType;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.fee.domain.received.ReceivedSourceType;
import com.icoin.trading.api.fee.events.execution.ExecutedReceiveMoneyTransactionStartedEvent;
import com.icoin.trading.api.fee.events.fee.receivable.AccountReceivableFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.receivable.AccountReceivableFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.receivable.AccountReceivableFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.receivable.AccountReceivableFeeOffsetedEvent;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeOffsetedEvent;
import com.icoin.trading.api.fee.events.offset.FeesOffsetedEvent;
import com.icoin.trading.api.fee.events.offset.OffsetAmountNotMatchedEvent;
import com.icoin.trading.api.fee.events.offset.OffsetCancelledEvent;
import com.icoin.trading.api.fee.events.offset.OffsetCreatedEvent;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.users.domain.UserId;
import org.axonframework.test.saga.AnnotatedSagaTestFixture;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-30
 * Time: PM11:20
 * To change this template use File | Settings | File Templates.
 */
public class ReceiveSoldMoneyFeeManagerSagaTest {
    private final FeeTransactionId feeTransactionId = new FeeTransactionId();
    private final String orderId = "orderId";
    private final CoinId coinId = new CoinId("BTC");
    private final TransactionId orderTransactionId = new TransactionId();
    private final PortfolioId portfolioId = new PortfolioId();
    private final UserId userId = new UserId();
    private final Date tradeTime = currentTime();
    private final Date dueDate = new Date();
    private final OrderBookId orderBookId = new OrderBookId();
    private final FeeId receivedFeeId = new FeeId();
    private final FeeId accountReceivableFeeId = new FeeId();
    private final OffsetId offsetId = new OffsetId();
    private AnnotatedSagaTestFixture fixture;
    private BigMoney soldMoney = BigMoney.parse("CNY 123.878");

    @Before
    public void setUp() throws Exception {
        fixture = new AnnotatedSagaTestFixture(ReceiveSoldMoneyFeeManagerSaga.class);
    }

    @Test
    public void testStarted() throws Exception {
        fixture.givenAggregate(feeTransactionId).published()
                .whenAggregate(feeTransactionId).publishes(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new CreateAccountReceivableFeeCommand(
                                feeTransactionId,
                                accountReceivableFeeId,
                                FeeStatus.PENDING,
                                soldMoney,
                                FeeType.PAY_MONEY,
                                BusinessType.TRADE_EXECUTED,
                                tradeTime,
                                dueDate,
                                portfolioId.toString(),
                                userId.toString(),
                                orderTransactionId.toString()),
                        new CreateReceivedFeeCommand(
                                feeTransactionId,
                                receivedFeeId,
                                FeeStatus.PENDING,
                                soldMoney,
                                FeeType.PAY_MONEY,
                                BusinessType.TRADE_EXECUTED,
                                tradeTime,
                                dueDate,
                                portfolioId.toString(),
                                userId.toString(),
                                orderTransactionId.toString(),
                                new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, portfolioId.toString())),
                        new CreateOffsetCommand(
                                offsetId,
                                OffsetType.RECEIVED_AR,
                                portfolioId.toString(),
                                ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, soldMoney)),
                                ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, soldMoney)),
                                soldMoney,
                                tradeTime));

    }

    @Test
    public void testReceivableCreated() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId))
                .whenAggregate(accountReceivableFeeId).publishes(
                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new ConfirmAccountReceivableFeeCommand(
                                accountReceivableFeeId,
                                tradeTime));

    }

    @Test
    public void testReceivedCreated() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId))
                .whenAggregate(receivedFeeId).publishes(
                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString(),
                        new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, orderTransactionId.toString())))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new ConfirmReceivedFeeCommand(
                                receivedFeeId,
                                soldMoney,
                                tradeTime));

    }

    @Test
    public void testOffsetCreated() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId))
                .whenAggregate(offsetId).publishes(
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, soldMoney)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, soldMoney)),
                        soldMoney,
                        tradeTime))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new OffsetFeesCommand(offsetId, tradeTime));

    }

    @Test
    public void testOffsetCreatedWithAllConfirmed() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, soldMoney)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, soldMoney)),
                        soldMoney,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString(),
                        new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, orderTransactionId.toString())),


                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()),
                new AccountReceivableFeeConfirmedEvent(
                        accountReceivableFeeId,
                        tradeTime),
                new ReceivedFeeConfirmedEvent(
                        receivedFeeId,
                        soldMoney,
                        tradeTime))
                .whenAggregate(offsetId).publishes(
                new FeesOffsetedEvent(
                        offsetId,
                        tradeTime
                ))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new OffsetAccountReceivableFeeCommand(accountReceivableFeeId, offsetId, tradeTime),
                        new OffsetReceivedFeeCommand(receivedFeeId, offsetId, tradeTime));
    }

    @Test
    public void testOffsetCreatedWithAllConfirmed2() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, soldMoney)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, soldMoney)),
                        soldMoney,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString(),
                        new ReceivedSource(ReceivedSourceType.ALIPAY, orderTransactionId.toString())),

                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()),
                new FeesOffsetedEvent(
                        offsetId,
                        tradeTime
                ),
                new AccountReceivableFeeConfirmedEvent(
                        accountReceivableFeeId,
                        tradeTime))
                .whenAggregate(receivedFeeId).publishes(
                new ReceivedFeeConfirmedEvent(
                        receivedFeeId,
                        soldMoney,
                        tradeTime))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new OffsetAccountReceivableFeeCommand(accountReceivableFeeId, offsetId, tradeTime),
                        new OffsetReceivedFeeCommand(receivedFeeId, offsetId, tradeTime));
    }


    @Test
    public void testOffsetCreatedWithPartiallyConfirmed() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, soldMoney)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, soldMoney)),
                        soldMoney,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString(),
                        new ReceivedSource(ReceivedSourceType.ALIPAY, orderTransactionId.toString())),

                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        receivedFeeId,
                        soldMoney,
                        tradeTime))
                .whenAggregate(offsetId).publishes(
                new FeesOffsetedEvent(
                        offsetId,
                        tradeTime
                ))
                .expectActiveSagas(1)
                .expectNoDispatchedCommands();
    }

    @Test
    public void testReceivableOffseted() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, soldMoney)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, soldMoney)),
                        soldMoney,
                        tradeTime),
                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString(),
                        new ReceivedSource(ReceivedSourceType.ALIPAY, orderTransactionId.toString())),
                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        receivedFeeId,
                        soldMoney,
                        tradeTime),
                new FeesOffsetedEvent(
                        offsetId,
                        tradeTime
                ))
                .whenAggregate(accountReceivableFeeId).publishes(
                new AccountReceivableFeeOffsetedEvent(
                        accountReceivableFeeId,
                        offsetId,
                        tradeTime
                ))
                .expectActiveSagas(1)
                .expectNoDispatchedCommands();
    }

    @Test
    public void testReceivedOffseted() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, soldMoney)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, soldMoney)),
                        soldMoney,
                        tradeTime),
                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString(),
                        new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, orderTransactionId.toString())),
                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        receivedFeeId,
                        soldMoney,
                        tradeTime),
                new FeesOffsetedEvent(
                        offsetId,
                        tradeTime
                ))
                .whenAggregate(accountReceivableFeeId).publishes(
                new ReceivedFeeOffsetedEvent(
                        accountReceivableFeeId,
                        offsetId,
                        tradeTime
                ))
                .expectActiveSagas(1)
                .expectNoDispatchedCommands();
    }

    @Test
    public void testAllOffseted() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, soldMoney)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, soldMoney)),
                        soldMoney,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString(),
                        new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, orderTransactionId.toString())),


                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        receivedFeeId,
                        soldMoney,
                        tradeTime),
                new FeesOffsetedEvent(
                        offsetId,
                        tradeTime
                ),
                new ReceivedFeeOffsetedEvent(
                        receivedFeeId,
                        offsetId,
                        tradeTime))
                .whenAggregate(accountReceivableFeeId).publishes(
                new AccountReceivableFeeOffsetedEvent(
                        accountReceivableFeeId,
                        offsetId,
                        tradeTime
                ))
                .expectActiveSagas(0)
                .expectNoDispatchedCommands();
    }

    @Test
    public void testOffsetAmountNotMatched() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, soldMoney)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, soldMoney)),
                        soldMoney,
                        tradeTime),
                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString(),
                        new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, orderTransactionId.toString())),


                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        receivedFeeId,
                        soldMoney,
                        tradeTime))
                .whenAggregate(offsetId).publishes(
                new OffsetAmountNotMatchedEvent(
                        offsetId,
                        soldMoney,
                        BigMoney.of(CurrencyUnit.EUR, 10),
                        BigMoney.of(CurrencyUnit.EUR, 10.6),
                        tradeTime))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new CancelOffsetCommand(offsetId, CancelledReason.AMOUNT_NOT_MATCHED, tradeTime)
                );
    }

    @Test
    public void testOffsetCancelled() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, soldMoney)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, soldMoney)),
                        soldMoney,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString(),
                        new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, orderTransactionId.toString())),


                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        receivedFeeId,
                        soldMoney,
                        tradeTime),
                new OffsetAmountNotMatchedEvent(
                        offsetId,
                        soldMoney,
                        BigMoney.of(CurrencyUnit.EUR, 10),
                        BigMoney.of(CurrencyUnit.EUR, 10.6),
                        tradeTime))
                .whenAggregate(offsetId).publishes(
                new OffsetCancelledEvent(
                        offsetId,
                        CancelledReason.AMOUNT_NOT_MATCHED,
                        tradeTime))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new CancelAccountReceivableFeeCommand(accountReceivableFeeId, com.icoin.trading.api.fee.domain.fee.CancelledReason.OFFSET_ERROR, tradeTime),
                        new CancelReceivedFeeCommand(receivedFeeId, com.icoin.trading.api.fee.domain.fee.CancelledReason.OFFSET_ERROR, tradeTime));
    }

    @Test
    public void testReceivedCancelled() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, soldMoney)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, soldMoney)),
                        soldMoney,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString(),
                        new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, orderTransactionId.toString())),


                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        receivedFeeId,
                        soldMoney,
                        tradeTime),
                new OffsetAmountNotMatchedEvent(
                        offsetId,
                        soldMoney,
                        BigMoney.of(CurrencyUnit.EUR, 10),
                        BigMoney.of(CurrencyUnit.EUR, 10.6),
                        tradeTime),
                new OffsetCancelledEvent(
                        offsetId,
                        CancelledReason.AMOUNT_NOT_MATCHED,
                        tradeTime))
                .whenAggregate(receivedFeeId).publishes(
                new ReceivedFeeCancelledEvent(receivedFeeId, com.icoin.trading.api.fee.domain.fee.CancelledReason.OFFSET_ERROR, tradeTime))
                .expectActiveSagas(1)
                .expectNoDispatchedCommands();
    }


    @Test
    public void testReceivableCancelled() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, soldMoney)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, soldMoney)),
                        soldMoney,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString(),
                        new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, orderTransactionId.toString())),


                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        receivedFeeId,
                        soldMoney,
                        tradeTime),
                new OffsetAmountNotMatchedEvent(
                        offsetId,
                        soldMoney,
                        BigMoney.of(CurrencyUnit.EUR, 10),
                        BigMoney.of(CurrencyUnit.EUR, 10.6),
                        tradeTime),
                new OffsetCancelledEvent(
                        offsetId,
                        CancelledReason.AMOUNT_NOT_MATCHED,
                        tradeTime))
                .whenAggregate(accountReceivableFeeId).publishes(
                new AccountReceivableFeeCancelledEvent(accountReceivableFeeId, com.icoin.trading.api.fee.domain.fee.CancelledReason.OFFSET_ERROR, tradeTime))
                .expectActiveSagas(1)
                .expectNoDispatchedCommands();
    }


    @Test
    public void testAllCancelled() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.parse("BTC 12.34"),
                        soldMoney,
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, soldMoney)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, soldMoney)),
                        soldMoney,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString(),
                        new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, orderTransactionId.toString())),

                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        soldMoney,
                        FeeType.PAY_MONEY,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        userId.toString(),
                        BusinessType.TRADE_EXECUTED,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        receivedFeeId,
                        soldMoney,
                        tradeTime),
                new OffsetAmountNotMatchedEvent(
                        offsetId,
                        soldMoney,
                        BigMoney.of(CurrencyUnit.EUR, 10),
                        BigMoney.of(CurrencyUnit.EUR, 10.6),
                        tradeTime),
                new OffsetCancelledEvent(
                        offsetId,
                        CancelledReason.AMOUNT_NOT_MATCHED,
                        tradeTime),
                new ReceivedFeeCancelledEvent(receivedFeeId, com.icoin.trading.api.fee.domain.fee.CancelledReason.OFFSET_ERROR, tradeTime)
        )
                .whenAggregate(accountReceivableFeeId).publishes(
                new AccountReceivableFeeCancelledEvent(accountReceivableFeeId, com.icoin.trading.api.fee.domain.fee.CancelledReason.OFFSET_ERROR, tradeTime))
                .expectActiveSagas(0)
                .expectNoDispatchedCommands();
    }
}
