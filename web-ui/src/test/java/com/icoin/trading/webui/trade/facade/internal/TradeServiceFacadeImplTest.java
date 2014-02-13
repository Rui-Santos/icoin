package com.icoin.trading.webui.trade.facade.internal;

import com.icoin.trading.tradeengine.application.command.transaction.command.StartBuyTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartSellTransactionCommand;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.webui.order.BuyOrder;
import com.icoin.trading.webui.order.SellOrder;
import com.icoin.trading.webui.user.facade.UserServiceFacade;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.money.BigMoney;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-20
 * Time: PM3:24
 * To change this template use File | Settings | File Templates.
 */
public class TradeServiceFacadeImplTest {

    @Test
    public void testPrepareBuyOrder() {
        String coinId = "BTC";
        String coinName = "Bitcoin";
        CurrencyPair currencyPair = CurrencyPair.BTC_AUD;
        BigMoney lowestSellPrice = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 100D);
        BigMoney balance = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 100);
        BigMoney reserved = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 12);

        CoinEntry coin = new CoinEntry();
        coin.setName(coinName);
        coin.setPrimaryKey(coinId);

        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setCurrencyPair(currencyPair);
        orderBookEntry.setLowestSellPrice(lowestSellPrice);

        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.setAmountOfMoney(balance);
        portfolioEntry.setReservedAmountOfMoney(reserved);

        CoinQueryRepository coinRepository = mock(CoinQueryRepository.class);
        when(coinRepository.findOne(eq(coinId))).thenReturn(coin);

        TradeServiceFacadeImpl facade = new TradeServiceFacadeImpl();
        facade.setCoinRepository(coinRepository);

        final BuyOrder buyOrder = facade.prepareBuyOrder(coinId, currencyPair, orderBookEntry, portfolioEntry);

        assertThat(buyOrder, notNullValue());
        assertThat(buyOrder.getCoinId(), equalTo(coinId));
        assertThat(buyOrder.getCoinName(), equalTo(coinName));
        assertThat(buyOrder.getAmountCcy(), equalTo(currencyPair.getBaseCurrency()));
        assertThat(buyOrder.getPriceCcy(), equalTo(currencyPair.getCounterCurrency()));
        assertThat(buyOrder.getSuggestedPrice(), notNullValue());

        assertThat(buyOrder.getSuggestedPrice(), is(closeTo(lowestSellPrice.getAmount(), BigDecimal.valueOf(0.00000000001d))));
        assertThat(buyOrder.getBalance(), notNullValue());
        assertThat(buyOrder.getBalance(), is(closeTo(balance.minus(reserved).getAmount(), BigDecimal.valueOf(0.00000000001d))));

        verify(coinRepository).findOne(eq(coinId));
    }


    @Test
    public void testPrepareBuyOrderUnAuthed() {
        String coinId = "BTC";
        String coinName = "Bitcoin";
        CurrencyPair currencyPair = CurrencyPair.BTC_AUD;
        BigMoney lowestSellPrice = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 100D);

        CoinEntry coin = new CoinEntry();
        coin.setName(coinName);
        coin.setPrimaryKey(coinId);

        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setCurrencyPair(currencyPair);
        orderBookEntry.setLowestSellPrice(lowestSellPrice);

        CoinQueryRepository coinRepository = mock(CoinQueryRepository.class);
        when(coinRepository.findOne(eq(coinId))).thenReturn(coin);

        UserServiceFacade userServiceFacade = mock(UserServiceFacade.class);

        TradeServiceFacadeImpl facade = new TradeServiceFacadeImpl();
        facade.setCoinRepository(coinRepository);
        facade.setUserServiceFacade(userServiceFacade);


        final BuyOrder buyOrder = facade.prepareBuyOrder(coinId, currencyPair, orderBookEntry, null);

        assertThat(buyOrder, notNullValue());
        assertThat(buyOrder.getCoinId(), equalTo(coinId));
        assertThat(buyOrder.getCoinName(), equalTo(coinName));
        assertThat(buyOrder.getAmountCcy(), equalTo(currencyPair.getBaseCurrency()));
        assertThat(buyOrder.getPriceCcy(), equalTo(currencyPair.getCounterCurrency()));
        assertThat(buyOrder.getSuggestedPrice(), notNullValue());
        assertThat(buyOrder.getSuggestedPrice(), is(closeTo(lowestSellPrice.getAmount(), BigDecimal.valueOf(0.00000000001d))));
        assertThat(buyOrder.getBalance(), anyOf(nullValue(), equalTo(BigDecimal.ZERO)));

        verify(coinRepository).findOne(eq(coinId));
        verify(userServiceFacade).obtainPortfolioForUser();
    }

    @Test
    public void testPrepareBuyOrderObtainAuth() {
        String coinId = "BTC";
        String coinName = "Bitcoin";
        CurrencyPair currencyPair = CurrencyPair.BTC_AUD;
        BigMoney lowestSellPrice = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 100D);
        BigMoney balance = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 100);
        BigMoney reserved = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 12);

        CoinEntry coin = new CoinEntry();
        coin.setName(coinName);
        coin.setPrimaryKey(coinId);

        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setCurrencyPair(currencyPair);
        orderBookEntry.setLowestSellPrice(lowestSellPrice);

        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.setAmountOfMoney(balance);
        portfolioEntry.setReservedAmountOfMoney(reserved);

        CoinQueryRepository coinRepository = mock(CoinQueryRepository.class);
        when(coinRepository.findOne(eq(coinId))).thenReturn(coin);


        UserServiceFacade userServiceFacade = mock(UserServiceFacade.class);
        when(userServiceFacade.obtainPortfolioForUser()).thenReturn(portfolioEntry);

        TradeServiceFacadeImpl facade = new TradeServiceFacadeImpl();
        facade.setCoinRepository(coinRepository);
        facade.setUserServiceFacade(userServiceFacade);


        final BuyOrder buyOrder = facade.prepareBuyOrder(coinId, currencyPair, orderBookEntry, null);

        assertThat(buyOrder, notNullValue());
        assertThat(buyOrder.getCoinId(), equalTo(coinId));
        assertThat(buyOrder.getCoinName(), equalTo(coinName));
        assertThat(buyOrder.getAmountCcy(), equalTo(currencyPair.getBaseCurrency()));
        assertThat(buyOrder.getPriceCcy(), equalTo(currencyPair.getCounterCurrency()));
        assertThat(buyOrder.getSuggestedPrice(), notNullValue());
        assertThat(buyOrder.getSuggestedPrice(), is(closeTo(lowestSellPrice.getAmount(), BigDecimal.valueOf(0.00000000001d))));
        assertThat(buyOrder.getBalance(), notNullValue());
        assertThat(buyOrder.getBalance(), is(closeTo(balance.minus(reserved).getAmount(), BigDecimal.valueOf(0.00000000001d))));

        verify(coinRepository).findOne(eq(coinId));
        verify(userServiceFacade).obtainPortfolioForUser();
    }

    @Test
    public void testPrepareBuyOrderNullOrderBook() {
        String coinId = "BTC";
        String coinName = "Bitcoin";
        CurrencyPair currencyPair = CurrencyPair.BTC_AUD;
        BigMoney lowestSellPrice = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 100D);
        BigMoney balance = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 88);
        BigMoney reserved = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 0);

        CoinEntry coin = new CoinEntry();
        coin.setName(coinName);
        coin.setPrimaryKey(coinId);

        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setCurrencyPair(currencyPair);
        orderBookEntry.setLowestSellPrice(lowestSellPrice);

        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.setAmountOfMoney(balance);
        portfolioEntry.setReservedAmountOfMoney(reserved);

        CoinQueryRepository coinRepository = mock(CoinQueryRepository.class);
        when(coinRepository.findOne(eq(coinId))).thenReturn(coin);

        OrderBookQueryRepository orderBookRepository = mock(OrderBookQueryRepository.class);
        when(orderBookRepository.findByCurrencyPair(eq(currencyPair))).thenReturn(orderBookEntry);

        UserServiceFacade userServiceFacade = mock(UserServiceFacade.class);
        when(userServiceFacade.obtainPortfolioForUser()).thenReturn(portfolioEntry);

        TradeServiceFacadeImpl facade = new TradeServiceFacadeImpl();
        facade.setCoinRepository(coinRepository);
        facade.setUserServiceFacade(userServiceFacade);
        facade.setOrderBookRepository(orderBookRepository);


        final BuyOrder buyOrder = facade.prepareBuyOrder(coinId, currencyPair, null, null);

        assertThat(buyOrder, notNullValue());
        assertThat(buyOrder.getCoinId(), equalTo(coinId));
        assertThat(buyOrder.getCoinName(), equalTo(coinName));
        assertThat(buyOrder.getAmountCcy(), equalTo(currencyPair.getBaseCurrency()));
        assertThat(buyOrder.getPriceCcy(), equalTo(currencyPair.getCounterCurrency()));
        assertThat(buyOrder.getSuggestedPrice(), notNullValue());
        assertThat(buyOrder.getSuggestedPrice(), is(closeTo(lowestSellPrice.getAmount(), BigDecimal.valueOf(0.00000000001d))));
        assertThat(buyOrder.getBalance(), notNullValue());
        assertThat(buyOrder.getBalance(), is(closeTo(balance.getAmount(), BigDecimal.valueOf(0.00000000001d))));

        verify(coinRepository).findOne(eq(coinId));
        verify(orderBookRepository).findByCurrencyPair(eq(currencyPair));
        verify(userServiceFacade).obtainPortfolioForUser();
    }

    @Test
    public void testPrepareBuyOrderOrderBookNotFound() {
        String coinId = "BTC";
        String coinName = "Bitcoin";
        CurrencyPair currencyPair = CurrencyPair.BTC_AUD;
        BigMoney balance = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 88);

        CoinEntry coin = new CoinEntry();
        coin.setName(coinName);
        coin.setPrimaryKey(coinId);

        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.setAmountOfMoney(balance);

        CoinQueryRepository coinRepository = mock(CoinQueryRepository.class);
        when(coinRepository.findOne(eq(coinId))).thenReturn(coin);

        OrderBookQueryRepository orderBookRepository = mock(OrderBookQueryRepository.class);
        when(orderBookRepository.findByCurrencyPair(eq(currencyPair))).thenReturn(null);

        UserServiceFacade userServiceFacade = mock(UserServiceFacade.class);
        when(userServiceFacade.obtainPortfolioForUser()).thenReturn(portfolioEntry);

        TradeServiceFacadeImpl facade = new TradeServiceFacadeImpl();
        facade.setCoinRepository(coinRepository);
        facade.setUserServiceFacade(userServiceFacade);
        facade.setOrderBookRepository(orderBookRepository);


        final BuyOrder buyOrder = facade.prepareBuyOrder(coinId, currencyPair, null, null);

        assertThat(buyOrder, notNullValue());
        assertThat(buyOrder.getCoinId(), equalTo(coinId));
        assertThat(buyOrder.getCoinName(), equalTo(coinName));
        assertThat(buyOrder.getAmountCcy(), equalTo(currencyPair.getBaseCurrency()));
        assertThat(buyOrder.getPriceCcy(), equalTo(currencyPair.getCounterCurrency()));
        assertThat(buyOrder.getSuggestedPrice(), is(closeTo(BigDecimal.ZERO, BigDecimal.valueOf(0.00000000001d))));
        assertThat(buyOrder.getBalance(), anyOf(nullValue(), equalTo(BigDecimal.ZERO)));

        verify(coinRepository).findOne(eq(coinId));
        verify(orderBookRepository).findByCurrencyPair(eq(currencyPair));
        verify(userServiceFacade, never()).obtainPortfolioForUser();
    }

    @Test
    public void testPrepareSellOrder() {
        String coinId = "BTC";
        String coinName = "Bitcoin";
        CurrencyPair currencyPair = CurrencyPair.BTC_CNY;
        BigMoney highestBuyPrice = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 100D);
        BigMoney balance = BigMoney.of(currencyPair.getBaseCurrencyUnit(), 88);

        CoinEntry coin = new CoinEntry();
        coin.setName(coinName);
        coin.setPrimaryKey(coinId);

        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setCurrencyPair(currencyPair);
        orderBookEntry.setHighestBuyPrice(highestBuyPrice);

        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.createItem(coinId, coinName);
        portfolioEntry.addItemInPossession(coinId, BigMoney.of(balance));


        CoinQueryRepository coinRepository = mock(CoinQueryRepository.class);
        when(coinRepository.findOne(eq(coinId))).thenReturn(coin);

        TradeServiceFacadeImpl facade = new TradeServiceFacadeImpl();
        facade.setCoinRepository(coinRepository);

        final SellOrder sellOrder = facade.prepareSellOrder(coinId, currencyPair, orderBookEntry, portfolioEntry);

        assertThat(sellOrder, notNullValue());
        assertThat(sellOrder.getCoinId(), equalTo(coinId));
        assertThat(sellOrder.getCoinName(), equalTo(coinName));
        assertThat(sellOrder.getAmountCcy(), equalTo(currencyPair.getBaseCurrency()));
        assertThat(sellOrder.getPriceCcy(), equalTo(currencyPair.getCounterCurrency()));
        assertThat(sellOrder.getSuggestedPrice(), notNullValue());
        assertThat(sellOrder.getSuggestedPrice(), is(closeTo(highestBuyPrice.getAmount(), BigDecimal.valueOf(0.00000000001d))));
        assertThat(sellOrder.getBalance(), notNullValue());
        assertThat(sellOrder.getBalance(), is(closeTo(balance.getAmount(), BigDecimal.valueOf(0.00000000001d))));

        verify(coinRepository).findOne(eq(coinId));
    }

    @Test
    public void testPrepareSellOrderUnAuthed() {
        String coinId = "BTC";
        String coinName = "Bitcoin";
        CurrencyPair currencyPair = CurrencyPair.BTC_CNY;
        BigMoney highestBuyPrice = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 100D);
        BigMoney balance = BigMoney.of(currencyPair.getBaseCurrencyUnit(), 88);

        CoinEntry coin = new CoinEntry();
        coin.setName(coinName);
        coin.setPrimaryKey(coinId);

        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setCurrencyPair(currencyPair);
        orderBookEntry.setHighestBuyPrice(highestBuyPrice);

        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.createItem(coinId, coinName);
        portfolioEntry.addItemInPossession(coinId, BigMoney.of(balance));



        CoinQueryRepository coinRepository = mock(CoinQueryRepository.class);
        when(coinRepository.findOne(eq(coinId))).thenReturn(coin);

        UserServiceFacade userServiceFacade = mock(UserServiceFacade.class);

        TradeServiceFacadeImpl facade = new TradeServiceFacadeImpl();
        facade.setCoinRepository(coinRepository);
        facade.setUserServiceFacade(userServiceFacade);

        final SellOrder sellOrder = facade.prepareSellOrder(coinId, currencyPair, orderBookEntry, null);

        assertThat(sellOrder, notNullValue());
        assertThat(sellOrder.getCoinId(), equalTo(coinId));
        assertThat(sellOrder.getCoinName(), equalTo(coinName));
        assertThat(sellOrder.getAmountCcy(), equalTo(currencyPair.getBaseCurrency()));
        assertThat(sellOrder.getPriceCcy(), equalTo(currencyPair.getCounterCurrency()));
        assertThat(sellOrder.getSuggestedPrice(), notNullValue());

        assertThat(sellOrder.getSuggestedPrice(), is(closeTo(highestBuyPrice.getAmount(), new BigDecimal(0.00000000001))));
        assertThat(sellOrder.getBalance(), is(closeTo(BigDecimal.ZERO, new BigDecimal(0.00000000001))));

        verify(coinRepository).findOne(eq(coinId));
    }

    @Test
    public void testPrepareSellOrderObtainAuth() {
        String coinId = "BTC";
        String coinName = "Bitcoin";
        CurrencyPair currencyPair = CurrencyPair.BTC_CNY;
        BigMoney highestBuyPrice = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 100D);
        BigMoney balance = BigMoney.of(currencyPair.getBaseCurrencyUnit(), 88);

        CoinEntry coin = new CoinEntry();
        coin.setName(coinName);
        coin.setPrimaryKey(coinId);

        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setCurrencyPair(currencyPair);
        orderBookEntry.setHighestBuyPrice(highestBuyPrice);

        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.createItem(coinId, coinName);
        portfolioEntry.addItemInPossession(coinId, BigMoney.of(balance));



        CoinQueryRepository coinRepository = mock(CoinQueryRepository.class);
        when(coinRepository.findOne(eq(coinId))).thenReturn(coin);

        UserServiceFacade userServiceFacade = mock(UserServiceFacade.class);
        when(userServiceFacade.obtainPortfolioForUser()).thenReturn(portfolioEntry);

        TradeServiceFacadeImpl facade = new TradeServiceFacadeImpl();
        facade.setCoinRepository(coinRepository);
        facade.setUserServiceFacade(userServiceFacade);

        final SellOrder sellOrder = facade.prepareSellOrder(coinId, currencyPair, orderBookEntry, null);

        assertThat(sellOrder, notNullValue());
        assertThat(sellOrder.getCoinId(), equalTo(coinId));
        assertThat(sellOrder.getCoinName(), equalTo(coinName));
        assertThat(sellOrder.getAmountCcy(), equalTo(currencyPair.getBaseCurrency()));
        assertThat(sellOrder.getPriceCcy(), equalTo(currencyPair.getCounterCurrency()));
        assertThat(sellOrder.getSuggestedPrice(), notNullValue());

        assertThat(sellOrder.getSuggestedPrice(), is(closeTo(highestBuyPrice.getAmount(), new BigDecimal(0.00000000001))));
        assertThat(sellOrder.getBalance(), is(closeTo(balance.getAmount(), new BigDecimal(0.00000000001))));

        verify(coinRepository).findOne(eq(coinId));
        verify(userServiceFacade).obtainPortfolioForUser();
    }

    @Test
    public void testPrepareSellOrderNullOrderBook() {
        String coinId = "BTC";
        String coinName = "Bitcoin";
        CurrencyPair currencyPair = CurrencyPair.BTC_CNY;
        BigMoney highestBuyPrice = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 100D);
        BigMoney balance = BigMoney.of(currencyPair.getBaseCurrencyUnit(), 88);

        CoinEntry coin = new CoinEntry();
        coin.setName(coinName);
        coin.setPrimaryKey(coinId);

        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setCurrencyPair(currencyPair);
        orderBookEntry.setHighestBuyPrice(highestBuyPrice);

        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.createItem(coinId, coinName);
        portfolioEntry.addItemInPossession(coinId, BigMoney.of(balance));



        CoinQueryRepository coinRepository = mock(CoinQueryRepository.class);
        when(coinRepository.findOne(eq(coinId))).thenReturn(coin);

        OrderBookQueryRepository orderBookRepository = mock(OrderBookQueryRepository.class);
        when(orderBookRepository.findByCurrencyPair(eq(currencyPair))).thenReturn(orderBookEntry);

        UserServiceFacade userServiceFacade = mock(UserServiceFacade.class);
        when(userServiceFacade.obtainPortfolioForUser()).thenReturn(portfolioEntry);

        TradeServiceFacadeImpl facade = new TradeServiceFacadeImpl();
        facade.setCoinRepository(coinRepository);
        facade.setOrderBookRepository(orderBookRepository);
        facade.setUserServiceFacade(userServiceFacade);

        final SellOrder sellOrder = facade.prepareSellOrder(coinId, currencyPair, null, null);

        assertThat(sellOrder, notNullValue());
        assertThat(sellOrder.getCoinId(), equalTo(coinId));
        assertThat(sellOrder.getCoinName(), equalTo(coinName));
        assertThat(sellOrder.getAmountCcy(), equalTo(currencyPair.getBaseCurrency()));
        assertThat(sellOrder.getPriceCcy(), equalTo(currencyPair.getCounterCurrency()));
        assertThat(sellOrder.getSuggestedPrice(), notNullValue());

        assertThat(sellOrder.getSuggestedPrice(), is(closeTo(highestBuyPrice.getAmount(), new BigDecimal(0.00000000001))));
        assertThat(sellOrder.getBalance(), is(closeTo(balance.getAmount(), new BigDecimal(0.00000000001))));

        verify(coinRepository).findOne(eq(coinId));
        verify(orderBookRepository).findByCurrencyPair(eq(currencyPair));
        verify(userServiceFacade).obtainPortfolioForUser();
    }

    @Test
    public void testPrepareSellOrderOrderBookNotFound() {
        String coinId = "BTC";
        String coinName = "Bitcoin";
        CurrencyPair currencyPair = CurrencyPair.BTC_CNY;
        BigMoney balance = BigMoney.of(currencyPair.getBaseCurrencyUnit(), 88);

        CoinEntry coin = new CoinEntry();
        coin.setName(coinName);
        coin.setPrimaryKey(coinId);

        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.createItem(coinId, coinName);
        portfolioEntry.addItemInPossession(coinId, BigMoney.of(balance));

        CoinQueryRepository coinRepository = mock(CoinQueryRepository.class);
        when(coinRepository.findOne(eq(coinId))).thenReturn(coin);

        OrderBookQueryRepository orderBookRepository = mock(OrderBookQueryRepository.class);
        when(orderBookRepository.findByCurrencyPair(eq(currencyPair))).thenReturn(null);

        UserServiceFacade userServiceFacade = mock(UserServiceFacade.class);
        when(userServiceFacade.obtainPortfolioForUser()).thenReturn(portfolioEntry);

        TradeServiceFacadeImpl facade = new TradeServiceFacadeImpl();
        facade.setCoinRepository(coinRepository);
        facade.setUserServiceFacade(userServiceFacade);
        facade.setOrderBookRepository(orderBookRepository);


        final SellOrder sellOrder = facade.prepareSellOrder(coinId, currencyPair, null, null);

        assertThat(sellOrder, notNullValue());
        assertThat(sellOrder.getCoinId(), equalTo(coinId));
        assertThat(sellOrder.getCoinName(), equalTo(coinName));
        assertThat(sellOrder.getAmountCcy(), equalTo(currencyPair.getBaseCurrency()));
        assertThat(sellOrder.getPriceCcy(), equalTo(currencyPair.getCounterCurrency()));
        assertThat(sellOrder.getSuggestedPrice(), notNullValue());
        assertThat(sellOrder.getSuggestedPrice(), is(closeTo(BigDecimal.ZERO, BigDecimal.valueOf(0.00000000001d))));
        assertThat(sellOrder.getBalance(), anyOf(nullValue(), equalTo(BigDecimal.ZERO)));

        verify(coinRepository).findOne(eq(coinId));
        verify(orderBookRepository).findByCurrencyPair(eq(currencyPair));
        verify(userServiceFacade, never()).obtainPortfolioForUser();
    }

    @Test
    public void testSellOrder() {
        final TransactionId transactionId = new TransactionId();
        final OrderBookId orderBookId = new OrderBookId();
        final CoinId coinId = new CoinId("BTC");
        final PortfolioId portfolioId = new PortfolioId();
        CurrencyPair currencyPair = CurrencyPair.BTC_CNY;
        BigMoney price = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 100);
        BigMoney amount = BigMoney.of(currencyPair.getBaseCurrencyUnit(), 300);

        final StartSellTransactionCommand command =
                new StartSellTransactionCommand(transactionId, coinId, currencyPair,
                        orderBookId, portfolioId, amount, price);

        CommandGateway gateway = mock(CommandGateway.class);
        doNothing().when(gateway).send(eq(command));

        TradeServiceFacadeImpl facade = new TradeServiceFacadeImpl();
        facade.setCommandGateway(gateway);

        facade.sellOrder(transactionId, coinId.toString(), currencyPair, orderBookId.toString(), portfolioId.toString(), amount, price);

        verify(gateway).send(eq(command));
    }

    @Test
    public void testBuyOrder() {
        final TransactionId transactionId = new TransactionId();
        final OrderBookId orderBookId = new OrderBookId();
        final CoinId coinId = new CoinId("BTC");
        final PortfolioId portfolioId = new PortfolioId();
        CurrencyPair currencyPair = CurrencyPair.BTC_CNY;
        BigMoney price = BigMoney.of(currencyPair.getCounterCurrencyUnit(), 100);
        BigMoney amount = BigMoney.of(currencyPair.getBaseCurrencyUnit(), 300);

        final StartBuyTransactionCommand command =
                new StartBuyTransactionCommand(transactionId, coinId, currencyPair, orderBookId, portfolioId, amount, price);

        CommandGateway gateway = mock(CommandGateway.class);
        doNothing().when(gateway).send(eq(command));

        TradeServiceFacadeImpl facade = new TradeServiceFacadeImpl();
        facade.setCommandGateway(gateway);

        facade.buyOrder(transactionId, coinId.toString(), currencyPair, orderBookId.toString(), portfolioId.toString(), amount, price);

        verify(gateway).send(eq(command));
    }
}
