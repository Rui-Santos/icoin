package com.icoin.trading.fee.application.command;

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.command.commission.StartBuyCommissionTransactionCommand;
import com.icoin.trading.api.fee.command.commission.StartSellCommissionTransactionCommand;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.events.commission.BuyExecutedCommissionTransactionStartedEvent;
import com.icoin.trading.api.fee.events.commission.SellExecutedCommissionTransactionStartedEvent;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.fee.domain.transaction.ExecutedCommissionTransaction;
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
 * Time: PM9:06
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedCommissionCommandHandlerTest {
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
    private final FeeId receivedFeeId = new FeeId();
    private final FeeId accountReceivableFeeId = new FeeId();
    private final OffsetId offsetId = new OffsetId();
    private FixtureConfiguration fixture;
    private ExecutedCommissionCommandHandler commandHandler;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(ExecutedCommissionTransaction.class);
        commandHandler = new ExecutedCommissionCommandHandler();
        fixture.registerAnnotatedCommandHandler(commandHandler);
    }

    @Test
    public void testHandleStartToSell() throws Exception {
        StartSellCommissionTransactionCommand command =
                new StartSellCommissionTransactionCommand(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
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
                        receivedFeeId,
                        accountReceivableFeeId,
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
        StartBuyCommissionTransactionCommand command =
                new StartBuyCommissionTransactionCommand(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
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
                .expectEvents(new BuyExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
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
}