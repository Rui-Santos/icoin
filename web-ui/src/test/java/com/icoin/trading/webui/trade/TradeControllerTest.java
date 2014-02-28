package com.icoin.trading.webui.trade;

import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
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

import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
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
//        mockMvc = standaloneSetup(new PersonController()).addFilters(new CharacterEncodingFilter()).build();
        this.mockMvc = standaloneSetup(tradeController)
                .defaultRequest(get("/index")
//                        .contextPath("/app").servletPath("/main")
//                        .accept(MediaType.APPLICATION_JSON)
                ).build();

        tradeController.setTradeServiceFacade(tradeServiceFacade);
        tradeController.setUserServiceFacade(userServiceFacade);
    }

    @Ignore
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


        mockMvc.perform(get("/")).andExpect(status().isOk())
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
    public void testSell() throws Exception {
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

        verify(tradeServiceFacade).prepareBuyOrder(eq(TradeController.DEFUALT_COIN), eq(TradeController.DEFAULT_CCY_PAIR), eq(orderBookEntry), eq(portfolioEntry));
        verify(tradeServiceFacade).loadCoin(eq(TradeController.DEFUALT_COIN));
        verify(tradeServiceFacade).findOrderAggregatedPrice(eq(orderBookEntry.getPrimaryKey()),
                eq(OrderType.BUY),
                any(Date.class));
        verify(tradeServiceFacade).findOrderAggregatedPrice(eq(orderBookEntry.getPrimaryKey()),
                eq(OrderType.SELL),
                any(Date.class));
        verify(tradeServiceFacade).findExecutedTrades(eq(orderBookEntry.getPrimaryKey()));
        verify(tradeServiceFacade).findUserActiveOrders(eq(portfolioEntry.getPrimaryKey()),eq(orderBookEntry.getPrimaryKey()));
        verify(tradeServiceFacade).loadOrderBookByCurrencyPair(eq(TradeController.DEFAULT_CCY_PAIR));
        verify(tradeServiceFacade).calculateSellOrderEffectiveAmount(any(SellOrder.class));
    }
}