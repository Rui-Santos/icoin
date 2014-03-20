package com.icoin.trading.fee.application.listener;


import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.command.commission.StartBuyCommissionTransactionCommand;
import com.icoin.trading.api.fee.command.commission.StartSellCommissionTransactionCommand;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.tradeengine.events.trade.TradeExecutedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-19
 * Time: PM8:35
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedCommissionApplicationListenerTest {
    @Test
    public void testHandleSellCommission() throws Exception {
        final BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10000);
        final BigMoney tradedPrice = BigMoney.zero(CurrencyUnit.CAD);
        final BigMoney executedMoney = BigMoney.of(CurrencyUnit.CAD, 0.00001);
        final BigMoney buyCommission = BigMoney.zero(CurrencyUnit.CAD);
        final BigMoney sellCommission = BigMoney.zero(CurrencyUnit.of("BTC"));
        final Date tradeTime = currentTime();
        final String buyOrderId = "buyOrderId";
        final String sellOrderId = "sellOrderId";
        final TransactionId buyTransactionId = new TransactionId("buyTransactionId");
        final TransactionId sellTransactionId = new TransactionId("sellTransactionId");
        final OrderBookId orderbookId = new OrderBookId("orderbookId");
        final CoinId coinId = new CoinId("XPM");
        final PortfolioId buyPortfolioId = new PortfolioId();
        final PortfolioId sellPortfolioId = new PortfolioId();
        final DateTimeZone zone =  DateTimeZone.forID("America/New_York");

        ExecutedCommissionApplicationListener listener = new ExecutedCommissionApplicationListener();

        CommandGateway gateway = mock(CommandGateway.class);
        listener.setCommandGateway(gateway);
        listener.setZone("America/New_York");

        listener.handleSellCommission(
                new TradeExecutedEvent(
                        orderbookId,
                        coinId,
                        tradeAmount,
                        tradedPrice,
                        executedMoney,
                        buyOrderId,
                        sellOrderId,
                        buyCommission,
                        sellCommission,
                        buyTransactionId,
                        sellTransactionId,
                        buyPortfolioId,
                        sellPortfolioId,
                        tradeTime,
                        TradeType.SELL));

        ArgumentCaptor<StartSellCommissionTransactionCommand> captor = ArgumentCaptor.forClass(StartSellCommissionTransactionCommand.class);
        verify(gateway).send(captor.capture());
        StartSellCommissionTransactionCommand command = captor.getValue();

        assertThat(command, notNullValue());
        assertThat(command.getFeeTransactionId(), notNullValue());
        assertThat(command.getReceivedFeeId(), notNullValue());
        assertThat(command.getAccountReceivableFeeId(), notNullValue());
        assertThat(command.getOffsetId(), notNullValue());
        assertThat(command.getCommissionAmount(), equalTo(sellCommission));
        assertThat(command.getOrderId(), equalTo(sellOrderId));
        assertThat(command.getOrderTransactionId(), equalTo(sellTransactionId));
        assertThat(command.getPortfolioId(), equalTo(sellPortfolioId));
        assertThat(command.getTradeTime(), equalTo(tradeTime));
        assertThat(command.getDueDate(), equalTo(new DateTime(tradeTime, zone).toDate()));
        assertThat(command.getTradeType(), equalTo(TradeType.SELL));
        assertThat(command.getTradedPrice(), equalTo(tradedPrice));
        assertThat(command.getTradeAmount(), equalTo(tradeAmount));
        assertThat(command.getExecutedMoney(), equalTo(executedMoney));
        assertThat(command.getOrderBookId(), equalTo(orderbookId));
        assertThat(command.getCoinId(), equalTo(coinId));
    }

    @Test
    public void testHandleBuyCommission() throws Exception {
        final BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10000);
        final BigMoney tradedPrice = BigMoney.zero(CurrencyUnit.CAD);
        final BigMoney executedMoney = BigMoney.of(CurrencyUnit.CAD, 0.00001);
        final BigMoney buyCommission = BigMoney.zero(CurrencyUnit.CAD);
        final BigMoney sellCommission = BigMoney.zero(CurrencyUnit.of("BTC"));
        final Date tradeTime = currentTime();
        final String buyOrderId = "buyOrderId";
        final String sellOrderId = "sellOrderId";
        final TransactionId buyTransactionId = new TransactionId("buyTransactionId");
        final TransactionId sellTransactionId = new TransactionId("sellTransactionId");
        final OrderBookId orderbookId = new OrderBookId("orderbookId");
        final CoinId coinId = new CoinId("XPM");
        final PortfolioId buyPortfolioId = new PortfolioId();
        final PortfolioId sellPortfolioId = new PortfolioId();
        final DateTimeZone zone =  DateTimeZone.forID("America/New_York");

        ExecutedCommissionApplicationListener listener = new ExecutedCommissionApplicationListener();

        CommandGateway gateway = mock(CommandGateway.class);
        listener.setCommandGateway(gateway);
        listener.setZone("America/New_York");

        listener.handleBuyCommission(
                new TradeExecutedEvent(
                        orderbookId,
                        coinId,
                        tradeAmount,
                        tradedPrice,
                        executedMoney,
                        buyOrderId,
                        sellOrderId,
                        buyCommission,
                        sellCommission,
                        buyTransactionId,
                        sellTransactionId,
                        buyPortfolioId,
                        sellPortfolioId,
                        tradeTime,
                        TradeType.BUY));

        ArgumentCaptor<StartBuyCommissionTransactionCommand> captor = ArgumentCaptor.forClass(StartBuyCommissionTransactionCommand.class);
        verify(gateway).send(captor.capture());
        StartBuyCommissionTransactionCommand command = captor.getValue();

        assertThat(command, notNullValue());
        assertThat(command.getFeeTransactionId(), notNullValue());
        assertThat(command.getReceivedFeeId(), notNullValue());
        assertThat(command.getAccountReceivableFeeId(), notNullValue());
        assertThat(command.getOffsetId(), notNullValue());
        assertThat(command.getCommissionAmount(), equalTo(sellCommission));
        assertThat(command.getOrderId(), equalTo(sellOrderId));
        assertThat(command.getOrderTransactionId(), equalTo(sellTransactionId));
        assertThat(command.getPortfolioId(), equalTo(sellPortfolioId));
        assertThat(command.getTradeTime(), equalTo(tradeTime));
        assertThat(command.getDueDate(), equalTo(new DateTime(tradeTime, zone).toDate()));
        assertThat(command.getTradeType(), equalTo(TradeType.BUY));
        assertThat(command.getTradedPrice(), equalTo(tradedPrice));
        assertThat(command.getTradeAmount(), equalTo(tradeAmount));
        assertThat(command.getExecutedMoney(), equalTo(executedMoney));
        assertThat(command.getOrderBookId(), equalTo(orderbookId));
        assertThat(command.getCoinId(), equalTo(coinId));
    }
}
