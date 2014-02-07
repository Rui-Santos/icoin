package com.icoin.trading.webui.trade.facade.internal;


import com.icoin.trading.tradeengine.application.command.order.RefreshOrderBookPriceCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartBuyTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartSellTransactionCommand;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.commission.Commission;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicy;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicyFactory;
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
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeExecutedEntry;
import com.icoin.trading.tradeengine.query.tradeexecuted.repositories.TradeExecutedQueryRepository;
import com.icoin.trading.webui.order.AbstractOrder;
import com.icoin.trading.webui.order.BuyOrder;
import com.icoin.trading.webui.order.SellOrder;
import com.icoin.trading.webui.user.UserServiceFacade;
import com.icoin.trading.webui.trade.facade.TradeServiceFacade;
import com.icoin.trading.webui.trade.facade.internal.assembler.BuyOrderAssembler;
import com.icoin.trading.webui.trade.facade.internal.assembler.SellOrderAssembler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

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
    private TradeExecutedQueryRepository tradeExecutedRepository;
    private CommandGateway commandGateway;
    private OrderQueryRepository orderQueryRepository;
    private UserServiceFacade userServiceFacade;
    private CommissionPolicyFactory commissionPolicyFactory;

    /**
     * At the moment we handle the first orderBook found for a coin.
     *
     * @param currencyPair Currency Pair to obtain the orderBook for
     * @return Found OrderBook for the coin belonging to the provided identifier
     */
    @Override
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
    public BigMoney calculateSellOrderEffectiveAmount(SellOrder order) {
        notNull(order);
        notNull(order.getTradeAmount());
        SellOrderAssembler assembler = new SellOrderAssembler();

        com.icoin.trading.tradeengine.domain.model.order.SellOrder sellOrder = assembler.toDomain(order);
        CommissionPolicy commissionPolicy = commissionPolicyFactory.createCommissionPolicy(sellOrder);
        Commission commission = commissionPolicy.calculateSellCommission(sellOrder);

        final Money money = commission.getCommission();

        return money.plus(order.getTradeAmount(), RoundingMode.HALF_EVEN).toBigMoney();
    }

    @Override
    public BigMoney calculateBuyOrderEffectiveAmount(BuyOrder order) {
        notNull(order);
        notNull(order.getItemPrice());
        notNull(order.getTradeAmount());
        notNull(order.getAmountCcy());
        notNull(order.getPriceCcy());

        BuyOrderAssembler assembler = new BuyOrderAssembler();

        com.icoin.trading.tradeengine.domain.model.order.BuyOrder buyOrder = assembler.toDomain(order);
        CommissionPolicy commissionPolicy = commissionPolicyFactory.createCommissionPolicy(buyOrder);
        Commission commission = commissionPolicy.calculateBuyCommission(buyOrder);

        //todo test precision
        final Money money = commission.getCommission();
        final Money reservedMoney = Money.of(CurrencyUnit.of(order.getPriceCcy()), order.getItemPrice().multiply(order.getTradeAmount()), RoundingMode.HALF_EVEN);
        return money.plus(reservedMoney).toBigMoney();
    }

    @Override
    public BuyOrder prepareBuyOrder(String coinId,
                                    CurrencyPair currencyPair,
                                    OrderBookEntry orderBookEntry,
                                    PortfolioEntry portfolioEntry) {
        hasLength(coinId);
        notNull(currencyPair);
        isTrue(coinId.equalsIgnoreCase(currencyPair.getBaseCurrency()));

        BuyOrder order = new BuyOrder();
        initCoinInfo(coinId, currencyPair, order);


        if (orderBookEntry == null) {
            orderBookEntry = loadOrderBookByCurrencyPair(currencyPair);
        }

        if (orderBookEntry == null) {
            return order;
        }
        //init suggested buy amount
        BigDecimal amount = BigDecimal.ZERO;
        if (orderBookEntry.getLowestSellPrice() != null) {
            amount = orderBookEntry.getLowestSellPrice().getAmount();
        }
        order.setSuggestedPrice(amount);


        //init the portfolio if possible
        if (portfolioEntry == null) {
            portfolioEntry = userServiceFacade.obtainPortfolioForUser();
        }
        if (portfolioEntry != null) {
            order.setBalance(portfolioEntry.obtainMoneyToSpend().getAmount());
        }

        return order;
    }

    @Override
    public SellOrder prepareSellOrder(String coinId,
                                      CurrencyPair currencyPair,
                                      OrderBookEntry orderBookEntry,
                                      PortfolioEntry portfolioEntry) {
        SellOrder order = new SellOrder();

        initCoinInfo(coinId, currencyPair, order);
        BigDecimal amount = BigDecimal.ZERO;

        if (orderBookEntry == null) {
            orderBookEntry = loadOrderBookByCurrencyPair(currencyPair);
        }

        if (orderBookEntry == null) {
            return order;
        }

        if (orderBookEntry.getHighestBuyPrice() != null) {
            amount = orderBookEntry.getHighestBuyPrice().getAmount();
        }
        order.setSuggestedPrice(amount);

        if (portfolioEntry == null) {
            portfolioEntry = userServiceFacade.obtainPortfolioForUser();
        }
        if (portfolioEntry != null) {
            order.setBalance(portfolioEntry
                    .obtainAmountOfAvailableItemFor(coinId, orderBookEntry.getBaseCurrency())
                    .getAmount());
        }

        return order;
    }

    private void initCoinInfo(String coinId, CurrencyPair currencyPair, AbstractOrder order) {
        CoinEntry coin = loadCoin(coinId);
        order.setCoinId(coinId);
        order.setCoinName(coin.getName());
        order.setPriceCcy(currencyPair.getCounterCurrency());
        order.setAmountCcy(currencyPair.getBaseCurrency());
    }

    @Override
    public List<TradeExecutedEntry> findExecutedTradesByOrderBookIdentifier(String orderBookIdentifier) {
        if (orderBookIdentifier == null) {
            return Collections.emptyList();
        }

        PageRequest pageRequest = new PageRequest(0, 20, Sort.Direction.DESC, "tradeTime");
        return tradeExecutedRepository.findByOrderBookIdentifier(orderBookIdentifier, pageRequest);
    }

    @Override
    public List<OrderEntry> findUserActiveOrders(String userId, String orderBookId) {
        if (userId == null || orderBookId == null) {
            return Collections.emptyList();
        }
        return orderQueryRepository.findUserActiveOrders(userId, orderBookId);
    }

    @Override
    public List<PriceAggregate> findOrderAggregatedPrice(String orderBookIdentifier,
                                                         OrderType type,
                                                         Date toDate) {
        if (orderBookIdentifier == null || type == null || toDate == null) {
            return Collections.emptyList();
        }
        return orderQueryRepository.findOrderAggregatedPrice(orderBookIdentifier, type, toDate, 10);
    }

    @Override
    public List<OrderEntry> findOrderForOrderBook(String orderBookIdentifier,
                                                  OrderType type,
                                                  OrderStatus orderStatus) {
        if (orderBookIdentifier == null || type == null || orderStatus == null) {
            return Collections.emptyList();
        }
        return orderQueryRepository.findByOrderBookIdentifierAndTypeAndOrderStatus(orderBookIdentifier, type, orderStatus);
    }

    @Override
    public void sellOrder(final TransactionId transactionId,
                          String coinId,
                          CurrencyPair currencyPair,
                          String orderBookId,
                          String portfolioId,
                          BigMoney tradeAmount,
                          BigMoney price) {
        StartSellTransactionCommand command =
                new StartSellTransactionCommand(
                        transactionId,
                        new CoinId(coinId),
                        currencyPair,
                        new OrderBookId(orderBookId),
                        new PortfolioId(portfolioId),
                        tradeAmount,
                        price);

        commandGateway.send(command);
    }

    @Override
    public void buyOrder(final TransactionId transactionId,
                         String coinId,
                         CurrencyPair currencyPair,
                         String orderBookId,
                         String portfolioId,
                         BigMoney tradeAmount,
                         BigMoney price) {
        StartBuyTransactionCommand command =
                new StartBuyTransactionCommand(
                        transactionId,
                        new CoinId(coinId),
                        currencyPair,
                        new OrderBookId(orderBookId),
                        new PortfolioId(portfolioId),
                        tradeAmount,
                        price);

        commandGateway.send(command);
    }

    @Override
    public void refreshOrderBookPrice() {
        final Iterable<OrderBookEntry> bookEntries = orderBookRepository.findAll();

        for (OrderBookEntry bookEntry : bookEntries) {
            RefreshOrderBookPriceCommand command =
                    new RefreshOrderBookPriceCommand(new OrderBookId(bookEntry.getPrimaryKey()));
            commandGateway.send(command);
        }
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
    public void setTradeExecutedRepository(TradeExecutedQueryRepository tradeExecutedRepository) {
        this.tradeExecutedRepository = tradeExecutedRepository;
    }

    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Autowired
    public void setOrderQueryRepository(OrderQueryRepository orderQueryRepository) {
        this.orderQueryRepository = orderQueryRepository;
    }

    @Autowired
    public void setCommissionPolicyFactory(CommissionPolicyFactory commissionPolicyFactory) {
        this.commissionPolicyFactory = commissionPolicyFactory;
    }
}