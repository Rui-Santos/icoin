package com.icoin.trading.webui.trade

import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.webui.order.BuyOrder;
import com.icoin.trading.webui.order.SellOrder;
import com.icoin.trading.webui.security.UserServiceFacade;
import com.icoin.trading.webui.trade.facade.TradeServiceFacade;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private final OrderBookEntry orderBookEntry = new OrderBookEntry();
    private PortfolioEntry portfolioEntry = new PortfolioEntry();

    private TradeServiceFacade tradeServiceFacade = mock(TradeServiceFacade.class);
    private UserServiceFacade userServiceFacade = mock(UserServiceFacade.class);

    //    @Autowired
    private org.springframework.web.context.WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
//        mockMvc = standaloneSetup(new PersonController()).addFilters(new CharacterEncodingFilter()).build();
        this.mockMvc = standaloneSetup(tradeController)
                .defaultRequest(get("/")
//                        .contextPath("/app").servletPath("/main")
//                        .accept(MediaType.APPLICATION_JSON)
        ).build();

        tradeController.setTradeServiceFacade(tradeServiceFacade);
        tradeController.setUserServiceFacade(userServiceFacade);

        when(tradeServiceFacade.loadOrderBookByCurrencyPair(eq(TradeController.DEFAULT_CCY_PAIR))).thenReturn(orderBookEntry);
    }

    @Test
    public void testIndex() throws Exception {
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


    }

    @Test
    public void testBuy() throws Exception {

    }
}