package com.icoin.trading.fee.application.command;

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.command.commission.GainBuyCoinTransactionCommand;
import com.icoin.trading.api.fee.command.commission.GainSoldMoneyTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PayBuyCommissionTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PayBuyMoneyTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PaySoldCoinTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PaySellCommissionTransactionCommand;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.events.execution.BuyExecutedCommissionTransactionStartedEvent;
import com.icoin.trading.api.fee.events.execution.ExecutedPayCoinTransactionStartedEvent;
import com.icoin.trading.api.fee.events.execution.ExecutedPayMoneyTransactionStartedEvent;
import com.icoin.trading.api.fee.events.execution.ExecutedReceiveCoinTransactionStartedEvent;
import com.icoin.trading.api.fee.events.execution.ExecutedReceiveMoneyTransactionStartedEvent;
import com.icoin.trading.api.fee.events.execution.SellExecutedCommissionTransactionStartedEvent;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.fee.domain.transaction.ExecutedCoinTransaction;
import com.icoin.trading.fee.domain.transaction.ExecutedCommissionTransaction;
import com.icoin.trading.fee.domain.transaction.ExecutedMoneyTransaction;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Test;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-19
 * Time: PM9:06
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionCommandHandlerTest {
    private final FeeTransactionId feeTransactionId = new FeeTransactionId();
    private final String orderId = "orderId";
    private final TransactionId orderTransactionId = new TransactionId();
    private final PortfolioId portfolioId = new PortfolioId();
    private final Date tradeTime = new Date();
    private final Date dueDate = new Date();
    private final BigMoney sellCommissionAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10);
    private final BigMoney buyCommissionAmount = BigMoney.of(CurrencyUnit.AUD, 15);
    private final BigMoney tradedPrice = BigMoney.of(CurrencyUnit.AUD, 108);
    private final BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10);
    private final BigMoney money = BigMoney.of(CurrencyUnit.AUD, 9000);
    private final OrderBookId orderBookId = new OrderBookId();
    private final CoinId coinId = new CoinId();
    private final FeeId paidReceivedFeeId = new FeeId();
    private final FeeId payableReceivableFeeId = new FeeId();
    private final OffsetId offsetId = new OffsetId();
    private FixtureConfiguration fixture;
    private ExecutionCommandHandler commandHandler;


    @Test
    public void testHandleStartToSell() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(ExecutedCommissionTransaction.class);
        commandHandler = new ExecutionCommandHandler();
        fixture.registerAnnotatedCommandHandler(commandHandler);

        commandHandler.setCommissionRepository(fixture.getRepository());

        PaySellCommissionTransactionCommand command =
                new PaySellCommissionTransactionCommand(
                        feeTransactionId,
                        paidReceivedFeeId,
                        payableReceivableFeeId,
                        offsetId,
                        sellCommissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        tradedPrice,
                        tradeAmount,
                        money,
                        orderBookId,
                        coinId
                );

        fixture.given()
                .when(command)
                .expectEvents(new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        paidReceivedFeeId,
                        payableReceivableFeeId,
                        offsetId,
                        sellCommissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        tradedPrice,
                        tradeAmount,
                        money,
                        orderBookId,
                        coinId
                ));
    }

    @Test
    public void testHandleStartToBuy() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(ExecutedCommissionTransaction.class);
        commandHandler = new ExecutionCommandHandler();
        fixture.registerAnnotatedCommandHandler(commandHandler);

        commandHandler.setCommissionRepository(fixture.getRepository());

        PayBuyCommissionTransactionCommand command =
                new PayBuyCommissionTransactionCommand(
                        feeTransactionId,
                        paidReceivedFeeId,
                        payableReceivableFeeId,
                        offsetId,
                        buyCommissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        tradedPrice,
                        tradeAmount,
                        money,
                        orderBookId,
                        coinId
                );

        fixture.given()
                .when(command)
                .expectEvents(new BuyExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        paidReceivedFeeId,
                        payableReceivableFeeId,
                        offsetId,
                        buyCommissionAmount,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        tradedPrice,
                        tradeAmount,
                        money,
                        orderBookId,
                        coinId
                ));
    }

    @Test
    public void testHandleStartToPaySoldCoin() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(ExecutedCoinTransaction.class);
        commandHandler = new ExecutionCommandHandler();
        fixture.registerAnnotatedCommandHandler(commandHandler);

        commandHandler.setCoinRepository(fixture.getRepository());

        PaySoldCoinTransactionCommand command =
                new PaySoldCoinTransactionCommand(
                        feeTransactionId,
                        paidReceivedFeeId,
                        payableReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        tradedPrice,
                        tradeAmount,
                        money,
                        orderBookId,
                        coinId
                );

        fixture.given()
                .when(command)
                .expectEvents(new ExecutedPayCoinTransactionStartedEvent(
                        feeTransactionId,
                        paidReceivedFeeId,
                        payableReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        tradedPrice,
                        tradeAmount,
                        money,
                        orderBookId,
                        coinId
                ));
    }

    @Test
    public void testHandleStartToReceiveMoneyFromSale() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(ExecutedMoneyTransaction.class);
        commandHandler = new ExecutionCommandHandler();
        fixture.registerAnnotatedCommandHandler(commandHandler);

        commandHandler.setMoneyRepository(fixture.getRepository());

        GainSoldMoneyTransactionCommand command =
                new GainSoldMoneyTransactionCommand(
                        feeTransactionId,
                        paidReceivedFeeId,
                        payableReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        tradedPrice,
                        tradeAmount,
                        money,
                        orderBookId,
                        coinId
                );

        fixture.given()
                .when(command)
                .expectEvents(new ExecutedReceiveMoneyTransactionStartedEvent(
                        feeTransactionId,
                        paidReceivedFeeId,
                        payableReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        tradedPrice,
                        tradeAmount,
                        money,
                        orderBookId,
                        coinId
                ));
    }

    @Test
    public void testHandleStartToPayForCoins() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(ExecutedMoneyTransaction.class);
        commandHandler = new ExecutionCommandHandler();
        fixture.registerAnnotatedCommandHandler(commandHandler);

        commandHandler.setMoneyRepository(fixture.getRepository());

        PayBuyMoneyTransactionCommand command =
                new PayBuyMoneyTransactionCommand(
                        feeTransactionId,
                        paidReceivedFeeId,
                        payableReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        tradedPrice,
                        tradeAmount,
                        money,
                        orderBookId,
                        coinId
                );

        fixture.given()
                .when(command)
                .expectEvents(new ExecutedPayMoneyTransactionStartedEvent(
                        feeTransactionId,
                        paidReceivedFeeId,
                        payableReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        tradedPrice,
                        tradeAmount,
                        money,
                        orderBookId,
                        coinId
                ));
    }

    @Test
    public void testHandleStartToReceiveCoinsFromSale() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(ExecutedCoinTransaction.class);
        commandHandler = new ExecutionCommandHandler();
        fixture.registerAnnotatedCommandHandler(commandHandler);

        commandHandler.setCoinRepository(fixture.getRepository());

        GainBuyCoinTransactionCommand command =
                new GainBuyCoinTransactionCommand(
                        feeTransactionId,
                        paidReceivedFeeId,
                        payableReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        tradedPrice,
                        tradeAmount,
                        money,
                        orderBookId,
                        coinId
                );

        fixture.given()
                .when(command)
                .expectEvents(new ExecutedReceiveCoinTransactionStartedEvent(
                        feeTransactionId,
                        paidReceivedFeeId,
                        payableReceivableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        tradedPrice,
                        tradeAmount,
                        money,
                        orderBookId,
                        coinId
                ));
    }
}