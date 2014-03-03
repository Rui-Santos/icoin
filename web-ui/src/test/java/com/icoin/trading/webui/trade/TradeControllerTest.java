package com.icoin.trading.webui.trade;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.OrderType;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.webui.order.BuyOrder;
import com.icoin.trading.webui.order.SellOrder;
import com.icoin.trading.webui.user.facade.UserServiceFacade;
import com.icoin.trading.webui.trade.facade.TradeServiceFacade;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/18/13
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@WebAppConfiguration
//@ContextConfiguration("file:main/webapp/WEB-INF/dispatcher-servlet.xml")
public class TradeControllerTest {
    private final TradeController tradeController = new TradeController();

    private TradeServiceFacade tradeServiceFacade = mock(TradeServiceFacade.class);
    private UserServiceFacade userServiceFacade = mock(UserServiceFacade.class);

    //    @Autowired
    private org.springframework.web.context.WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
//        mockMvc = standaloneSetup(new PersonController()).addFilters(new CharacterEncodingFilter()).build();
        this.mockMvc = standaloneSetup(tradeController)
                .defaultRequest(get("/index")
//                        .contextPath("/app").servletPath("/main")
//                        .accept(MediaType.APPLICATION_JSON)
                ).setViewResolvers(viewResolver)
                .build();

        tradeController.setTradeServiceFacade(tradeServiceFacade);
        tradeController.setUserServiceFacade(userServiceFacade);
    }

    @Test
    public void testIndex() throws Exception {
        OrderBookEntry orderBookEntry = new OrderBookEntry();

        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.setPrimaryKey(new PortfolioId().toString());

        when(userServiceFacade.obtainPortfolioForUser()).thenReturn(portfolioEntry);

        BuyOrder buyOrder = new BuyOrder();
        SellOrder sellOrder = new SellOrder();
        when(tradeServiceFacade.
                prepareSellOrder(eq(TradeController.DEFUALT_COIN), eq(TradeController.DEFAULT_CCY_PAIR), eq(orderBookEntry), eq(portfolioEntry)))
                .thenReturn(sellOrder);
        when(tradeServiceFacade.
                prepareBuyOrder(eq(TradeController.DEFUALT_COIN), eq(TradeController.DEFAULT_CCY_PAIR), eq(orderBookEntry), eq(portfolioEntry)))
                .thenReturn(buyOrder);
        CoinEntry coinEntry = new CoinEntry();
        when(tradeServiceFacade.loadOrderBookByCurrencyPair(eq(TradeController.DEFAULT_CCY_PAIR))).thenReturn(orderBookEntry);
        when(tradeServiceFacade.loadCoin(eq(TradeController.DEFUALT_COIN))).thenReturn(coinEntry);


        mockMvc.perform(get("/index")).andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "orderBook",
                        "sellOrder",
                        "buyOrder",
                        "coin",
                        "activeOrders",
                        "buyOrders",
                        "sellOrders",
                        "executedTrades"));

        verify(tradeServiceFacade).prepareSellOrder(eq(TradeController.DEFUALT_COIN), eq(TradeController.DEFAULT_CCY_PAIR), eq(orderBookEntry), eq(portfolioEntry));
        verify(tradeServiceFacade).prepareBuyOrder(eq(TradeController.DEFUALT_COIN), eq(TradeController.DEFAULT_CCY_PAIR), eq(orderBookEntry), eq(portfolioEntry));
        verify(tradeServiceFacade).loadCoin(eq(TradeController.DEFUALT_COIN));
        verify(tradeServiceFacade).loadOrderBookByCurrencyPair(eq(TradeController.DEFAULT_CCY_PAIR));
    }

    @Test
    public void testBuy() throws Exception {
        final CurrencyPair btc = new CurrencyPair("BTC");
        final BigMoney total = BigMoney.of(btc.getCounterCurrencyUnit(), 100);
        final BigMoney reserved = BigMoney.of(btc.getCounterCurrencyUnit(), 80);
        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.setAmountOfMoney(total);
        portfolioEntry.setReservedAmountOfMoney(reserved);

        OrderBookEntry orderBookEntry = new OrderBookEntry();
        portfolioEntry.setPrimaryKey(new PortfolioId().toString());
        when(userServiceFacade.obtainPortfolioForUser()).thenReturn(portfolioEntry);

        BuyOrder buyOrder = new BuyOrder();
        SellOrder sellOrder = new SellOrder();
        when(tradeServiceFacade.
                prepareSellOrder(eq(btc.getBaseCurrency()), eq(TradeController.DEFAULT_CCY_PAIR), eq(orderBookEntry), eq(portfolioEntry)))
                .thenReturn(sellOrder);
        when(tradeServiceFacade.
                prepareBuyOrder(eq(btc.getBaseCurrency()), eq(TradeController.DEFAULT_CCY_PAIR), eq(orderBookEntry), eq(portfolioEntry)))
                .thenReturn(buyOrder);
        CoinEntry coinEntry = new CoinEntry();
        when(tradeServiceFacade.loadCoin(eq(btc.getBaseCurrency()))).thenReturn(coinEntry);

        when(userServiceFacade.obtainPortfolioForUser()).thenReturn(portfolioEntry);
        when(tradeServiceFacade.loadOrderBookByCurrencyPair(eq(btc))).thenReturn(orderBookEntry);
        when(tradeServiceFacade.calculateBuyOrderEffectiveAmount(any(BuyOrder.class)))
                .thenReturn(BigMoney.of(CurrencyUnit.of(btc.getCounterCurrency()), BigDecimal.valueOf(32.988).multiply(BigDecimal.valueOf(1.3))));

        final BuyOrder buyOrderPara = new BuyOrder();
        buyOrderPara.setAmountCcy(btc.getBaseCurrency());
        buyOrderPara.setPriceCcy(btc.getCounterCurrency());
        buyOrderPara.setItemPrice(BigDecimal.valueOf(32.988));
        buyOrderPara.setTradeAmount(BigDecimal.valueOf(1.2));
        buyOrderPara.setTradingPassword("password123");

        mockMvc.perform(post("/buy/" + btc.getBaseCurrency())
                .param("itemPrice", "32.988")
                .param("tradeAmount", "1.2")
                .param("tradingPassword", "password123")
        ).andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "orderBook",
                        "sellOrder",
                        "buyOrder",
                        "coin",
                        "activeOrders",
                        "buyOrders",
                        "sellOrders",
                        "executedTrades"
                ));

        verify(tradeServiceFacade).prepareSellOrder(eq(TradeController.DEFUALT_COIN), eq(TradeController.DEFAULT_CCY_PAIR), eq(orderBookEntry), eq(portfolioEntry));
        verify(tradeServiceFacade).loadCoin(eq(TradeController.DEFUALT_COIN));
        verify(tradeServiceFacade).findOrderAggregatedPrice(eq(orderBookEntry.getPrimaryKey()),
                eq(OrderType.BUY),
                any(Date.class));
        verify(tradeServiceFacade).findOrderAggregatedPrice(eq(orderBookEntry.getPrimaryKey()),
                eq(OrderType.SELL),
                any(Date.class));
        verify(tradeServiceFacade).findExecutedTrades(eq(orderBookEntry.getPrimaryKey()));
        verify(tradeServiceFacade).findUserActiveOrders(eq(portfolioEntry.getPrimaryKey()), eq(orderBookEntry.getPrimaryKey()));
        verify(tradeServiceFacade).loadOrderBookByCurrencyPair(eq(TradeController.DEFAULT_CCY_PAIR));
        verify(tradeServiceFacade).calculateBuyOrderEffectiveAmount(any(BuyOrder.class));
    }

    @Test
    public void testSellWithFormHasErrors() throws Exception {
        final CurrencyPair btc = new CurrencyPair("BTC");


        final BuyOrder buyOrderPara = new BuyOrder();
        buyOrderPara.setAmountCcy(btc.getBaseCurrency());
        buyOrderPara.setPriceCcy(btc.getCounterCurrency());
        buyOrderPara.setItemPrice(BigDecimal.valueOf(32.988));
        buyOrderPara.setTradeAmount(BigDecimal.valueOf(1.2));

        OrderBookEntry orderBookEntry = new OrderBookEntry();
        BuyOrder buyOrder = new BuyOrder();

        CoinEntry coinEntry = new CoinEntry();
        when(tradeServiceFacade.loadCoin(eq(btc.getBaseCurrency()))).thenReturn(coinEntry);

        when(tradeServiceFacade.loadOrderBookByCurrencyPair(eq(btc))).thenReturn(orderBookEntry);
        when(tradeServiceFacade.
                prepareBuyOrder(eq(btc.getBaseCurrency()), eq(btc), eq(orderBookEntry), isNull(PortfolioEntry.class)))
                .thenReturn(buyOrder);


        mockMvc.perform(post("/sell/" + btc.getBaseCurrency())
                .param("itemPrice", "32.988")
                .param("tradeAmount", "0.0000000001")
                .param("tradingPassword", "21")
        ).andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "orderBook",
                        "sellOrder",
                        "buyOrder",
                        "coin",
                        "activeOrders",
                        "buyOrders",
                        "sellOrders",
                        "executedTrades"
                ))
                .andExpect(model().attributeErrorCount("sellOrder", 2));

        verify(userServiceFacade, never()).isWithdrawPasswordSet();

        verify(tradeServiceFacade).loadCoin(eq(btc.getBaseCurrency()));
        verify(tradeServiceFacade).loadOrderBookByCurrencyPair(eq(btc));
        verify(tradeServiceFacade).prepareBuyOrder(eq(btc.getBaseCurrency()), eq(btc), eq(orderBookEntry), isNull(PortfolioEntry.class));
        verify(tradeServiceFacade).findOrderAggregatedPrice(anyString(), eq(OrderType.BUY), any(Date.class));
        verify(tradeServiceFacade).findOrderAggregatedPrice(anyString(), eq(OrderType.SELL), any(Date.class));
        verify(tradeServiceFacade).findExecutedTrades(anyString());
        verify(tradeServiceFacade, never()).findUserActiveOrders(anyString(), anyString());
    }

    @Test
    public void testSellWithoutPortfolioFound() throws Exception {
        final CurrencyPair btc = new CurrencyPair("BTC");


        final BuyOrder buyOrderPara = new BuyOrder();
        buyOrderPara.setAmountCcy(btc.getBaseCurrency());
        buyOrderPara.setPriceCcy(btc.getCounterCurrency());
        buyOrderPara.setItemPrice(BigDecimal.valueOf(32.988));
        buyOrderPara.setTradeAmount(BigDecimal.valueOf(1.2));

        OrderBookEntry orderBookEntry = new OrderBookEntry();
        BuyOrder buyOrder = new BuyOrder();

        CoinEntry coinEntry = new CoinEntry();
        when(tradeServiceFacade.loadCoin(eq(btc.getBaseCurrency()))).thenReturn(coinEntry);

        when(tradeServiceFacade.loadOrderBookByCurrencyPair(eq(btc))).thenReturn(orderBookEntry);
        when(tradeServiceFacade.
                prepareBuyOrder(eq(btc.getBaseCurrency()), eq(btc), eq(orderBookEntry), isNull(PortfolioEntry.class)))
                .thenReturn(buyOrder);


        mockMvc.perform(post("/sell/" + btc.getBaseCurrency())
                .param("itemPrice", "32.988")
                .param("tradeAmount", "1.2")
                .param("tradingPassword", "password123")
        ).andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "orderBook",
                        "sellOrder",
                        "buyOrder",
                        "coin",
                        "activeOrders",
                        "buyOrders",
                        "sellOrders",
                        "executedTrades"
                ))
                .andExpect(model().attributeErrorCount("sellOrder", 1));

        verify(userServiceFacade, never()).isWithdrawPasswordSet();

        verify(tradeServiceFacade).loadCoin(eq(btc.getBaseCurrency()));
        verify(tradeServiceFacade).loadOrderBookByCurrencyPair(eq(btc));
        verify(tradeServiceFacade).prepareBuyOrder(eq(btc.getBaseCurrency()), eq(btc), eq(orderBookEntry), isNull(PortfolioEntry.class));
        verify(tradeServiceFacade).findOrderAggregatedPrice(anyString(), eq(OrderType.BUY), any(Date.class));
        verify(tradeServiceFacade).findOrderAggregatedPrice(anyString(), eq(OrderType.SELL), any(Date.class));
        verify(tradeServiceFacade).findExecutedTrades(anyString());
        verify(tradeServiceFacade, never()).findUserActiveOrders(anyString(), anyString());
    }

    @Test
    public void testSellWithWithdrawPasswordNotSet() throws Exception {
        final CurrencyPair btc = new CurrencyPair("BTC");

        PortfolioEntry portfolioEntry = new PortfolioEntry();

        portfolioEntry.setPrimaryKey(new PortfolioId().toString());
        when(userServiceFacade.obtainPortfolioForUser()).thenReturn(portfolioEntry);

        final BuyOrder buyOrderPara = new BuyOrder();
        buyOrderPara.setAmountCcy(btc.getBaseCurrency());
        buyOrderPara.setPriceCcy(btc.getCounterCurrency());
        buyOrderPara.setItemPrice(BigDecimal.valueOf(32.988));
        buyOrderPara.setTradeAmount(BigDecimal.valueOf(1.2));

        OrderBookEntry orderBookEntry = new OrderBookEntry();
        BuyOrder buyOrder = new BuyOrder();

        CoinEntry coinEntry = new CoinEntry();
        when(tradeServiceFacade.loadCoin(eq(btc.getBaseCurrency()))).thenReturn(coinEntry);

        when(tradeServiceFacade.loadOrderBookByCurrencyPair(eq(btc))).thenReturn(orderBookEntry);

        when(tradeServiceFacade.
                prepareBuyOrder(eq(btc.getBaseCurrency()), eq(btc), eq(orderBookEntry), eq(portfolioEntry)))
                .thenReturn(buyOrder);


        mockMvc.perform(post("/sell/" + btc.getBaseCurrency())
                .param("itemPrice", "32.988")
                .param("tradeAmount", "1.2")
                .param("tradingPassword", "password123")
        ).andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "orderBook",
                        "sellOrder",
                        "buyOrder",
                        "coin",
                        "activeOrders",
                        "buyOrders",
                        "sellOrders",
                        "executedTrades"
                ))
                .andExpect(model().attributeErrorCount("sellOrder", 1));

        verify(userServiceFacade).isWithdrawPasswordSet();

        verify(tradeServiceFacade).loadCoin(eq(btc.getBaseCurrency()));
        verify(tradeServiceFacade).loadOrderBookByCurrencyPair(eq(btc));
        verify(tradeServiceFacade).prepareBuyOrder(eq(btc.getBaseCurrency()), eq(btc), eq(orderBookEntry), eq(portfolioEntry));
        verify(tradeServiceFacade).findOrderAggregatedPrice(anyString(), eq(OrderType.BUY), any(Date.class));
        verify(tradeServiceFacade).findOrderAggregatedPrice(anyString(), eq(OrderType.SELL), any(Date.class));
        verify(tradeServiceFacade).findExecutedTrades(anyString());
        verify(tradeServiceFacade).findUserActiveOrders(anyString(), anyString());
    }

    @Test
    public void testSellWithWithdrawPasswordNotMatched() throws Exception {
        final CurrencyPair btc = new CurrencyPair("BTC");

        PortfolioEntry portfolioEntry = new PortfolioEntry();

        portfolioEntry.setPrimaryKey(new PortfolioId().toString());
        when(userServiceFacade.obtainPortfolioForUser()).thenReturn(portfolioEntry);
        when(userServiceFacade.isWithdrawPasswordSet()).thenReturn(true);

        final BuyOrder buyOrderPara = new BuyOrder();
        buyOrderPara.setAmountCcy(btc.getBaseCurrency());
        buyOrderPara.setPriceCcy(btc.getCounterCurrency());
        buyOrderPara.setItemPrice(BigDecimal.valueOf(32.988));
        buyOrderPara.setTradeAmount(BigDecimal.valueOf(1.2));

        OrderBookEntry orderBookEntry = new OrderBookEntry();
        BuyOrder buyOrder = new BuyOrder();

        CoinEntry coinEntry = new CoinEntry();
        when(tradeServiceFacade.loadCoin(eq(btc.getBaseCurrency()))).thenReturn(coinEntry);

        when(tradeServiceFacade.loadOrderBookByCurrencyPair(eq(btc))).thenReturn(orderBookEntry);
        when(tradeServiceFacade.calculateSellOrderEffectiveAmount(any(SellOrder.class)))
                .thenReturn(BigMoney.of(Constants.CURRENCY_UNIT_BTC, 1.26));

        when(tradeServiceFacade.
                prepareBuyOrder(eq(btc.getBaseCurrency()), eq(btc), eq(orderBookEntry), eq(portfolioEntry)))
                .thenReturn(buyOrder);


        mockMvc.perform(post("/sell/" + btc.getBaseCurrency())
                .param("itemPrice", "32.988")
                .param("tradeAmount", "1.2")
                .param("tradingPassword", "password123")
        ).andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "orderBook",
                        "sellOrder",
                        "buyOrder",
                        "coin",
                        "activeOrders",
                        "buyOrders",
                        "sellOrders",
                        "executedTrades"
                ))
                .andExpect(model().attributeErrorCount("sellOrder", 1));

        verify(userServiceFacade).isWithdrawPasswordSet();
        verify(userServiceFacade).isWithdrawPasswordMatched(eq("password123"));

        verify(tradeServiceFacade).loadCoin(eq(btc.getBaseCurrency()));
        verify(tradeServiceFacade).loadOrderBookByCurrencyPair(eq(btc));
        verify(tradeServiceFacade).prepareBuyOrder(eq(btc.getBaseCurrency()), eq(btc), eq(orderBookEntry), eq(portfolioEntry));
        verify(tradeServiceFacade).findOrderAggregatedPrice(anyString(), eq(OrderType.BUY), any(Date.class));
        verify(tradeServiceFacade).findOrderAggregatedPrice(anyString(), eq(OrderType.SELL), any(Date.class));
        verify(tradeServiceFacade).findExecutedTrades(anyString());
        verify(tradeServiceFacade).findUserActiveOrders(anyString(), anyString());
    }

    @Test
    public void testSellWithMoneyNotEnough() throws Exception {
        final CurrencyPair btc = new CurrencyPair("BTC");

        PortfolioEntry portfolioEntry = new PortfolioEntry();

        portfolioEntry.setPrimaryKey(new PortfolioId().toString());
        when(userServiceFacade.obtainPortfolioForUser()).thenReturn(portfolioEntry);
        when(userServiceFacade.isWithdrawPasswordSet()).thenReturn(true);

        final BuyOrder buyOrderPara = new BuyOrder();
        buyOrderPara.setAmountCcy(btc.getBaseCurrency());
        buyOrderPara.setPriceCcy(btc.getCounterCurrency());
        buyOrderPara.setItemPrice(BigDecimal.valueOf(32.988));
        buyOrderPara.setTradeAmount(BigDecimal.valueOf(1.2));

        OrderBookEntry orderBookEntry = new OrderBookEntry();
        BuyOrder buyOrder = new BuyOrder();

        CoinEntry coinEntry = new CoinEntry();
        when(tradeServiceFacade.loadCoin(eq(btc.getBaseCurrency()))).thenReturn(coinEntry);
        when(userServiceFacade.isWithdrawPasswordMatched(eq("password123"))).thenReturn(true);

        when(tradeServiceFacade.loadOrderBookByCurrencyPair(eq(btc))).thenReturn(orderBookEntry);
        when(tradeServiceFacade.calculateSellOrderEffectiveAmount(any(SellOrder.class)))
                .thenReturn(BigMoney.of(Constants.CURRENCY_UNIT_BTC, 1.26));

        when(tradeServiceFacade.
                prepareBuyOrder(eq(btc.getBaseCurrency()), eq(btc), eq(orderBookEntry), eq(portfolioEntry)))
                .thenReturn(buyOrder);


        mockMvc.perform(post("/sell/" + btc.getBaseCurrency())
                .param("itemPrice", "32.988")
                .param("tradeAmount", "1.2")
                .param("tradingPassword", "password123")
        ).andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "orderBook",
                        "sellOrder",
                        "buyOrder",
                        "coin",
                        "activeOrders",
                        "buyOrders",
                        "sellOrders",
                        "executedTrades"
                ))
                .andExpect(model().attributeHasFieldErrors("sellOrder", "tradeAmount"));

        verify(userServiceFacade).isWithdrawPasswordSet();
        verify(userServiceFacade).isWithdrawPasswordMatched(eq("password123"));
        verify(tradeServiceFacade).calculateSellOrderEffectiveAmount(any(SellOrder.class));

        verify(tradeServiceFacade).loadCoin(eq(btc.getBaseCurrency()));
        verify(tradeServiceFacade).loadOrderBookByCurrencyPair(eq(btc));
        verify(tradeServiceFacade).prepareBuyOrder(eq(btc.getBaseCurrency()), eq(btc), eq(orderBookEntry), eq(portfolioEntry));
        verify(tradeServiceFacade).findOrderAggregatedPrice(anyString(), eq(OrderType.BUY), any(Date.class));
        verify(tradeServiceFacade).findOrderAggregatedPrice(anyString(), eq(OrderType.SELL), any(Date.class));
        verify(tradeServiceFacade).findExecutedTrades(anyString());
        verify(tradeServiceFacade).findUserActiveOrders(anyString(), anyString());
    }

    @Test
    public void testSell() throws Exception {
        final CurrencyPair btc = new CurrencyPair("BTC");
        final BigMoney total = BigMoney.of(btc.getCounterCurrencyUnit(), 100);
        final BigMoney reserved = BigMoney.of(btc.getCounterCurrencyUnit(), 80);
        final BigMoney available = BigMoney.of(btc.getBaseCurrencyUnit(), 10);
        OrderBookEntry orderBookEntry = new OrderBookEntry();
        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.setAmountOfMoney(total);
        portfolioEntry.setReservedAmountOfMoney(reserved);
        portfolioEntry.createItem("BTC", "Bitcoin");
        portfolioEntry.addItemInPossession("BTC", available);

        portfolioEntry.setPrimaryKey(new PortfolioId().toString());
        when(userServiceFacade.obtainPortfolioForUser()).thenReturn(portfolioEntry);
        when(userServiceFacade.isWithdrawPasswordSet()).thenReturn(true);
        when(userServiceFacade.isWithdrawPasswordMatched(eq("password123"))).thenReturn(true);

        BuyOrder buyOrder = new BuyOrder();
        SellOrder sellOrder = new SellOrder();
        when(tradeServiceFacade.
                prepareSellOrder(eq(btc.getBaseCurrency()), eq(TradeController.DEFAULT_CCY_PAIR), eq(orderBookEntry), eq(portfolioEntry)))
                .thenReturn(sellOrder);
        when(tradeServiceFacade.
                prepareBuyOrder(eq(btc.getBaseCurrency()), eq(TradeController.DEFAULT_CCY_PAIR), eq(orderBookEntry), eq(portfolioEntry)))
                .thenReturn(buyOrder);
        CoinEntry coinEntry = new CoinEntry();
        when(tradeServiceFacade.loadCoin(eq(btc.getBaseCurrency()))).thenReturn(coinEntry);

        when(userServiceFacade.obtainPortfolioForUser()).thenReturn(portfolioEntry);
        when(tradeServiceFacade.loadOrderBookByCurrencyPair(eq(btc))).thenReturn(orderBookEntry);
        when(tradeServiceFacade.calculateSellOrderEffectiveAmount(any(SellOrder.class)))
                .thenReturn(BigMoney.of(CurrencyUnit.of(btc.getBaseCurrency()), BigDecimal.valueOf(1.3)));


        final BuyOrder buyOrderPara = new BuyOrder();
        buyOrderPara.setAmountCcy(btc.getBaseCurrency());
        buyOrderPara.setPriceCcy(btc.getCounterCurrency());
        buyOrderPara.setItemPrice(BigDecimal.valueOf(32.988));
        buyOrderPara.setTradeAmount(BigDecimal.valueOf(1.2));

        mockMvc.perform(post("/sell/" + btc.getBaseCurrency())
                .param("itemPrice", "32.988")
                .param("tradeAmount", "1.2")
                .param("tradingPassword", "password123")
        ).andExpect(status().isFound());

        verify(tradeServiceFacade)
                .sellOrder(any(TransactionId.class), eq("BTC"),
                        eq(btc), anyString(), anyString(),
                        eq(BigMoney.of(btc.getBaseCurrencyUnit(), 1.2).toMoney().toBigMoney()), eq(BigMoney.of(btc.getCounterCurrencyUnit(), 32.988)));

        verify(userServiceFacade).isWithdrawPasswordMatched(eq("password123"));
    }
}