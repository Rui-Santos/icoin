package com.icoin.trading.fee.saga;

import com.google.common.collect.ImmutableList;
import com.icoin.fee.application.command.fee.CreateAccountReceivableFeeCommand;
import com.icoin.fee.application.command.fee.CreateReceivedFeeCommand;
import com.icoin.fee.domain.model.fee.BusinessType;
import com.icoin.fee.domain.model.fee.FeeId;
import com.icoin.fee.domain.model.fee.FeeStatus;
import com.icoin.fee.domain.model.fee.FeeTransactionId;
import com.icoin.fee.domain.model.fee.FeeType;
import com.icoin.fee.domain.model.offset.FeeItem;
import com.icoin.fee.domain.model.offset.FeeItemType;
import com.icoin.fee.domain.model.offset.OffsetId;
import com.icoin.fee.domain.model.offset.OffsetType;
import com.icoin.fee.domain.model.received.ReceivedSource;
import com.icoin.fee.domain.model.received.ReceivedSourceType;
import com.icoin.fee.saga.SellExecutedCommissionManagerSaga;
import com.icoin.trading.api.fee.command.offset.CreateOffsetCommand;
import com.icoin.trading.api.fee.events.SellExecutedCommissionTransactionStartedEvent;
import com.icoin.trading.api.fee.events.ar.AccountReceivableFeeCreatedEvent;
import com.icoin.trading.api.fee.events.offset.OffsetCreatedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeType;
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
 * Date: 14-3-19
 * Time: AM9:04
 * To change this template use File | Settings | File Templates.
 */
public class SellExecutedCommissionManagerSagaTest {
    private final FeeTransactionId feeTransactionId = new FeeTransactionId();
    private final String orderId = "orderId";
    private final CoinId coinId = new CoinId("BTC");
    private final TransactionId orderTransactionId = new TransactionId();
    private final PortfolioId portfolioId = new PortfolioId();
    private final Date tradeTime = currentTime();
    private final Date dueDate = new Date();
    private final OrderBookId orderBookId = new OrderBookId();
    private final FeeId receivedFeeId = new FeeId();
    private final FeeId accountReceivableFeeId = new FeeId();
    private final OffsetId offsetId = new OffsetId();
    private AnnotatedSagaTestFixture fixture;
    private BigMoney commissionAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10);

    @Before
    public void setUp() throws Exception {
        fixture = new AnnotatedSagaTestFixture(SellExecutedCommissionManagerSaga.class);
    }

    @Test
    public void testStarted() throws Exception {
        fixture.givenAggregate(feeTransactionId).published()
                .whenAggregate(feeTransactionId).publishes(
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new CreateAccountReceivableFeeCommand(
                                feeTransactionId,
                                accountReceivableFeeId,
                                FeeStatus.PENDING,
                                commissionAmount,
                                FeeType.SELL_COMMISSION,
                                BusinessType.SELL_COMMISSION,
                                tradeTime,
                                dueDate,
                                portfolioId.toString(),
                                orderTransactionId.toString()),
                        new CreateReceivedFeeCommand(
                                feeTransactionId,
                                receivedFeeId,
                                FeeStatus.PENDING,
                                commissionAmount,
                                FeeType.SELL_COMMISSION,
                                BusinessType.SELL_COMMISSION,
                                tradeTime,
                                dueDate,
                                portfolioId.toString(),
                                orderTransactionId.toString(),
                                new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, portfolioId.toString())),
                        new CreateOffsetCommand(
                                offsetId,
                                OffsetType.RECEIVED_AR,
                                portfolioId.toString(),
                                ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, commissionAmount)),
                                ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, commissionAmount)),
                                commissionAmount,
                                tradeTime));

    }

    @Test
    public void testReceivableCreated() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId))
                .whenAggregate(feeTransactionId).publishes(
                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
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
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId))
                .whenAggregate(feeTransactionId).publishes(
                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new ConfirmReceivedFeeCommand(
                                accountReceivableFeeId,
                                tradeTime));

    }

    @Test
    public void testOffsetCreated() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId))
                .whenAggregate(feeTransactionId).publishes(
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, commissionAmount)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, commissionAmount)),
                        commissionAmount,
                        tradeTime))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new OffsetFeesCommand(offsetId, tradeTime));

    }

    @Test
    public void testOffsetCreatedWithAllConfirmed() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, commissionAmount)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, commissionAmount)),
                        commissionAmount,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),

                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),
                new AccountReceivableFeeConfirmedEvent(
                        accountReceivableFeeId,
                        tradeTime),
                new ReceivedFeeConfirmedEvent(
                        accountReceivableFeeId,
                        tradeTime))
                .whenAggregate(feeTransactionId).publishes(
                new FeesOffsetedEvent(
                        offsetId,
                        tradeTime
                ))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new OffsetAccountReceivableFeeCommand(accountReceivableFeeId, tradeTime),
                        new OffsetReceivedFeeCommand(receivedFeeId, tradeTime));
    }

    @Test
    public void testOffsetCreatedWithAllConfirmed2() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, commissionAmount)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, commissionAmount)),
                        commissionAmount,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),

                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
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
                        accountReceivableFeeId,
                        tradeTime))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new OffsetAccountReceivableFeeCommand(accountReceivableFeeId, tradeTime),
                        new OffsetReceivedFeeCommand(receivedFeeId, tradeTime));
    }


    @Test
    public void testOffsetCreatedWithPartiallyConfirmed() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, commissionAmount)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, commissionAmount)),
                        commissionAmount,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),

                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        accountReceivableFeeId,
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
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, commissionAmount)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, commissionAmount)),
                        commissionAmount,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),

                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        accountReceivableFeeId,
                        tradeTime),
                new FeesOffsetedEvent(
                        offsetId,
                        tradeTime
                ))
                .whenAggregate(feeTransactionId).publishes(
                    new AccountReceivableFeeOffsetedEvent(
                            accountReceivableFeeId,
                            tradeTime
                    ))
                .expectActiveSagas(1)
                .expectNoDispatchedCommands();
    }

    @Test
    public void testReceivedOffseted() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, commissionAmount)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, commissionAmount)),
                        commissionAmount,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),

                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        accountReceivableFeeId,
                        tradeTime),
                new FeesOffsetedEvent(
                        offsetId,
                        tradeTime
                ))
                .whenAggregate(feeTransactionId).publishes(
                    new ReceivedFeeOffsetedEvent(
                            accountReceivableFeeId,
                            tradeTime
                    ))
                .expectActiveSagas(1)
                .expectNoDispatchedCommands();
    }

    @Test
    public void testAllOffseted() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, commissionAmount)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, commissionAmount)),
                        commissionAmount,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),

                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        accountReceivableFeeId,
                        tradeTime),
                new FeesOffsetedEvent(
                        offsetId,
                        tradeTime
                ),
                new AccountReceivableFeeOffsetedEvent(
                        accountReceivableFeeId,
                        tradeTime
                ))
                .whenAggregate(feeTransactionId).publishes(
                new ReceivedFeeOffsetedEvent(
                        accountReceivableFeeId,
                        tradeTime
                ))
                .expectActiveSagas(0)
                .expectNoDispatchedCommands();
    }

    @Test
    public void testOffsetAmountNotMatched() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, commissionAmount)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, commissionAmount)),
                        commissionAmount,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),

                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        accountReceivableFeeId,
                        tradeTime))
                .whenAggregate(feeTransactionId).publishes(
                    new OffsetAmountNotMatchedEvent(
                            offsetId,
                            commissionAmount,
                            arapAmount,
                            receivedPaidAmount,
                            tradeTime))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new CancelOffsetCommand(offsetId, CancelledReason.AMOUNT_NOT_MATCHED, event.getOffsetDate())
                );
    }

    @Test
    public void testOffsetCancelled() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, commissionAmount)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, commissionAmount)),
                        commissionAmount,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),

                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        accountReceivableFeeId,
                        tradeTime),
                new OffsetAmountNotMatchedEvent(
                        offsetId,
                        commissionAmount,
                        arapAmount,
                        receivedPaidAmount,
                        tradeTime))
                .whenAggregate(feeTransactionId).publishes(
                new OffsetCancelledEvent(
                        offsetId,
                        CancelledReason.AMOUNT_NOT_MATCHED,
                        tradeTime))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new CancelReceivedFeeCommand(accountReceivableId, CancelledReason.OFFSET_ERROR, tradeTime),
                        new CancelReceivedFeeCommand(receivedFeeId, CancelledReason.OFFSET_ERROR, tradeTime));
    }

    @Test
    public void testReceivedCancelled() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, commissionAmount)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, commissionAmount)),
                        commissionAmount,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),

                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        accountReceivableFeeId,
                        tradeTime),
                new OffsetAmountNotMatchedEvent(
                        offsetId,
                        commissionAmount,
                        arapAmount,
                        receivedPaidAmount,
                        tradeTime),
                new OffsetCancelledEvent(
                        offsetId,
                        CancelledReason.AMOUNT_NOT_MATCHED,
                        tradeTime))
                .whenAggregate(feeTransactionId).publishes(
                    new ReceivedFeeCancelledEvent(accountReceivableFeeId, tradeTime))
                .expectActiveSagas(1)
                .expectNoDispatchedCommands();
    }


    @Test
    public void testReceivableCancelled() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, commissionAmount)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, commissionAmount)),
                        commissionAmount,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),

                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        accountReceivableFeeId,
                        tradeTime),
                new OffsetAmountNotMatchedEvent(
                        offsetId,
                        commissionAmount,
                        arapAmount,
                        receivedPaidAmount,
                        tradeTime),
                new OffsetCancelledEvent(
                        offsetId,
                        CancelledReason.AMOUNT_NOT_MATCHED,
                        tradeTime))
                .whenAggregate(feeTransactionId).publishes(
                new AccountReceivableFeeCancelledEvent(accountReceivableFeeId, tradeTime))
                .expectActiveSagas(1)
                .expectNoDispatchedCommands();
    }


    @Test
    public void testAllCancelled() throws Exception {
        fixture.givenAggregate(feeTransactionId).published(
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId),
                new OffsetCreatedEvent(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(accountReceivableFeeId.toString(), FeeItemType.AR, commissionAmount)),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, commissionAmount)),
                        commissionAmount,
                        tradeTime),

                new ReceivedFeeCreatedEvent(
                        receivedFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),

                new AccountReceivableFeeCreatedEvent(
                        accountReceivableFeeId,
                        FeeStatus.PENDING,
                        commissionAmount,
                        FeeType.SELL_COMMISSION,
                        dueDate,
                        tradeTime,
                        portfolioId.toString(),
                        BusinessType.SELL_COMMISSION,
                        orderTransactionId.toString()),
                new ReceivedFeeConfirmedEvent(
                        accountReceivableFeeId,
                        tradeTime),
                new OffsetAmountNotMatchedEvent(
                        offsetId,
                        commissionAmount,
                        arapAmount,
                        receivedPaidAmount,
                        tradeTime),
                new OffsetCancelledEvent(
                        offsetId,
                        CancelledReason.AMOUNT_NOT_MATCHED,
                        tradeTime),
                new ReceivedFeeCancelledEvent(accountReceivableFeeId, tradeTime),
                new AccountReceivableFeeCancelledEvent(accountReceivableFeeId, tradeTime))
                .whenAggregate(feeTransactionId).publishes()
                .expectActiveSagas(0)
                .expectNoDispatchedCommands();
    }
}
