package com.icoin.trading.fee.application.listener;


import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.command.commission.GainBuyCoinTransactionCommand;
import com.icoin.trading.api.fee.command.commission.GainSoldMoneyTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PayBuyCommissionTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PayBuyMoneyTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PaySellCommissionTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PaySoldCoinTransactionCommand;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.tradeengine.events.trade.TradeExecutedEvent;
import com.icoin.trading.api.users.domain.UserId;
import com.icoin.trading.fee.domain.DueDateService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-19
 * Time: PM8:35
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedTransactionFeeApplicationListenerTest {
    @Test
    public void testHandlePaySellCommission() throws Exception {
        final BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10000);
        final BigMoney tradedPrice = BigMoney.zero(CurrencyUnit.CAD);
        final BigMoney executedMoney = BigMoney.of(CurrencyUnit.CAD, 0.00001);
        final BigMoney buyCommission = BigMoney.parse("CAD 1");
        final BigMoney sellCommission = BigMoney.parse("BTC 1");
        final Date tradeTime = currentTime();
        final String buyOrderId = "buyOrderId";
        final String sellOrderId = "sellOrderId";
        final TransactionId buyTransactionId = new TransactionId("buyTransactionId");
        final TransactionId sellTransactionId = new TransactionId("sellTransactionId");
        final OrderBookId orderbookId = new OrderBookId("orderbookId");
        final CoinId coinId = new CoinId("XPM");
        final PortfolioId buyPortfolioId = new PortfolioId();
        final PortfolioId sellPortfolioId = new PortfolioId();
        final UserId buyUserId = new UserId();
        final UserId sellUserId = new UserId();
        final DateTimeZone zone = DateTimeZone.forID("America/New_York");
        final Date dueDate = new DateTime(tradeTime, zone).toDate();

        final DueDateService dueDateService = mock(DueDateService.class);
        when(dueDateService.computeDueDate(Mockito.any(Date.class))).thenReturn(dueDate);

        ExecutedTransactionFeeApplicationListener listener = new ExecutedTransactionFeeApplicationListener();

        CommandGateway gateway = mock(CommandGateway.class);
        listener.setCommandGateway(gateway);
        listener.setDueDateService(dueDateService);

        listener.handlePaySellCommission(
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
                        buyUserId,
                        sellUserId,
                        tradeTime,
                        TradeType.SELL));

        ArgumentCaptor<PaySellCommissionTransactionCommand> captor = ArgumentCaptor.forClass(PaySellCommissionTransactionCommand.class);
        verify(gateway).send(captor.capture());
        PaySellCommissionTransactionCommand command = captor.getValue();

        assertThat(command, notNullValue());
        assertThat(command.getFeeTransactionId(), notNullValue());
        assertThat(command.getPaidFeeId(), notNullValue());
        assertThat(command.getAccountPayableFeeId(), notNullValue());
        assertThat(command.getOffsetId(), notNullValue());
        assertThat(command.getCommission(), equalTo(sellCommission));
        assertThat(command.getOrderId(), equalTo(sellOrderId));
        assertThat(command.getOrderTransactionId(), equalTo(sellTransactionId));
        assertThat(command.getPortfolioId(), equalTo(sellPortfolioId));
        assertThat(command.getTradeTime(), equalTo(tradeTime));
        assertThat(command.getDueDate(), equalTo(dueDate));
        assertThat(command.getTradeType(), equalTo(TradeType.SELL));
        assertThat(command.getTradedPrice(), equalTo(tradedPrice));
        assertThat(command.getTradeAmount(), equalTo(tradeAmount));
        assertThat(command.getExecutedMoney(), equalTo(executedMoney));
        assertThat(command.getOrderBookId(), equalTo(orderbookId));
        assertThat(command.getCoinId(), equalTo(coinId));
        verify(dueDateService).computeDueDate(Mockito.any(Date.class));
    }

    @Test
    public void testHandlePayBuyCommission() throws Exception {
        final BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10000);
        final BigMoney tradedPrice = BigMoney.zero(CurrencyUnit.CAD);
        final BigMoney executedMoney = BigMoney.of(CurrencyUnit.CAD, 0.00001);
        final BigMoney buyCommission = BigMoney.parse("CAD 1");
        final BigMoney sellCommission = BigMoney.parse("BTC 1");
        final Date tradeTime = currentTime();
        final String buyOrderId = "buyOrderId";
        final String sellOrderId = "sellOrderId";
        final TransactionId buyTransactionId = new TransactionId("buyTransactionId");
        final TransactionId sellTransactionId = new TransactionId("sellTransactionId");
        final OrderBookId orderbookId = new OrderBookId("orderbookId");
        final CoinId coinId = new CoinId("XPM");
        final PortfolioId buyPortfolioId = new PortfolioId();
        final PortfolioId sellPortfolioId = new PortfolioId();
        final UserId buyUserId = new UserId();
        final UserId sellUserId = new UserId();
        final DateTimeZone zone = DateTimeZone.forID("America/New_York");
        final Date dueDate = new DateTime(tradeTime, zone).toDate();

        final DueDateService dueDateService = mock(DueDateService.class);
        when(dueDateService.computeDueDate(Mockito.any(Date.class))).thenReturn(dueDate);

        ExecutedTransactionFeeApplicationListener listener = new ExecutedTransactionFeeApplicationListener();

        CommandGateway gateway = mock(CommandGateway.class);
        listener.setCommandGateway(gateway);
        listener.setDueDateService(dueDateService);

        listener.handlePayBuyCommission(
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
                        buyUserId,
                        sellUserId,
                        tradeTime,
                        TradeType.BUY));

        ArgumentCaptor<PayBuyCommissionTransactionCommand> captor = ArgumentCaptor.forClass(PayBuyCommissionTransactionCommand.class);
        verify(gateway).send(captor.capture());
        PayBuyCommissionTransactionCommand command = captor.getValue();

        assertThat(command, notNullValue());
        assertThat(command.getFeeTransactionId(), notNullValue());
        assertThat(command.getPaidFeeId(), notNullValue());
        assertThat(command.getAccountPayableFeeId(), notNullValue());
        assertThat(command.getOffsetId(), notNullValue());
        assertThat(command.getCommission(), equalTo(sellCommission));
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
        verify(dueDateService).computeDueDate(Mockito.any(Date.class));
    }

    @Test
    public void testHandlePaySoldCoin() throws Exception {
        final BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10000);
        final BigMoney tradedPrice = BigMoney.zero(CurrencyUnit.CAD);
        final BigMoney executedMoney = BigMoney.of(CurrencyUnit.CAD, 0.00001);
        final BigMoney buyCommission = BigMoney.parse("CAD 1");
        final BigMoney sellCommission = BigMoney.parse("BTC 1");
        final Date tradeTime = currentTime();
        final String buyOrderId = "buyOrderId";
        final String sellOrderId = "sellOrderId";
        final TransactionId buyTransactionId = new TransactionId("buyTransactionId");
        final TransactionId sellTransactionId = new TransactionId("sellTransactionId");
        final OrderBookId orderbookId = new OrderBookId("orderbookId");
        final CoinId coinId = new CoinId("XPM");
        final PortfolioId buyPortfolioId = new PortfolioId();
        final PortfolioId sellPortfolioId = new PortfolioId();
        final UserId buyUserId = new UserId();
        final UserId sellUserId = new UserId();
        final DateTimeZone zone = DateTimeZone.forID("America/New_York");
        final Date dueDate = new DateTime(tradeTime, zone).toDate();

        final DueDateService dueDateService = mock(DueDateService.class);
        when(dueDateService.computeDueDate(Mockito.any(Date.class))).thenReturn(dueDate);

        ExecutedTransactionFeeApplicationListener listener = new ExecutedTransactionFeeApplicationListener();

        CommandGateway gateway = mock(CommandGateway.class);
        listener.setCommandGateway(gateway);
        listener.setDueDateService(dueDateService);

        listener.handlePaySoldCoin(
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
                        buyUserId,
                        sellUserId,
                        tradeTime,
                        TradeType.BUY));

        ArgumentCaptor<PaySoldCoinTransactionCommand> captor = ArgumentCaptor.forClass(PaySoldCoinTransactionCommand.class);
        verify(gateway).send(captor.capture());
        PaySoldCoinTransactionCommand command = captor.getValue();

        assertThat(command, notNullValue());
        assertThat(command.getFeeTransactionId(), notNullValue());
        assertThat(command.getPaidFeeId(), notNullValue());
        assertThat(command.getAccountPayableFeeId(), notNullValue());
        assertThat(command.getOffsetId(), notNullValue());
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
        verify(dueDateService).computeDueDate(Mockito.any(Date.class));
    }

    //seller gains money from sale
    @Test
    public void testHandleReceiveMoneyFromSale() throws Exception {
        final BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10000);
        final BigMoney tradedPrice = BigMoney.zero(CurrencyUnit.CAD);
        final BigMoney executedMoney = BigMoney.of(CurrencyUnit.CAD, 0.00001);
        final BigMoney buyCommission = BigMoney.parse("CAD 1");
        final BigMoney sellCommission = BigMoney.parse("BTC 1");
        final Date tradeTime = currentTime();
        final String buyOrderId = "buyOrderId";
        final String sellOrderId = "sellOrderId";
        final TransactionId buyTransactionId = new TransactionId("buyTransactionId");
        final TransactionId sellTransactionId = new TransactionId("sellTransactionId");
        final OrderBookId orderbookId = new OrderBookId("orderbookId");
        final CoinId coinId = new CoinId("XPM");
        final PortfolioId buyPortfolioId = new PortfolioId();
        final PortfolioId sellPortfolioId = new PortfolioId();
        final UserId buyUserId = new UserId();
        final UserId sellUserId = new UserId();
        final DateTimeZone zone = DateTimeZone.forID("America/New_York");
        final Date dueDate = new DateTime(tradeTime, zone).toDate();

        final DueDateService dueDateService = mock(DueDateService.class);
        when(dueDateService.computeDueDate(Mockito.any(Date.class))).thenReturn(dueDate);

        ExecutedTransactionFeeApplicationListener listener = new ExecutedTransactionFeeApplicationListener();

        CommandGateway gateway = mock(CommandGateway.class);
        listener.setCommandGateway(gateway);
        listener.setDueDateService(dueDateService);

        listener.handleReceiveMoneyFromSale(
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
                        buyUserId,
                        sellUserId,
                        tradeTime,
                        TradeType.BUY));

        ArgumentCaptor<GainSoldMoneyTransactionCommand> captor = ArgumentCaptor.forClass(GainSoldMoneyTransactionCommand.class);
        verify(gateway).send(captor.capture());
        GainSoldMoneyTransactionCommand command = captor.getValue();

        assertThat(command, notNullValue());
        assertThat(command.getFeeTransactionId(), notNullValue());
        assertThat(command.getReceivedFeeId(), notNullValue());
        assertThat(command.getAccountReceivableFeeId(), notNullValue());
        assertThat(command.getOffsetId(), notNullValue());
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

        verify(dueDateService).computeDueDate(Mockito.any(Date.class));
    }

    //buyer pay money for coins
    @Test
    public void testHandlePayForCoins() throws Exception {
        final BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10000);
        final BigMoney tradedPrice = BigMoney.zero(CurrencyUnit.CAD);
        final BigMoney executedMoney = BigMoney.of(CurrencyUnit.CAD, 0.00001);
        final BigMoney buyCommission = BigMoney.parse("CAD 1");
        final BigMoney sellCommission = BigMoney.parse("BTC 1");
        final Date tradeTime = currentTime();
        final String buyOrderId = "buyOrderId";
        final String sellOrderId = "sellOrderId";
        final TransactionId buyTransactionId = new TransactionId("buyTransactionId");
        final TransactionId sellTransactionId = new TransactionId("sellTransactionId");
        final OrderBookId orderbookId = new OrderBookId("orderbookId");
        final CoinId coinId = new CoinId("XPM");
        final PortfolioId buyPortfolioId = new PortfolioId();
        final PortfolioId sellPortfolioId = new PortfolioId();
        final UserId buyUserId = new UserId();
        final UserId sellUserId = new UserId();
        final DateTimeZone zone = DateTimeZone.forID("America/New_York");
        final Date dueDate = new DateTime(tradeTime, zone).toDate();

        final DueDateService dueDateService = mock(DueDateService.class);
        when(dueDateService.computeDueDate(Mockito.any(Date.class))).thenReturn(dueDate);

        ExecutedTransactionFeeApplicationListener listener = new ExecutedTransactionFeeApplicationListener();

        CommandGateway gateway = mock(CommandGateway.class);
        listener.setCommandGateway(gateway);
        listener.setDueDateService(dueDateService);

        listener.handlePayForCoins(
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
                        buyUserId,
                        sellUserId,
                        tradeTime,
                        TradeType.BUY));

        ArgumentCaptor<PayBuyMoneyTransactionCommand> captor = ArgumentCaptor.forClass(PayBuyMoneyTransactionCommand.class);
        verify(gateway).send(captor.capture());
        PayBuyMoneyTransactionCommand command = captor.getValue();

        assertThat(command, notNullValue());
        assertThat(command.getFeeTransactionId(), notNullValue());
        assertThat(command.getPaidFeeId(), notNullValue());
        assertThat(command.getAccountPayableFeeId(), notNullValue());
        assertThat(command.getOffsetId(), notNullValue());
        assertThat(command.getOrderId(), equalTo(buyOrderId));
        assertThat(command.getOrderTransactionId(), equalTo(buyTransactionId));
        assertThat(command.getPortfolioId(), equalTo(buyPortfolioId));
        assertThat(command.getTradeTime(), equalTo(tradeTime));
        assertThat(command.getDueDate(), equalTo(new DateTime(tradeTime, zone).toDate()));
        assertThat(command.getTradeType(), equalTo(TradeType.BUY));
        assertThat(command.getTradedPrice(), equalTo(tradedPrice));
        assertThat(command.getTradeAmount(), equalTo(tradeAmount));
        assertThat(command.getExecutedMoney(), equalTo(executedMoney));
        assertThat(command.getOrderBookId(), equalTo(orderbookId));
        assertThat(command.getCoinId(), equalTo(coinId));
        verify(dueDateService).computeDueDate(Mockito.any(Date.class));
    }

    //bought coins from seller
    @Test
    public void testHandleReceiveCoinsFromSale() throws Exception {
        final BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10000);
        final BigMoney tradedPrice = BigMoney.zero(CurrencyUnit.CAD);
        final BigMoney executedMoney = BigMoney.of(CurrencyUnit.CAD, 0.00001);
        final BigMoney buyCommission = BigMoney.parse("CAD 1");
        final BigMoney sellCommission = BigMoney.parse("BTC 1");
        final Date tradeTime = currentTime();
        final String buyOrderId = "buyOrderId";
        final String sellOrderId = "sellOrderId";
        final TransactionId buyTransactionId = new TransactionId("buyTransactionId");
        final TransactionId sellTransactionId = new TransactionId("sellTransactionId");
        final OrderBookId orderbookId = new OrderBookId("orderbookId");
        final CoinId coinId = new CoinId("XPM");
        final PortfolioId buyPortfolioId = new PortfolioId();
        final PortfolioId sellPortfolioId = new PortfolioId();
        final UserId buyUserId = new UserId();
        final UserId sellUserId = new UserId();
        final DateTimeZone zone = DateTimeZone.forID("America/New_York");
        final Date dueDate = new DateTime(tradeTime, zone).toDate();

        final DueDateService dueDateService = mock(DueDateService.class);
        when(dueDateService.computeDueDate(Mockito.any(Date.class))).thenReturn(dueDate);

        ExecutedTransactionFeeApplicationListener listener = new ExecutedTransactionFeeApplicationListener();

        CommandGateway gateway = mock(CommandGateway.class);
        listener.setCommandGateway(gateway);
        listener.setDueDateService(dueDateService);

        listener.handleReceiveCoinsFromSale(
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
                        buyUserId,
                        sellUserId,
                        tradeTime,
                        TradeType.BUY));

        ArgumentCaptor<GainBuyCoinTransactionCommand> captor = ArgumentCaptor.forClass(GainBuyCoinTransactionCommand.class);
        verify(gateway).send(captor.capture());
        GainBuyCoinTransactionCommand command = captor.getValue();

        assertThat(command, notNullValue());
        assertThat(command.getFeeTransactionId(), notNullValue());
        assertThat(command.getReceivedFeeId(), notNullValue());
        assertThat(command.getAccountReceivableFeeId(), notNullValue());
        assertThat(command.getOffsetId(), notNullValue());
        assertThat(command.getOrderId(), equalTo(buyOrderId));
        assertThat(command.getOrderTransactionId(), equalTo(buyTransactionId));
        assertThat(command.getPortfolioId(), equalTo(buyPortfolioId));
        assertThat(command.getTradeTime(), equalTo(tradeTime));
        assertThat(command.getDueDate(), equalTo(new DateTime(tradeTime, zone).toDate()));
        assertThat(command.getTradeType(), equalTo(TradeType.BUY));
        assertThat(command.getTradedPrice(), equalTo(tradedPrice));
        assertThat(command.getTradeAmount(), equalTo(tradeAmount));
        assertThat(command.getExecutedMoney(), equalTo(executedMoney));
        assertThat(command.getOrderBookId(), equalTo(orderbookId));
        assertThat(command.getCoinId(), equalTo(coinId));

        verify(dueDateService).computeDueDate(Mockito.any(Date.class));
    }
}
