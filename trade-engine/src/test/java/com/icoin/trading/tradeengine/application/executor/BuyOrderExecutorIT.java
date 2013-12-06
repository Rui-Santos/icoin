package com.icoin.trading.tradeengine.application.executor;

import com.icoin.trading.tradeengine.application.command.order.OrderBookCommandHandler;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.SellOrderRepository;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-6
 * Time: AM9:09
 * To change this template use File | Settings | File Templates.
 */
public class BuyOrderExecutorIT {

    private FixtureConfiguration fixture;
    private SellOrderRepository sellOrderRepository= mock(SellOrderRepository.class);
    private BuyOrderRepository buyOrderRepository = mock(BuyOrderRepository.class);
    private BuyOrderExecutor buyOrderExecutor = mock(BuyOrderExecutor.class);

    @Before
    public void setUp() {
        fixture = Fixtures.newGivenWhenThenFixture(OrderBook.class);
        buyOrderExecutor.setOrderBookRepository(fixture.getRepository());

        buyOrderExecutor.setBuyOrderRepository(buyOrderRepository);
        buyOrderExecutor.setSellOrderRepository(sellOrderRepository);
    }

    @Test
    public void testMassiveSellerTradeExecution() throws Exception {

    }

    @Test
    public void testHandleTradeExecuted() throws Exception {

    }
}
