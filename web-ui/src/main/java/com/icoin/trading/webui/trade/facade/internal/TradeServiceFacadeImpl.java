package com.icoin.trading.webui.trade.facade.internal;

import com.icoin.trading.tradeengine.application.command.transaction.command.StartBuyTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartSellTransactionCommand;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.order.OrderType;
import com.icoin.trading.tradeengine.query.order.PriceAggregate;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.order.repositories.OrderQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeExecutedEntry;
import com.icoin.trading.tradeengine.query.tradeexecuted.repositories.TradeExecutedQueryRepository;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import com.icoin.trading.webui.order.AbstractOrder;
import com.icoin.trading.webui.order.BuyOrder;
import com.icoin.trading.webui.order.SellOrder;
import com.icoin.trading.webui.security.UserServiceFacade;
import com.icoin.trading.webui.trade.facade.TradeServiceFacade;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/17/13
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class TradeServiceFacadeImpl implements TradeServiceFacade {
    private static Logger logger = LoggerFactory.getLogger(TradeServiceFacadeImpl.class);

    private CoinQueryRepository coinRepository;
    private OrderBookQueryRepository orderBookRepository;
    private UserQueryRepository userRepository;
    private TradeExecutedQueryRepository tradeExecutedRepository;
    private PortfolioQueryRepository portfolioQueryRepository;
    private CommandGateway commandGateway;
    private OrderQueryRepository orderQueryRepository;


    private UserServiceFacade userServiceFacade;

    /**
     * At the moment we handle the first orderBook found for a coin.
     *
     * @param currencyPair Currency Pair to obtain the orderBook for
     * @return Found OrderBook for the coin belonging to the provided identifier
     */
    public OrderBookEntry loadOrderBookByCurrencyPair(CurrencyPair currencyPair) {
        OrderBookEntry byCoinIdentifier = orderBookRepository.findByCurrencyPair(currencyPair);
        if (logger.isDebugEnabled()) {
            logger.debug("Find by currencyPair {} : {}", currencyPair, byCoinIdentifier);
        }

        return byCoinIdentifier;
    }


    public CoinEntry loadCoin(String coinId) {
        return coinRepository.findOne(coinId);
    }

    @Override
    public BuyOrder prepareBuyOrder(String coinId, CurrencyPair currencyPair, OrderBookEntry orderBookEntry) {
        BuyOrder order = new BuyOrder();
        initCoinInfo(coinId, order);


        if (orderBookEntry == null) {
            orderBookEntry = loadOrderBookByCurrencyPair(currencyPair);
        }

        if (orderBookEntry != null) {
            return order;
        }
        //init suggested buy amount
        BigDecimal amount = BigDecimal.ZERO;
        if (orderBookEntry.getLowestSellPrice() != null) {
            amount = orderBookEntry.getLowestSellPrice().getAmount();
        }
        order.setSuggestedPrice(amount);


        //init the portfolio if possible
        final PortfolioEntry portfolioEntry = userServiceFacade.obtainPortfolioForUser();
        if (portfolioEntry != null) {
            order.setBalance(portfolioEntry
                    .obtainAmountOfAvailableItemsFor(coinId, orderBookEntry.getBaseCurrency())
                    .getAmount());
        }

        return order;
    }

    @Override
    public SellOrder prepareSellOrder(String coinId, CurrencyPair currencyPair, OrderBookEntry orderBookEntry) {
        SellOrder order = new SellOrder();

        initCoinInfo(coinId, order);
        BigDecimal amount = BigDecimal.ZERO;

        if (orderBookEntry == null) {
            orderBookEntry = loadOrderBookByCurrencyPair(currencyPair);
        }

        if (orderBookEntry != null) {
            return order;
        }

        if (orderBookEntry.getHighestBuyPrice() != null) {
            amount = orderBookEntry.getHighestBuyPrice().getAmount();
        }
        order.setSuggestedPrice(amount);

        final PortfolioEntry portfolioEntry = userServiceFacade.obtainPortfolioForUser();
        if (portfolioEntry != null) {
            order.setBalance(portfolioEntry.getAmountOfMoney().getAmount());
        }

        return order;
    }

    private void initCoinInfo(String coinId, AbstractOrder order) {
        CoinEntry coin = loadCoin(coinId);
        order.setCoinId(coinId);
        order.setCoinName(coin.getName());
    }

    @Override
    public List<TradeExecutedEntry> findByOrderBookIdentifier(String orderBookIdentifier) {
        if (orderBookIdentifier == null) {
            return Collections.emptyList();
        }
        return tradeExecutedRepository.findByOrderBookIdentifier(orderBookIdentifier);
    }

    @Override
    public List<OrderEntry> findUserActiveOrders(String userId, String orderBookId) {
        if (userId == null || orderBookId == null) {
            return Collections.emptyList();
        }
        return orderQueryRepository.findUserActiveOrders(userId, orderBookId);
    }

    @Override
    public List<PriceAggregate> findOrderAggregatedPrice(String orderBookIdentifier, OrderType type, Date toDate) {
        if (orderBookIdentifier == null || type == null || toDate == null) {
            return Collections.emptyList();
        }
        return orderQueryRepository.findOrderAggregatedPrice(orderBookIdentifier, type, toDate);
    }

    @Override
    public List<OrderEntry> findOrderForOrderBook(String orderBookIdentifier, OrderType type, OrderStatus orderStatus) {
        if (orderBookIdentifier == null || type == null || orderStatus == null) {
            return Collections.emptyList();
        }
        return orderQueryRepository.findByOrderBookIdentifierAndTypeAndOrderStatus(orderBookIdentifier, type, orderStatus);
    }

    @Override
    public void sellOrder(String orderBookId, String portfolioId, BigMoney tradeAmount, BigMoney price) {
        StartSellTransactionCommand command = new StartSellTransactionCommand(new TransactionId(),
                new OrderBookId(orderBookId),
                new PortfolioId(portfolioId),
                tradeAmount,
                price);

        commandGateway.send(command);
    }

    @Override
    public void buyOrder(String orderBookId, String portfolioId, BigMoney tradeAmount, BigMoney price) {
        StartBuyTransactionCommand command = new StartBuyTransactionCommand(new TransactionId(),
                new OrderBookId(orderBookId),
                new PortfolioId(portfolioId),
                tradeAmount,
                price);

        commandGateway.send(command);
    }

    @Autowired
    public void setUserServiceFacade(UserServiceFacade userServiceFacade) {
        this.userServiceFacade = userServiceFacade;
    }

    @Autowired
    public void setCoinRepository(CoinQueryRepository coinRepository) {
        this.coinRepository = coinRepository;
    }

    @Autowired
    public void setOrderBookRepository(OrderBookQueryRepository orderBookRepository) {
        this.orderBookRepository = orderBookRepository;
    }

    @Autowired
    public void setUserRepository(UserQueryRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setTradeExecutedRepository(TradeExecutedQueryRepository tradeExecutedRepository) {
        this.tradeExecutedRepository = tradeExecutedRepository;
    }

    @Autowired
    public void setPortfolioQueryRepository(PortfolioQueryRepository portfolioQueryRepository) {
        this.portfolioQueryRepository = portfolioQueryRepository;
    }

    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Autowired
    public void setOrderQueryRepository(OrderQueryRepository orderQueryRepository) {
        this.orderQueryRepository = orderQueryRepository;
    }

}