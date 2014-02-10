package com.icoin.trading.tradeengine;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.icoin.trading.tradeengine.application.command.admin.EnsureCqrsIndexesCommand;
import com.icoin.trading.tradeengine.application.command.admin.ReinitializeOrderBookTradingExecutorsCommand;
import com.icoin.trading.tradeengine.application.command.admin.ReinstallDataBaseCommand;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.order.OrderType;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.order.repositories.OrderQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeExecutedEntry;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeType;
import com.icoin.trading.tradeengine.query.tradeexecuted.repositories.TradeExecutedQueryRepository;
import com.icoin.trading.tradeengine.query.transaction.TransactionEntry;
import com.icoin.trading.tradeengine.query.transaction.TransactionState;
import com.icoin.trading.tradeengine.query.transaction.repositories.TransactionQueryRepository;
import com.icoin.trading.users.domain.model.user.UserId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.icoin.trading.tradeengine.TradingTests.addItems;
import static com.icoin.trading.tradeengine.TradingTests.addMoney;
import static com.icoin.trading.tradeengine.TradingTests.createBTCCoin;
import static com.icoin.trading.tradeengine.TradingTests.createUser;
import static com.icoin.trading.tradeengine.TradingTests.obtainOrderBookByCoinName;
import static com.icoin.trading.tradeengine.TradingTests.placeBuyOrder;
import static com.icoin.trading.tradeengine.TradingTests.placeSellOrder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/27/13
 * Time: 1:12 PM
 * To change this template use File | Settings | File Templates.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration({"classpath:com/icoin/trading/tradeengine/infrastructure/persistence/mongo/tradeengine-persistence-mongo.xml",
        "classpath:META-INF/spring/cqrs-infrastructure-context.xml",
        "classpath:META-INF/spring/order-context.xml",
        "classpath:META-INF/spring/trade-engine-context.xml",
        "classpath:META-INF/spring/users-context.xml",
        "classpath:META-INF/spring/users-persistence-mongo.xml",
        "classpath:META-INF/spring/coin-context.xml",
        "classpath:META-INF/spring/configuration-context.xml",
        "classpath:META-INF/spring/external-context.xml"
})
public class TradingIT {

    private final CoinId coinId = new CoinId(Currencies.BTC);
    private final String coinName = "Bitcoin";
    private final CurrencyPair currencyPair = new CurrencyPair(Currencies.BTC);
    private UserId userId1;
    private UserId userId2;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CommandGateway commandGateway;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CoinQueryRepository coinRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private PortfolioQueryRepository portfolioRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private OrderBookQueryRepository orderBookRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private OrderQueryRepository orderQueryRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private TradeExecutedQueryRepository tradeExecutedQueryRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private TransactionQueryRepository transactionQueryRepository;

    /**
     * *************************initial data*****************************************
     * user1
     * money: 100,000
     * btc: 0
     * user2
     * money: 0
     * btc: 5.23
     * ********************************************************************************
     */
    @Before
    public void setUp() throws Exception {
        ReinstallDataBaseCommand reinstallDataBaseCommand = new ReinstallDataBaseCommand();
        commandGateway.send(reinstallDataBaseCommand);

        createBTCCoin(coinId, coinName, commandGateway);

        userId1 = createUser("User1st", "User", "One", commandGateway);
        userId2 = createUser("User2nd", "User", "Two", commandGateway);

        EnsureCqrsIndexesCommand ensureCqrsIndexesCommand = new EnsureCqrsIndexesCommand();
        commandGateway.send(ensureCqrsIndexesCommand);

        commandGateway.send(new ReinitializeOrderBookTradingExecutorsCommand());
    }

    @Test(timeout = 30000)
    public void testExecuteOneToOneOrder() throws Exception {
        final BigDecimal money = BigDecimal.valueOf(100000);
        final BigDecimal btcAmount = BigDecimal.valueOf(5.233412);
        final BigMoney price = BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(12.456));
        final BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of(Currencies.BTC), btcAmount);

        addMoney(userId1, money, commandGateway, portfolioRepository);
        addItems(userId2, "BTC", btcAmount.add(BigDecimal.ONE), commandGateway, portfolioRepository);

        OrderBookEntry orderBookEntry = obtainOrderBookByCoinName(coinId.toString(), coinRepository, orderBookRepository);

        //user1 place buy order 
        PortfolioEntry buyPortfolio = portfolioRepository.findByUserIdentifier(userId1.toString());
        TransactionId buyTransactionId = new TransactionId();
        placeBuyOrder(coinId, currencyPair, orderBookEntry, buyPortfolio, tradeAmount, price, buyTransactionId, commandGateway);

        //user2 place a sell order 
        PortfolioEntry sellPortfolio = portfolioRepository.findByUserIdentifier(userId2.toString());
        TransactionId sellTransactionId = new TransactionId();
        placeSellOrder(coinId, currencyPair, orderBookEntry, sellPortfolio, tradeAmount.plus(0.001), price, sellTransactionId, commandGateway);


        //sleep to wait for the execution complete 
        waitForComplete(buyPortfolio);
        /****************************data***************************************** 
         user1                             after trading           user1 
         money: 100,000                                            money: 99934.487 
         btc: 0                                                    btc: 5.133412 
         price: 12.456 
         amount: 5.133412 
         commission: 0.5%                                          commission: 65.187 * 0.5% = 0.326
         total: 65.187379872                                       total: 65.513 

         user2                             after trading           user2 
         money: 0                                                  money: 65.187 
         btc: 5.23                                                 btc: 0.07092094 
         price: 12.456 
         amount: 5.133412 
         commission: 0.5%                                          commission: 5.133412 * 0.5% = 0.02566706
         total: 5.133412                                           total: 5.15907906 
         **********************************************************************************/

        //verify data 

        //buyPortfolio 
        buyPortfolio = portfolioRepository.findByUserIdentifier(userId1.toString());
        BigMoney buyerLeftAmount = buyPortfolio.getAmountOfMoney();
        BigMoney buyerLeftItem = buyPortfolio.obtainAmountOfAvailableItemFor(coinId.toString(), CurrencyUnit.of(coinId.toString()));
        assertThat(buyerLeftAmount.compareTo(Money.of(CurrencyUnit.of(Currencies.CNY), 99934.487)), is(0));
        assertThat(buyerLeftItem.compareTo(Money.of(CurrencyUnit.of(Currencies.BTC), 5.233412)), is(0));

        //sellPortfolio 
        sellPortfolio = portfolioRepository.findByUserIdentifier(userId2.toString());
        BigMoney sellerLeftAmount = sellPortfolio.getAmountOfMoney();
        BigMoney sellerLeftItem = sellPortfolio.obtainAmountOfAvailableItemFor(coinId.toString(), CurrencyUnit.of(coinId.toString()));
        assertThat(sellerLeftAmount.compareTo(Money.of(CurrencyUnit.of(Currencies.CNY), 65.187)), is(0));
        assertThat(sellerLeftItem.compareTo(Money.of(CurrencyUnit.of(Currencies.BTC), 0.97282794)), is(0));

        //verify orders 
        String orderBookIdentifier = orderBookEntry.getPrimaryKey();
        List<OrderEntry> orders = orderQueryRepository.findByOrderBookIdentifier(orderBookIdentifier);

        assertThat(orders, hasSize(2));

        //verify buy order 
        Optional<OrderEntry> buyOrderOptional = Iterables.tryFind(orders, new Predicate<OrderEntry>() {
            @Override
            public boolean apply(OrderEntry input) {
                return input.getType() == OrderType.BUY;
            }
        });

        assertThat(buyOrderOptional.isPresent(), is(true));
        OrderEntry buyOrder = buyOrderOptional.get();
        assertThat(buyOrder.getItemPrice().isEqual(price), is(true));
        assertThat(buyOrder.getCurrencyPair(), equalTo(currencyPair));
        assertThat(buyOrder.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(buyOrder.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(buyOrder.getType(), equalTo(OrderType.BUY));
        assertThat(buyOrder.getUserId(), equalTo(buyPortfolio.getPrimaryKey()));
        assertThat(buyOrder.getItemRemaining().isNegativeOrZero(), is(true));
        assertThat(buyOrder.getTradeAmount().isEqual(tradeAmount), is(true));
        assertThat(buyOrder.getExecutedCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.326)), is(true));
        assertThat(buyOrder.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.326)), is(true));

        assertThat(buyOrder.getPlacedDate(), notNullValue());
        assertThat(buyOrder.getLastTradedTime(), notNullValue());
        assertThat(buyOrder.getCompleteDate(), notNullValue());
        assertThat(buyOrder.getLastTradedTime(), equalTo(buyOrder.getCompleteDate()));
        assertThat(buyOrder.getPlacedDate().compareTo(buyOrder.getLastTradedTime()), lessThanOrEqualTo(0));
        assertThat(buyOrder.getPlacedDate().compareTo(buyOrder.getCompleteDate()), lessThanOrEqualTo(0));

        //verify sell order 
        Optional<OrderEntry> sellOrderOptional = Iterables.tryFind(orders, new Predicate<OrderEntry>() {
            @Override
            public boolean apply(OrderEntry input) {
                return input.getType() == OrderType.SELL;
            }
        });

        assertThat(sellOrderOptional.isPresent(), is(true));
        OrderEntry sellOrder = sellOrderOptional.get();
        assertThat(sellOrder.getCurrencyPair(), equalTo(new CurrencyPair(Currencies.BTC)));
        assertThat(sellOrder.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(sellOrder.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(sellOrder.getType(), equalTo(OrderType.SELL));
        assertThat(sellOrder.getUserId(), equalTo(sellPortfolio.getPrimaryKey()));
        assertThat(sellOrder.getItemPrice().isEqual(price), is(true));
        assertThat(sellOrder.getItemRemaining().isPositive(), is(true));
        assertThat(sellOrder.getTradeAmount().isEqual(tradeAmount.plus(0.001)), is(true));
        assertThat(sellOrder.getExecutedCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 0.02616706)), is(true));
        assertThat(sellOrder.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 0.02617206)), is(true));

        assertThat(sellOrder.getCompleteDate(), nullValue());
        assertThat(sellOrder.getPlacedDate(), notNullValue());
        assertThat(sellOrder.getLastTradedTime(), notNullValue());
        assertThat(sellOrder.getPlacedDate().compareTo(sellOrder.getLastTradedTime()), lessThanOrEqualTo(0));

        //order book 
        OrderBookEntry orderBook = obtainOrderBookByCoinName(coinId.toString(), coinRepository, orderBookRepository);

        assertThat(orderBook.getPrimaryKey(), equalTo(orderBookIdentifier));
        assertThat(orderBook.getCurrencyPair(), equalTo(new CurrencyPair(Currencies.BTC)));
        assertThat(orderBook.getBaseCurrency(), equalTo(Constants.CURRENCY_UNIT_BTC));
        assertThat(orderBook.getCounterCurrency(), equalTo(Constants.DEFAULT_CURRENCY_UNIT));
        assertThat(orderBook.getCoinIdentifier(), equalTo(coinId.toString()));

        assertThat(orderBook.getHighestBuyId(), nullValue());
        assertThat(orderBook.getHighestBuyPrice().isEqual(BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT)), is(true));
        assertThat(orderBook.getLowestSellPrice().isEqual(price), is(true));
        assertThat(orderBook.getLowestSellId(), equalTo(sellOrder.getPrimaryKey()));
        assertThat(orderBook.getLastTradedTime(), equalTo(sellOrder.getLastTradedTime()));
        assertThat(orderBook.getTradedPrice().isEqual(price), is(true));
        assertThat(orderBook.getSellTransactionId(), equalTo(sellTransactionId.toString()));
        assertThat(orderBook.getBuyTransactionId(), equalTo(buyTransactionId.toString()));


        //trade transaction 
        List<TransactionEntry> sellTransactions = transactionQueryRepository.findByPortfolioIdentifier(sellPortfolio.getIdentifier());
        assertThat(sellTransactions, hasSize(1));
        TransactionEntry sellTransactionEntry = sellTransactions.get(0);
        assertThat(sellTransactionEntry.getAmountOfExecutedItem().isEqual(tradeAmount), is(true));
        assertThat(sellTransactionEntry.getAmountOfItem().isEqual(tradeAmount.plus(0.001)), is(true));
        assertThat(sellTransactionEntry.getCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 0.02616706)), is(true));
        assertThat(sellTransactionEntry.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 65.187)), is(true));
        assertThat(sellTransactionEntry.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(sellTransactionEntry.getPortfolioIdentifier(), equalTo(sellPortfolio.getIdentifier()));
        assertThat(sellTransactionEntry.getState(), equalTo(TransactionState.PARTIALLY_EXECUTED));
        assertThat(sellTransactionEntry.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 0.02617206)), is(true));
        assertThat(sellTransactionEntry.getTotalMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 65.2)), is(true));

        List<TransactionEntry> buyTransactions = transactionQueryRepository.findByPortfolioIdentifier(buyPortfolio.getIdentifier());
        assertThat(buyTransactions, hasSize(1));
        TransactionEntry buyTransactionEntry = buyTransactions.get(0);
        assertThat(buyTransactionEntry.getAmountOfExecutedItem().isEqual(tradeAmount), is(true));
        assertThat(buyTransactionEntry.getAmountOfItem().isEqual(tradeAmount), is(true));
        assertThat(buyTransactionEntry.getCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.326)), is(true));
        assertThat(buyTransactionEntry.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 65.187)), is(true));
        assertThat(buyTransactionEntry.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(buyTransactionEntry.getPortfolioIdentifier(), equalTo(buyPortfolio.getIdentifier()));
        assertThat(buyTransactionEntry.getState(), equalTo(TransactionState.EXECUTED));
        assertThat(buyTransactionEntry.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.326)), is(true));
        assertThat(buyTransactionEntry.getTotalMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 65.187)), is(true));

        //executed trade 
        List<TradeExecutedEntry> executedEntries = tradeExecutedQueryRepository.findByOrderBookIdentifier(orderBookIdentifier, new PageRequest(0, 10));

        assertThat(executedEntries, hasSize(1));
        TradeExecutedEntry executedEntry = executedEntries.get(0);
        assertThat(executedEntry.getCoinName(), equalTo(coinName));
        assertThat(executedEntry.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(executedEntry.getTradedAmount().isEqual(tradeAmount), is(true));
        assertThat(executedEntry.getTradedPrice().isEqual(price), is(true));
        assertThat(executedEntry.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 65.187)), is(true));
        assertThat(executedEntry.getTradeTime(), notNullValue());
        assertThat(executedEntry.getTradeTime(), equalTo(buyOrder.getLastTradedTime()));
        assertThat(executedEntry.getTradeTime(), equalTo(sellOrder.getLastTradedTime()));
        assertThat(executedEntry.getTradeType(), equalTo(TradeType.SELL));
    }

    @Test(timeout = 30000)
    public void testExecuteExactOneToOneOrder() throws Exception {
        final BigDecimal moneyInAccount = BigDecimal.valueOf(97.485);
        final BigDecimal btcInAccount = BigDecimal.valueOf(8.04);
        final BigDecimal btcAmount = BigDecimal.valueOf(8.0000);

        addMoney(userId1, moneyInAccount, commandGateway, portfolioRepository);
        addItems(userId2, "BTC", btcInAccount, commandGateway, portfolioRepository);

        BigMoney price = BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(12.125));
        BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of(Currencies.BTC), btcAmount);
        OrderBookEntry orderBookEntry = obtainOrderBookByCoinName(coinId.toString(), coinRepository, orderBookRepository);

        //user1 place buy order 
        PortfolioEntry buyPortfolio = portfolioRepository.findByUserIdentifier(userId1.toString());
        TransactionId buyTransactionId = new TransactionId();
        placeBuyOrder(coinId, currencyPair, orderBookEntry, buyPortfolio, tradeAmount, price, buyTransactionId, commandGateway);

        //user2 place a sell order 
        PortfolioEntry sellPortfolio = portfolioRepository.findByUserIdentifier(userId2.toString());
        TransactionId sellTransactionId = new TransactionId();
        placeSellOrder(coinId, currencyPair, orderBookEntry, sellPortfolio, tradeAmount, price, sellTransactionId, commandGateway);


        //sleep to wait for the execution complete 
        waitForComplete(sellPortfolio);
        /****************************data***************************************** 
         user1                             after trading           user1 
         money: 97.485                                             money: 0 
         btc: 0                                                    btc: 8 
         price: 12.125 
         amount: 8 
         commission: 0.5%                                          commission: 0.485
         total: 97.485                                             total: 97.485 

         user2                             after trading           user2 
         money: 0                                                  money: 97 
         btc: 8                                                    btc: 0 
         price: 12.125 
         amount: 8 
         commission: 0.5%                                          commission: 8 * 0.5% = 0.04
         total money amount: 8.04                                  total: 8.04 
         **********************************************************************************/

        //verify data 

        //buyPortfolio 
        buyPortfolio = portfolioRepository.findByUserIdentifier(userId1.toString());
        BigMoney buyerLeftAmount = buyPortfolio.getAmountOfMoney();
        BigMoney buyerLeftItem = buyPortfolio.obtainAmountOfAvailableItemFor(coinId.toString(), CurrencyUnit.of(coinId.toString()));
        assertThat(buyerLeftAmount.compareTo(Money.of(CurrencyUnit.of(Currencies.CNY), 0)), is(0));
        assertThat(buyerLeftItem.compareTo(Money.of(CurrencyUnit.of(Currencies.BTC), 8)), is(0));

        //sellPortfolio 
        sellPortfolio = portfolioRepository.findByUserIdentifier(userId2.toString());
        BigMoney sellerLeftAmount = sellPortfolio.getAmountOfMoney();
        BigMoney sellerLeftItem = sellPortfolio.obtainAmountOfAvailableItemFor(coinId.toString(), CurrencyUnit.of(coinId.toString()));
        assertThat(sellerLeftAmount.compareTo(Money.of(CurrencyUnit.of(Currencies.CNY), 97)), is(0));
        assertThat(sellerLeftItem.compareTo(Money.of(CurrencyUnit.of(Currencies.BTC), 0)), is(0));

        //verify orders 
        String orderBookIdentifier = orderBookEntry.getPrimaryKey();
        List<OrderEntry> orders = orderQueryRepository.findByOrderBookIdentifier(orderBookIdentifier);

        assertThat(orders, hasSize(2));

        //verify buy order 
        Optional<OrderEntry> buyOrderOptional = Iterables.tryFind(orders, new Predicate<OrderEntry>() {
            @Override
            public boolean apply(OrderEntry input) {
                return input.getType() == OrderType.BUY;
            }
        });

        assertThat(buyOrderOptional.isPresent(), is(true));
        OrderEntry buyOrder = buyOrderOptional.get();
        assertThat(buyOrder.getCurrencyPair(), equalTo(currencyPair));
        assertThat(buyOrder.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(buyOrder.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(buyOrder.getType(), equalTo(OrderType.BUY));
        assertThat(buyOrder.getUserId(), equalTo(buyPortfolio.getPrimaryKey()));
        assertThat(buyOrder.getItemPrice().isEqual(price), is(true));
        assertThat(buyOrder.getItemRemaining().isNegativeOrZero(), is(true));
        assertThat(buyOrder.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(0.485))), is(true));
        assertThat(buyOrder.getExecutedCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(0.485))), is(true));
        assertThat(buyOrder.getTradeAmount().isEqual(tradeAmount), is(true));

        assertThat(buyOrder.getPlacedDate(), notNullValue());
        assertThat(buyOrder.getLastTradedTime(), notNullValue());
        assertThat(buyOrder.getCompleteDate(), notNullValue());
        assertThat(buyOrder.getLastTradedTime(), equalTo(buyOrder.getCompleteDate()));

        assertThat(buyOrder.getPlacedDate(), notNullValue());
        assertThat(buyOrder.getPlacedDate().compareTo(buyOrder.getLastTradedTime()), lessThanOrEqualTo(0));
        assertThat(buyOrder.getPlacedDate().compareTo(buyOrder.getCompleteDate()), lessThanOrEqualTo(0));

        //verify sell order 
        Optional<OrderEntry> sellOrderOptional = Iterables.tryFind(orders, new Predicate<OrderEntry>() {
            @Override
            public boolean apply(OrderEntry input) {
                return input.getType() == OrderType.SELL;
            }
        });

        assertThat(sellOrderOptional.isPresent(), is(true));
        OrderEntry sellOrder = sellOrderOptional.get();
        assertThat(sellOrder.getCurrencyPair(), equalTo(new CurrencyPair(Currencies.BTC)));
        assertThat(sellOrder.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(sellOrder.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(sellOrder.getType(), equalTo(OrderType.SELL));
        assertThat(sellOrder.getUserId(), equalTo(sellPortfolio.getPrimaryKey()));
        assertThat(sellOrder.getItemPrice().isEqual(price), is(true));
        assertThat(sellOrder.getItemRemaining().isNegativeOrZero(), is(true));
        assertThat(sellOrder.getTradeAmount().isEqual(tradeAmount), is(true));
        assertThat(sellOrder.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(0.04))), is(true));
        assertThat(sellOrder.getExecutedCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(0.04))), is(true));

        assertThat(sellOrder.getCompleteDate(), notNullValue());
        assertThat(sellOrder.getLastTradedTime(), notNullValue());
        assertThat(sellOrder.getLastTradedTime(), equalTo(sellOrder.getCompleteDate()));

        assertThat(sellOrder.getPlacedDate(), notNullValue());
        assertThat(sellOrder.getPlacedDate().compareTo(sellOrder.getLastTradedTime()), lessThanOrEqualTo(0));
        assertThat(sellOrder.getPlacedDate().compareTo(sellOrder.getCompleteDate()), lessThanOrEqualTo(0));

        //order book 
        OrderBookEntry orderBook = obtainOrderBookByCoinName(coinId.toString(), coinRepository, orderBookRepository);

        assertThat(orderBook.getPrimaryKey(), equalTo(orderBookIdentifier));
        assertThat(orderBook.getCurrencyPair(), equalTo(new CurrencyPair(Currencies.BTC)));
        assertThat(orderBook.getBaseCurrency(), equalTo(Constants.CURRENCY_UNIT_BTC));
        assertThat(orderBook.getCounterCurrency(), equalTo(Constants.DEFAULT_CURRENCY_UNIT));
        assertThat(orderBook.getCoinIdentifier(), equalTo(coinId.toString()));

        assertThat(orderBook.getHighestBuyId(), nullValue());
        assertThat(orderBook.getLowestSellId(), nullValue());
        assertThat(orderBook.getLowestSellPrice().isEqual(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, Constants.INIT_SELL_PRICE)), is(true));
        assertThat(orderBook.getHighestBuyPrice().isEqual(BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT)), is(true));
        assertThat(orderBook.getLastTradedTime(), equalTo(sellOrder.getLastTradedTime()));
        assertThat(orderBook.getTradedPrice().isEqual(price), is(true));
        assertThat(orderBook.getSellTransactionId(), equalTo(sellTransactionId.toString()));
        assertThat(orderBook.getBuyTransactionId(), equalTo(buyTransactionId.toString()));

        //trade transaction 
        List<TransactionEntry> sellTransactions = transactionQueryRepository.findByPortfolioIdentifier(sellPortfolio.getIdentifier());
        assertThat(sellTransactions, hasSize(1));
        TransactionEntry sellTransactionEntry = sellTransactions.get(0);
        assertThat(sellTransactionEntry.getAmountOfExecutedItem().isEqual(tradeAmount), is(true));
        assertThat(sellTransactionEntry.getAmountOfItem().isEqual(tradeAmount), is(true));
        assertThat(sellTransactionEntry.getCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 0.04)), is(true));
        assertThat(sellTransactionEntry.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 97)), is(true));
        assertThat(sellTransactionEntry.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(sellTransactionEntry.getPortfolioIdentifier(), equalTo(sellPortfolio.getIdentifier()));
        assertThat(sellTransactionEntry.getState(), equalTo(TransactionState.EXECUTED));
        assertThat(sellTransactionEntry.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 0.04)), is(true));
        assertThat(sellTransactionEntry.getTotalMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 97)), is(true));

        List<TransactionEntry> buyTransactions = transactionQueryRepository.findByPortfolioIdentifier(buyPortfolio.getIdentifier());
        assertThat(buyTransactions, hasSize(1));
        TransactionEntry buyTransactionEntry = buyTransactions.get(0);
        assertThat(buyTransactionEntry.getAmountOfExecutedItem().isEqual(tradeAmount), is(true));
        assertThat(buyTransactionEntry.getAmountOfItem().isEqual(tradeAmount), is(true));
        assertThat(buyTransactionEntry.getCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.485)), is(true));
        assertThat(buyTransactionEntry.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 97)), is(true));
        assertThat(buyTransactionEntry.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(buyTransactionEntry.getPortfolioIdentifier(), equalTo(buyPortfolio.getIdentifier()));
        assertThat(buyTransactionEntry.getState(), equalTo(TransactionState.EXECUTED));
        assertThat(buyTransactionEntry.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.485)), is(true));
        assertThat(buyTransactionEntry.getTotalMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 97)), is(true));

        //executed trade 
        List<TradeExecutedEntry> executedEntries = tradeExecutedQueryRepository.findByOrderBookIdentifier(orderBookIdentifier, new PageRequest(0, 10));

        assertThat(executedEntries, hasSize(1));
        TradeExecutedEntry executedEntry = executedEntries.get(0);
        assertThat(executedEntry.getCoinName(), equalTo(coinName));
        assertThat(executedEntry.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(executedEntry.getTradedAmount().isEqual(tradeAmount), is(true));
        assertThat(executedEntry.getTradedPrice().isEqual(price), is(true));
        assertThat(executedEntry.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 97)), is(true));
        assertThat(executedEntry.getTradeTime(), notNullValue());
        assertThat(executedEntry.getTradeTime(), equalTo(buyOrder.getLastTradedTime()));
        assertThat(executedEntry.getTradeTime(), equalTo(sellOrder.getLastTradedTime()));
        assertThat(executedEntry.getTradeType(), equalTo(TradeType.SELL));

    }


    @Test(timeout = 30000)
    public void testExecuteOneBuyToManySell() throws Exception {
        final BigDecimal moneyInAccount = BigDecimal.valueOf(97.485);
        final BigDecimal btcInAccount = BigDecimal.valueOf(12.08);

        addMoney(userId1, moneyInAccount, commandGateway, portfolioRepository);
        addItems(userId2, "BTC", btcInAccount, commandGateway, portfolioRepository);

        BigMoney buyPrice = BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(12.125));
        BigMoney buyAmount = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(8.0000));

        BigMoney sellPrice1 = BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(12));
        BigMoney sellPrice2 = BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(12.1));
        BigMoney sellPrice3 = BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(12.125));

        BigMoney sellAmount1 = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(2.0000));
        BigMoney sellAmount2 = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(4.0000));
        BigMoney sellAmount3 = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(6.0000));

        OrderBookEntry orderBookEntry = obtainOrderBookByCoinName(coinId.toString(), coinRepository, orderBookRepository); 

        /* 
        user2: 
        sell price1 12            2     sell commission 0.01   executed buy commission 0.12 
        sell price2 12.1          4     sell commission 0.02   executed buy commission 0.242 
        sell price3 12.125        6     sell commission 0.03   executed buy commission 0.36345(all) 2 coins 0.12125 = 0.121 

        user1 
        buy price 12.125          8     first reserved commission, 0.485, actual one 0.12+0.242+0.121 = 0.483 
        buy price 12.125          8     remained money 0.485 - 0.48325 = 0.00175 
        */
        //user1 place buy order 
        PortfolioEntry buyPortfolio = portfolioRepository.findByUserIdentifier(userId1.toString());
        TransactionId buyTransactionId = new TransactionId();
        placeBuyOrder(coinId, currencyPair, orderBookEntry, buyPortfolio, buyAmount, buyPrice, buyTransactionId, commandGateway);

        //user2 place a sell order 
        PortfolioEntry sellPortfolio = portfolioRepository.findByUserIdentifier(userId2.toString());
        TransactionId sellTransactionId1 = new TransactionId();
        TransactionId sellTransactionId2 = new TransactionId();
        TransactionId sellTransactionId3 = new TransactionId();
        placeSellOrder(coinId, currencyPair, orderBookEntry, sellPortfolio, sellAmount1, sellPrice1, sellTransactionId1, commandGateway);
        placeSellOrder(coinId, currencyPair, orderBookEntry, sellPortfolio, sellAmount2, sellPrice2, sellTransactionId2, commandGateway);
        placeSellOrder(coinId, currencyPair, orderBookEntry, sellPortfolio, sellAmount3, sellPrice3, sellTransactionId3, commandGateway);

        //sleep to wait for the execution complete 

        waitForComplete(buyPortfolio);

        /****************************data***************************************** 
         user1                             after trading           user1 
         money: 97.485                                             money: 0.00175 
         btc: 0                                                    btc: 8 
         price: 12.125 
         amount: 8 
         commission: 0.5%                                          commission: 0.48325
         total: 97.485                                             total: 97.48325 


         user2                             after trading           user2 
         money: 0                                                  money: 97
         btc: 8                                                    btc:12.08 - 12 - 0.01 - 0.02 - 0.03 = 0.02 
         price: 12.125                                             reserved left: 4 btc for selling 
         amount: 8 
         commission: 0.5%                                          commission: 8 * 0.5% = 0.04
         total money amount: 8.04                                  total: 8.04 
         **********************************************************************************/

        //verify data 

        //buyPortfolio 
        buyPortfolio = portfolioRepository.findByUserIdentifier(userId1.toString());
        BigMoney buyerLeftAmount = buyPortfolio.getAmountOfMoney();
        BigMoney buyerLeftItem = buyPortfolio.obtainAmountOfAvailableItemFor(coinId.toString(), CurrencyUnit.of(coinId.toString()));
        assertThat("expect money 0.001, but is " + buyerLeftAmount, buyerLeftAmount.compareTo(Money.of(CurrencyUnit.of(Currencies.CNY), 0.001)), is(0));
        assertThat("expect btc 8, but is " + buyerLeftItem, buyerLeftItem.compareTo(Money.of(CurrencyUnit.of(Currencies.BTC), 8)), is(0));

        //sellPortfolio 
        sellPortfolio = portfolioRepository.findByUserIdentifier(userId2.toString());
        BigMoney sellerLeftAmount = sellPortfolio.getAmountOfMoney();
        BigMoney itemsInPossession = sellPortfolio.obtainAmountOfItemInPossessionFor(coinId.toString(), CurrencyUnit.of(coinId.toString()));
        BigMoney itemsReserved = sellPortfolio.obtainAmountOfReservedItemFor(coinId.toString(), CurrencyUnit.of(coinId.toString()));
        BigMoney sellerLeftItem = sellPortfolio.obtainAmountOfAvailableItemFor(coinId.toString(), CurrencyUnit.of(coinId.toString()));
        assertThat("expect money 97, but is " + sellerLeftAmount, sellerLeftAmount.compareTo(Money.of(CurrencyUnit.of(Currencies.CNY), 97)), is(0));
        assertThat(sellerLeftItem.compareTo(Money.of(CurrencyUnit.of(Currencies.BTC), 0.02)), is(0));
        assertThat(itemsInPossession.compareTo(Money.of(CurrencyUnit.of(Currencies.BTC), 4.04)), is(0));
        assertThat("expect btc 4.02, but is " + sellerLeftAmount, itemsReserved.compareTo(Money.of(CurrencyUnit.of(Currencies.BTC), 4.02)), is(0));

        //verify orders 
        String orderBookIdentifier = orderBookEntry.getPrimaryKey();
        List<OrderEntry> orders = orderQueryRepository.findByOrderBookIdentifier(orderBookIdentifier);

        assertThat(orders, hasSize(4));

        //verify buy order 
        List<OrderEntry> buyOrders = Lists.newArrayList(Iterables.filter(orders, new Predicate<OrderEntry>() {
            @Override
            public boolean apply(OrderEntry input) {
                return input.getType() == OrderType.BUY;
            }
        }));

        assertThat(buyOrders, hasSize(1));

        OrderEntry buyOrder = buyOrders.get(0);
        assertThat(buyOrder.getCurrencyPair(), equalTo(currencyPair));
        assertThat(buyOrder.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(buyOrder.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(buyOrder.getType(), equalTo(OrderType.BUY));
        assertThat(buyOrder.getUserId(), equalTo(buyPortfolio.getPrimaryKey()));
        assertThat(buyOrder.getTradeAmount().isEqual(buyAmount), is(true));
        assertThat(buyOrder.getItemPrice().isEqual(buyPrice), is(true));
        assertThat(buyOrder.getItemRemaining().isNegativeOrZero(), is(true));
        assertThat(buyOrder.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.485)), is(true));
        assertThat(buyOrder.getExecutedCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.484)), is(true));

        assertThat(buyOrder.getPlacedDate(), notNullValue());
        assertThat(buyOrder.getLastTradedTime(), notNullValue());
        assertThat(buyOrder.getCompleteDate(), notNullValue());
        assertThat(buyOrder.getLastTradedTime(), equalTo(buyOrder.getCompleteDate()));

        assertThat(buyOrder.getPlacedDate(), notNullValue());
        assertThat(buyOrder.getPlacedDate().compareTo(buyOrder.getLastTradedTime()), lessThanOrEqualTo(0));
        assertThat(buyOrder.getPlacedDate().compareTo(buyOrder.getCompleteDate()), lessThanOrEqualTo(0));

        //verify sell order 
        List<OrderEntry> sellOrders = Lists.newArrayList(Iterables.filter(orders, new Predicate<OrderEntry>() {
            @Override
            public boolean apply(OrderEntry input) {
                return input.getType() == OrderType.SELL;
            }
        }));

        Collections.sort(sellOrders, new Comparator<OrderEntry>() {
            @Override
            public int compare(OrderEntry o1, OrderEntry o2) {
                return o1.getLastTradedTime().compareTo(o2.getLastTradedTime());
            }
        });

        assertThat(sellOrders.size(), is(3));
        OrderEntry sellOrder1 = sellOrders.get(0);
        OrderEntry sellOrder2 = sellOrders.get(1);
        OrderEntry sellOrder3 = sellOrders.get(2);

        //sell order1 
        assertThat(sellOrder1.getCurrencyPair(), equalTo(new CurrencyPair(Currencies.BTC)));
        assertThat(sellOrder1.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(sellOrder1.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(sellOrder1.getType(), equalTo(OrderType.SELL));
        assertThat(sellOrder1.getUserId(), equalTo(sellPortfolio.getPrimaryKey()));
        assertThat(sellOrder1.getItemPrice().isEqual(sellPrice1), is(true));
        assertThat(sellOrder1.getItemRemaining().isZero(), is(true));
        assertThat(sellOrder1.getTradeAmount().isEqual(sellAmount1), is(true));
        assertThat(sellOrder1.getExecutedCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(0.01))), is(true));
        assertThat(sellOrder1.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(0.01))), is(true));

        assertThat(sellOrder1.getCompleteDate(), notNullValue());
        assertThat(sellOrder1.getLastTradedTime(), notNullValue());
        assertThat(sellOrder1.getLastTradedTime(), equalTo(sellOrder1.getCompleteDate()));

        assertThat(sellOrder1.getPlacedDate(), notNullValue());
        assertThat(sellOrder1.getPlacedDate().compareTo(sellOrder1.getLastTradedTime()), lessThanOrEqualTo(0));
        assertThat(sellOrder1.getPlacedDate().compareTo(sellOrder1.getCompleteDate()), lessThanOrEqualTo(0));

        //sell order2 
        assertThat(sellOrder2.getCurrencyPair(), equalTo(new CurrencyPair(Currencies.BTC)));
        assertThat(sellOrder2.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(sellOrder2.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(sellOrder2.getType(), equalTo(OrderType.SELL));
        assertThat(sellOrder2.getUserId(), equalTo(sellPortfolio.getPrimaryKey()));
        assertThat(sellOrder2.getItemPrice().isEqual(sellPrice2), is(true));
        assertThat(sellOrder2.getItemRemaining().isZero(), is(true));
        assertThat(sellOrder2.getTradeAmount().isEqual(sellAmount2), is(true));
        assertThat(sellOrder2.getExecutedCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(0.02))), is(true));
        assertThat(sellOrder2.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(0.02))), is(true));

        assertThat(sellOrder2.getCompleteDate(), notNullValue());
        assertThat(sellOrder2.getLastTradedTime(), notNullValue());
        assertThat(sellOrder2.getLastTradedTime(), equalTo(sellOrder2.getCompleteDate()));

        assertThat(sellOrder2.getPlacedDate(), notNullValue());
        assertThat(sellOrder2.getPlacedDate().compareTo(sellOrder2.getLastTradedTime()), lessThanOrEqualTo(0));
        assertThat(sellOrder2.getPlacedDate().compareTo(sellOrder2.getCompleteDate()), lessThanOrEqualTo(0));

        //sell order3 
        assertThat(sellOrder3.getCurrencyPair(), equalTo(new CurrencyPair(Currencies.BTC)));
        assertThat(sellOrder3.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(sellOrder3.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(sellOrder3.getType(), equalTo(OrderType.SELL));
        assertThat(sellOrder3.getUserId(), equalTo(sellPortfolio.getPrimaryKey()));
        assertThat(sellOrder3.getTradeAmount().isEqual(sellAmount3), is(true));
        assertThat(sellOrder3.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(0.03))), is(true));
        assertThat(sellOrder3.getItemPrice().isEqual(sellPrice3), is(true));
        assertThat(sellOrder3.getItemRemaining().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(4.0000))), is(true));
        assertThat(sellOrder3.getExecutedCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(0.01))), is(true));
        assertThat(sellOrder3.getExecutedCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(0.01))), is(true));
        assertThat(sellOrder3.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(0.03))), is(true));

        assertThat(sellOrder3.getCompleteDate(), nullValue());
        assertThat(sellOrder3.getLastTradedTime(), notNullValue());

        assertThat(sellOrder3.getPlacedDate(), notNullValue());
        assertThat(sellOrder3.getPlacedDate().compareTo(sellOrder3.getLastTradedTime()), lessThanOrEqualTo(0));

        //order book
        OrderBookEntry orderBook = obtainOrderBookByCoinName(coinId.toString(), coinRepository, orderBookRepository);

        assertThat(orderBook.getPrimaryKey(), equalTo(orderBookIdentifier));
        assertThat(orderBook.getCurrencyPair(), equalTo(new CurrencyPair(Currencies.BTC)));
        assertThat(orderBook.getBaseCurrency(), equalTo(Constants.CURRENCY_UNIT_BTC));
        assertThat(orderBook.getCounterCurrency(), equalTo(Constants.DEFAULT_CURRENCY_UNIT));
        assertThat(orderBook.getCoinIdentifier(), equalTo(coinId.toString()));

        assertThat(orderBook.getHighestBuyId(), nullValue());
        assertThat(orderBook.getHighestBuyPrice().isEqual(BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT)), is(true));
        assertThat(orderBook.getLowestSellPrice().isEqual(sellPrice3), is(true));
        assertThat(orderBook.getLowestSellId(), equalTo(sellOrder3.getPrimaryKey()));
        assertThat(orderBook.getLastTradedTime(), equalTo(sellOrder3.getLastTradedTime()));
        assertThat(orderBook.getTradedPrice().isEqual(sellPrice3), is(true));
        assertThat(orderBook.getSellTransactionId(), equalTo(sellTransactionId3.toString()));
        assertThat(orderBook.getBuyTransactionId(), equalTo(buyTransactionId.toString()));

        //trade transaction 
        List<TransactionEntry> sellTransactions = transactionQueryRepository.findByPortfolioIdentifier(sellPortfolio.getIdentifier());

        Collections.sort(sellTransactions, new Comparator<TransactionEntry>() {
            @Override
            public int compare(TransactionEntry o1, TransactionEntry o2) {
                return o1.getCreated().compareTo(o2.getCreated());
            }
        });

        assertThat(sellTransactions, hasSize(3));
        TransactionEntry sellTransactionEntry1 = sellTransactions.get(0);
        assertThat(sellTransactionEntry1.getAmountOfExecutedItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 2)), is(true));
        assertThat(sellTransactionEntry1.getAmountOfItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 2)), is(true));
        assertThat(sellTransactionEntry1.getCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 0.01)), is(true));
        assertThat(sellTransactionEntry1.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 24.25)), is(true));
        assertThat(sellTransactionEntry1.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(sellTransactionEntry1.getPortfolioIdentifier(), equalTo(sellPortfolio.getIdentifier()));
        assertThat(sellTransactionEntry1.getState(), equalTo(TransactionState.EXECUTED));
        assertThat(sellTransactionEntry1.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 0.01)), is(true));
        assertThat(sellTransactionEntry1.getTotalMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 24)), is(true));

        TransactionEntry sellTransactionEntry2 = sellTransactions.get(1);
        assertThat(sellTransactionEntry2.getAmountOfExecutedItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 4)), is(true));
        assertThat(sellTransactionEntry2.getAmountOfItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 4)), is(true));
        assertThat(sellTransactionEntry2.getCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 0.02)), is(true));
        assertThat(sellTransactionEntry2.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 48.5)), is(true));
        assertThat(sellTransactionEntry2.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(sellTransactionEntry2.getPortfolioIdentifier(), equalTo(sellPortfolio.getIdentifier()));
        assertThat(sellTransactionEntry2.getState(), equalTo(TransactionState.EXECUTED));
        assertThat(sellTransactionEntry2.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 0.02)), is(true));
        assertThat(sellTransactionEntry2.getTotalMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 48.4)), is(true));

        TransactionEntry sellTransactionEntry3 = sellTransactions.get(2);
        assertThat(sellTransactionEntry3.getAmountOfExecutedItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 2)), is(true));
        assertThat(sellTransactionEntry3.getAmountOfItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 6)), is(true));
        assertThat(sellTransactionEntry3.getCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 0.01)), is(true));
        assertThat(sellTransactionEntry3.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 24.25)), is(true));
        assertThat(sellTransactionEntry3.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(sellTransactionEntry3.getPortfolioIdentifier(), equalTo(sellPortfolio.getIdentifier()));
        assertThat(sellTransactionEntry3.getState(), equalTo(TransactionState.PARTIALLY_EXECUTED));
        assertThat(sellTransactionEntry3.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 0.03)), is(true));
        assertThat(sellTransactionEntry3.getTotalMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 72.75)), is(true));

        List<TransactionEntry> buyTransactions = transactionQueryRepository.findByPortfolioIdentifier(buyPortfolio.getIdentifier());
        assertThat(buyTransactions, hasSize(1));
        TransactionEntry buyTransactionEntry = buyTransactions.get(0);
        assertThat(buyTransactionEntry.getAmountOfExecutedItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 8)), is(true));
        assertThat(buyTransactionEntry.getAmountOfItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 8)), is(true));
        assertThat(buyTransactionEntry.getCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.484)), is(true));
        assertThat(buyTransactionEntry.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 97)), is(true));
        assertThat(buyTransactionEntry.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(buyTransactionEntry.getPortfolioIdentifier(), equalTo(buyPortfolio.getIdentifier()));
        assertThat(buyTransactionEntry.getState(), equalTo(TransactionState.EXECUTED));
        assertThat(buyTransactionEntry.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.485)), is(true));
        assertThat(buyTransactionEntry.getTotalMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 97)), is(true));

        //executed trade 
        List<TradeExecutedEntry> executedEntries = tradeExecutedQueryRepository.findByOrderBookIdentifier(orderBookIdentifier, new PageRequest(0, 10));
        Collections.sort(executedEntries, new Comparator<TradeExecutedEntry>() {
            @Override
            public int compare(TradeExecutedEntry o1, TradeExecutedEntry o2) {
                return o1.getTradeTime().compareTo(o2.getTradeTime());
            }
        });

        assertThat(executedEntries, hasSize(3));
        TradeExecutedEntry executedEntry1 = executedEntries.get(0);
        assertThat(executedEntry1.getCoinName(), equalTo(coinName));
        assertThat(executedEntry1.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(executedEntry1.getTradedAmount().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 2)), is(true));
        assertThat(executedEntry1.getTradedPrice().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 12.125)), is(true));
        assertThat(executedEntry1.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 24.25)), is(true));
        assertThat(executedEntry1.getTradeTime(), notNullValue());
        assertThat(executedEntry1.getTradeTime(), equalTo(sellOrder1.getLastTradedTime()));
        assertThat(executedEntry1.getTradeType(), equalTo(TradeType.SELL));

        TradeExecutedEntry executedEntry2 = executedEntries.get(1);
        assertThat(executedEntry2.getCoinName(), equalTo(coinName));
        assertThat(executedEntry2.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(executedEntry2.getTradedAmount().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 4)), is(true));
        assertThat(executedEntry2.getTradedPrice().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 12.125)), is(true));
        assertThat(executedEntry2.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 48.5)), is(true));
        assertThat(executedEntry2.getTradeTime(), notNullValue());
        assertThat(executedEntry2.getTradeTime(), equalTo(sellOrder2.getLastTradedTime()));
        assertThat(executedEntry2.getTradeType(), equalTo(TradeType.SELL));

        TradeExecutedEntry executedEntry3 = executedEntries.get(2);
        assertThat(executedEntry3.getCoinName(), equalTo(coinName));
        assertThat(executedEntry3.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(executedEntry3.getTradedAmount().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 2)), is(true));
        assertThat(executedEntry3.getTradedPrice().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 12.125)), is(true));
        assertThat(executedEntry3.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 24.25)), is(true));
        assertThat(executedEntry3.getTradeTime(), notNullValue());
        assertThat(executedEntry3.getTradeTime(), equalTo(buyOrder.getLastTradedTime()));
        assertThat(executedEntry3.getTradeTime(), equalTo(sellOrder3.getLastTradedTime()));
        assertThat(executedEntry3.getTradeType(), equalTo(TradeType.SELL));
    }

    private void waitForComplete(PortfolioEntry portfolio) throws InterruptedException {
        List<TransactionEntry> transactions = transactionQueryRepository.findByPortfolioIdentifier(portfolio.getIdentifier());
        TransactionEntry entry = transactions.get(0);
        while (entry.getState() != TransactionState.EXECUTED) {
            TimeUnit.MILLISECONDS.sleep(100);
            transactions = transactionQueryRepository.findByPortfolioIdentifier(portfolio.getIdentifier());
            entry = transactions.get(0);
        }
        TimeUnit.MILLISECONDS.sleep(2000);
    }


    @Test(timeout = 300000)
    public void testExecuteManyBuyToOneSell() throws Exception {
        final BigDecimal moneyInAccount = BigDecimal.valueOf(130);
        final BigDecimal btcInAccount = BigDecimal.valueOf(12.07);

        addMoney(userId1, moneyInAccount, commandGateway, portfolioRepository);
        addItems(userId2, "BTC", btcInAccount, commandGateway, portfolioRepository);

        BigMoney buyPrice1 = BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(12.125));
        BigMoney buyPrice2 = BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(12));
        BigMoney buyPrice3 = BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(12.12));
        BigMoney buyAmount1 = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(4));
        BigMoney buyAmount2 = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(2));
        BigMoney buyAmount3 = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(4));

        BigMoney sellPrice = BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(12));
        BigMoney sellAmount = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(8));
        OrderBookEntry orderBookEntry = obtainOrderBookByCoinName(coinId.toString(), coinRepository, orderBookRepository);

        //user1 place buy order 
        PortfolioEntry buyPortfolio = portfolioRepository.findByUserIdentifier(userId1.toString());
        TransactionId buyTransactionId1 = new TransactionId();
        TransactionId buyTransactionId2 = new TransactionId();
        TransactionId buyTransactionId3 = new TransactionId();
        placeBuyOrder(coinId, currencyPair, orderBookEntry, buyPortfolio, buyAmount1, buyPrice1, buyTransactionId1, commandGateway);
        placeBuyOrder(coinId, currencyPair, orderBookEntry, buyPortfolio, buyAmount2, buyPrice2, buyTransactionId2, commandGateway);
        placeBuyOrder(coinId, currencyPair, orderBookEntry, buyPortfolio, buyAmount3, buyPrice3, buyTransactionId3, commandGateway);

        //user2 place a sell order 
        PortfolioEntry sellPortfolio = portfolioRepository.findByUserIdentifier(userId2.toString());
        TransactionId sellTransactionId = new TransactionId();
        placeSellOrder(coinId, currencyPair, orderBookEntry, sellPortfolio, sellAmount, sellPrice, sellTransactionId, commandGateway);


        //sleep to wait for the execution complete 
        waitForComplete(sellPortfolio);
        /****************************data*****************************************
         user1                             after trading           user1 
         money: 130                                                money: 0
         btc: 0                                                    btc: 8 
         price:  12.125  12  12.12
         amount: 4       2   4
         commission: 0.5%                                          commission: 0.606
         total: 120.98 + 0.604 = 121.584                           total: 8.416
         user2                             after trading           user2 
         money: 0                                                  money: 97 
         btc: 8                                                    btc: 0 
         price: 12
         amount: 8 
         commission: 0.5%                                          commission: 8 * 0.5% = 0.04
         total money amount: 8.04                                  total: 8.04 
         **********************************************************************************/

        //verify data 

        //buyPortfolio 
        buyPortfolio = portfolioRepository.findByUserIdentifier(userId1.toString());
        BigMoney buyerLeftAmount = buyPortfolio.getAmountOfMoney();
        BigMoney moneyToSpend = buyPortfolio.obtainMoneyToSpend();
        BigMoney reservedAmountOfMoney = buyPortfolio.getReservedAmountOfMoney();
        BigMoney buyerLeftItem = buyPortfolio.obtainAmountOfAvailableItemFor(coinId.toString(), CurrencyUnit.of(coinId.toString()));
        assertThat(buyerLeftAmount.compareTo(Money.of(CurrencyUnit.of(Currencies.CNY), 32.787)), is(0));
        assertThat(moneyToSpend.compareTo(Money.of(CurrencyUnit.of(Currencies.CNY), 8.416)), is(0));
        assertThat(reservedAmountOfMoney.compareTo(Money.of(CurrencyUnit.of(Currencies.CNY), 24.371)), is(0));
        assertThat(buyerLeftItem.compareTo(Money.of(CurrencyUnit.of(Currencies.BTC), 8)), is(0));

        //sellPortfolio 
        sellPortfolio = portfolioRepository.findByUserIdentifier(userId2.toString());
        BigMoney sellerLeftAmount = sellPortfolio.getAmountOfMoney();
        BigMoney sellerLeftItem = sellPortfolio.obtainAmountOfAvailableItemFor(coinId.toString(), CurrencyUnit.of(coinId.toString()));
        assertThat(sellerLeftAmount.compareTo(Money.of(CurrencyUnit.of(Currencies.CNY), 96.73)), is(0));
        assertThat(sellerLeftItem.compareTo(Money.of(CurrencyUnit.of(Currencies.BTC), 4.03)), is(0));

        //verify orders 
        String orderBookIdentifier = orderBookEntry.getPrimaryKey();
        List<OrderEntry> orders = orderQueryRepository.findByOrderBookIdentifier(orderBookIdentifier);

        assertThat(orders, hasSize(4));

        //verify buy order 
        List<OrderEntry> buyOrders = Lists.newArrayList(Iterables.filter(orders, new Predicate<OrderEntry>() {
            @Override
            public boolean apply(OrderEntry input) {
                return input.getType() == OrderType.BUY;
            }
        }));

        assertThat(buyOrders, hasSize(3));

        Collections.sort(buyOrders, new Comparator<OrderEntry>() {
            @Override
            public int compare(OrderEntry o1, OrderEntry o2) {
                return o1.getPlacedDate().compareTo(o2.getPlacedDate());
            }
        });

        OrderEntry buyOrder1 = buyOrders.get(0);
        assertThat(buyOrder1.getCurrencyPair(), equalTo(currencyPair));
        assertThat(buyOrder1.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(buyOrder1.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(buyOrder1.getType(), equalTo(OrderType.BUY));
        assertThat(buyOrder1.getUserId(), equalTo(buyPortfolio.getPrimaryKey()));
        assertThat(buyOrder1.getItemPrice().isEqual(buyPrice1), is(true));
        assertThat(buyOrder1.getItemRemaining().isPositive(), is(true));
        assertThat(buyOrder1.getItemRemaining().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(2))), is(true));
        assertThat(buyOrder1.getTradeAmount().isEqual(buyAmount1), is(true));
        assertThat(buyOrder1.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(0.242))), is(true));
        assertThat(buyOrder1.getExecutedCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(0.121))), is(true));

        assertThat(buyOrder1.getPlacedDate(), notNullValue());
        assertThat(buyOrder1.getLastTradedTime(), notNullValue());
        assertThat(buyOrder1.getCompleteDate(), nullValue());

        assertThat(buyOrder1.getPlacedDate(), notNullValue());
        assertThat(buyOrder1.getPlacedDate().compareTo(buyOrder1.getLastTradedTime()), lessThanOrEqualTo(0));

        OrderEntry buyOrder2 = buyOrders.get(1);
        assertThat(buyOrder2.getCurrencyPair(), equalTo(currencyPair));
        assertThat(buyOrder2.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(buyOrder2.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(buyOrder2.getType(), equalTo(OrderType.BUY));
        assertThat(buyOrder2.getUserId(), equalTo(buyPortfolio.getPrimaryKey()));
        assertThat(buyOrder2.getItemPrice().isEqual(buyPrice2), is(true));
        assertThat(buyOrder2.getItemRemaining().isNegativeOrZero(), is(true));
        assertThat(buyOrder2.getTradeAmount().isEqual(buyAmount2), is(true));
        assertThat(buyOrder2.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(0.12))), is(true));
        assertThat(buyOrder2.getExecutedCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(0.12))), is(true));

        assertThat(buyOrder2.getPlacedDate(), notNullValue());
        assertThat(buyOrder2.getLastTradedTime(), notNullValue());
        assertThat(buyOrder2.getCompleteDate(), notNullValue());
        assertThat(buyOrder2.getLastTradedTime(), equalTo(buyOrder2.getCompleteDate()));

        assertThat(buyOrder2.getPlacedDate(), notNullValue());
        assertThat(buyOrder2.getPlacedDate().compareTo(buyOrder2.getLastTradedTime()), lessThanOrEqualTo(0));
        assertThat(buyOrder2.getPlacedDate().compareTo(buyOrder2.getCompleteDate()), lessThanOrEqualTo(0));

        OrderEntry buyOrder3 = buyOrders.get(2);
        assertThat(buyOrder3.getCurrencyPair(), equalTo(currencyPair));
        assertThat(buyOrder3.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(buyOrder3.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(buyOrder3.getType(), equalTo(OrderType.BUY));
        assertThat(buyOrder3.getUserId(), equalTo(buyPortfolio.getPrimaryKey()));
        assertThat(buyOrder3.getItemPrice().isEqual(buyPrice3), is(true));
        assertThat(buyOrder3.getItemRemaining().isNegativeOrZero(), is(true));
        assertThat(buyOrder3.getTradeAmount().isEqual(buyAmount3), is(true));
        assertThat(buyOrder3.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(0.242))), is(true));
        assertThat(buyOrder3.getExecutedCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(0.242))), is(true));

        assertThat(buyOrder3.getPlacedDate(), notNullValue());
        assertThat(buyOrder3.getLastTradedTime(), notNullValue());
        assertThat(buyOrder3.getCompleteDate(), notNullValue());
        assertThat(buyOrder3.getLastTradedTime(), equalTo(buyOrder3.getCompleteDate()));

        assertThat(buyOrder3.getPlacedDate(), notNullValue());
        assertThat(buyOrder3.getPlacedDate().compareTo(buyOrder3.getLastTradedTime()), lessThanOrEqualTo(0));
        assertThat(buyOrder3.getPlacedDate().compareTo(buyOrder3.getCompleteDate()), lessThanOrEqualTo(0));

        //verify sell order 
        Optional<OrderEntry> sellOrderOptional = Iterables.tryFind(orders, new Predicate<OrderEntry>() {
            @Override
            public boolean apply(OrderEntry input) {
                return input.getType() == OrderType.SELL;
            }
        });

        assertThat(sellOrderOptional.isPresent(), is(true));
        OrderEntry sellOrder = sellOrderOptional.get();
        assertThat(sellOrder.getCurrencyPair(), equalTo(new CurrencyPair(Currencies.BTC)));
        assertThat(sellOrder.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(sellOrder.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(sellOrder.getType(), equalTo(OrderType.SELL));
        assertThat(sellOrder.getUserId(), equalTo(sellPortfolio.getPrimaryKey()));
        assertThat(sellOrder.getItemPrice().isEqual(sellPrice), is(true));
        assertThat(sellOrder.getItemRemaining().isZero(), is(true));
        assertThat(sellOrder.getTradeAmount().isEqual(sellAmount), is(true));
        assertThat(sellOrder.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(0.04))), is(true));
        assertThat(sellOrder.getExecutedCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(0.04))), is(true));

        assertThat(sellOrder.getCompleteDate(), notNullValue());
        assertThat(sellOrder.getLastTradedTime(), notNullValue());
        assertThat(sellOrder.getLastTradedTime(), equalTo(sellOrder.getCompleteDate()));

        assertThat(sellOrder.getPlacedDate(), notNullValue());
        assertThat(sellOrder.getPlacedDate().compareTo(sellOrder.getLastTradedTime()), lessThanOrEqualTo(0));
        assertThat(sellOrder.getPlacedDate().compareTo(sellOrder.getCompleteDate()), lessThanOrEqualTo(0));

        //order book 
        OrderBookEntry orderBook = obtainOrderBookByCoinName(coinId.toString(), coinRepository, orderBookRepository);

        assertThat(orderBook.getPrimaryKey(), equalTo(orderBookIdentifier));
        assertThat(orderBook.getCurrencyPair(), equalTo(new CurrencyPair(Currencies.BTC)));
        assertThat(orderBook.getBaseCurrency(), equalTo(Constants.CURRENCY_UNIT_BTC));
        assertThat(orderBook.getCounterCurrency(), equalTo(Constants.DEFAULT_CURRENCY_UNIT));
        assertThat(orderBook.getCoinIdentifier(), equalTo(coinId.toString()));

        assertThat(orderBook.getHighestBuyId(), notNullValue());
        assertThat(orderBook.getHighestBuyId(), equalTo(buyOrder1.getPrimaryKey()));
        assertThat(orderBook.getHighestBuyPrice().isEqual(buyPrice1), is(true));
        assertThat(orderBook.getLowestSellPrice().isEqual(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, Constants.INIT_SELL_PRICE)), is(true));
        assertThat(orderBook.getLowestSellId(), nullValue());
        assertThat(orderBook.getLastTradedTime(), equalTo(sellOrder.getLastTradedTime()));
        assertThat(orderBook.getTradedPrice().isEqual(buyPrice1), is(true));
        assertThat(orderBook.getSellTransactionId(), equalTo(sellTransactionId.toString()));
        assertThat(orderBook.getBuyTransactionId(), equalTo(buyTransactionId1.toString()));

        //trade transaction 
        List<TransactionEntry> buyTransactions = transactionQueryRepository.findByPortfolioIdentifier(buyPortfolio.getIdentifier());
        Collections.sort(buyTransactions, new Comparator<TransactionEntry>() {
            @Override
            public int compare(TransactionEntry o1, TransactionEntry o2) {
                return o1.getCreated().compareTo(o2.getCreated());
            }
        });

        assertThat(buyTransactions, hasSize(3));
        TransactionEntry buyTransactionEntry1 = buyTransactions.get(0);
        assertThat(buyTransactionEntry1.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(buyTransactionEntry1.getPortfolioIdentifier(), equalTo(buyPortfolio.getIdentifier()));
        assertThat(buyTransactionEntry1.getState(), equalTo(TransactionState.PARTIALLY_EXECUTED));
        assertThat(buyTransactionEntry1.getAmountOfItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 4)), is(true));
        assertThat(buyTransactionEntry1.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.242)), is(true));
        assertThat(buyTransactionEntry1.getTotalMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 48.5)), is(true));
        assertThat(buyTransactionEntry1.getAmountOfExecutedItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 2)), is(true));
        assertThat(buyTransactionEntry1.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 24.25)), is(true));
        assertThat(buyTransactionEntry1.getCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.121)), is(true));

        TransactionEntry buyTransactionEntry2 = buyTransactions.get(1);
        assertThat(buyTransactionEntry2.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(buyTransactionEntry2.getPortfolioIdentifier(), equalTo(buyPortfolio.getIdentifier()));
        assertThat(buyTransactionEntry2.getState(), equalTo(TransactionState.EXECUTED));
        assertThat(buyTransactionEntry2.getAmountOfItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 2)), is(true));
        assertThat(buyTransactionEntry2.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.12)), is(true));
        assertThat(buyTransactionEntry2.getTotalMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 24)), is(true));
        assertThat(buyTransactionEntry2.getAmountOfExecutedItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 2)), is(true));
        assertThat(buyTransactionEntry2.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 24)), is(true));
        assertThat(buyTransactionEntry2.getCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.12)), is(true));

        TransactionEntry buyTransactionEntry3 = buyTransactions.get(2);
        assertThat(buyTransactionEntry3.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(buyTransactionEntry3.getPortfolioIdentifier(), equalTo(buyPortfolio.getIdentifier()));
        assertThat(buyTransactionEntry3.getState(), equalTo(TransactionState.EXECUTED));
        assertThat(buyTransactionEntry3.getAmountOfItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 4)), is(true));
        assertThat(buyTransactionEntry3.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.242)), is(true));
        assertThat(buyTransactionEntry3.getTotalMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 48.48)), is(true));
        assertThat(buyTransactionEntry3.getAmountOfExecutedItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 4)), is(true));
        assertThat(buyTransactionEntry3.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 48.48)), is(true));
        assertThat(buyTransactionEntry3.getCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 0.242)), is(true));

        List<TransactionEntry> sellTransactions = transactionQueryRepository.findByPortfolioIdentifier(sellPortfolio.getIdentifier());
        assertThat(sellTransactions, hasSize(1));
        TransactionEntry sellTransactionEntry = sellTransactions.get(0);
        assertThat(sellTransactionEntry.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(sellTransactionEntry.getPortfolioIdentifier(), equalTo(sellPortfolio.getIdentifier()));
        assertThat(sellTransactionEntry.getState(), equalTo(TransactionState.EXECUTED));
        assertThat(sellTransactionEntry.getAmountOfExecutedItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 8)), is(true));
        assertThat(sellTransactionEntry.getAmountOfItem().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 8)), is(true));
        assertThat(sellTransactionEntry.getCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 0.04)), is(true));
        assertThat(sellTransactionEntry.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 96.73)), is(true));
        assertThat(sellTransactionEntry.getTotalCommission().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 0.04)), is(true));
        assertThat(sellTransactionEntry.getTotalMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 96)), is(true));

        //executed trade 
        List<TradeExecutedEntry> executedEntries = tradeExecutedQueryRepository.findByOrderBookIdentifier(orderBookIdentifier, new PageRequest(0, 10));

        Collections.sort(executedEntries, new Comparator<TradeExecutedEntry>() {
            @Override
            public int compare(TradeExecutedEntry o1, TradeExecutedEntry o2) {
                return o1.getTradeTime().compareTo(o2.getTradeTime());
            }
        });

        assertThat(executedEntries, hasSize(3));
        TradeExecutedEntry executedEntry1 = executedEntries.get(0);
        assertThat(executedEntry1.getCoinName(), equalTo(coinName));
        assertThat(executedEntry1.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(executedEntry1.getTradedAmount().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 2)), is(true));
        assertThat(executedEntry1.getTradedPrice().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 12)), is(true));
        assertThat(executedEntry1.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 24)), is(true));
        assertThat(executedEntry1.getTradeTime(), notNullValue());
        assertThat(executedEntry1.getTradeTime(), equalTo(buyOrder2.getLastTradedTime()));
        assertThat(executedEntry1.getTradeType(), equalTo(TradeType.SELL));

        TradeExecutedEntry executedEntry2 = executedEntries.get(1);
        assertThat(executedEntry2.getCoinName(), equalTo(coinName));
        assertThat(executedEntry2.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(executedEntry2.getTradedAmount().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 4)), is(true));
        assertThat(executedEntry2.getTradedPrice().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 12.12)), is(true));
        assertThat(executedEntry2.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 48.48)), is(true));
        assertThat(executedEntry2.getTradeTime(), notNullValue());
        assertThat(executedEntry2.getTradeTime(), equalTo(buyOrder3.getLastTradedTime()));
        assertThat(executedEntry2.getTradeType(), equalTo(TradeType.SELL));

        TradeExecutedEntry executedEntry3 = executedEntries.get(2);
        assertThat(executedEntry3.getCoinName(), equalTo(coinName));
        assertThat(executedEntry3.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(executedEntry3.getTradedAmount().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 2)), is(true));
        assertThat(executedEntry3.getTradedPrice().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 12.125)), is(true));
        assertThat(executedEntry3.getExecutedMoney().isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 24.25)), is(true));
        assertThat(executedEntry3.getTradeTime(), notNullValue());
        assertThat(executedEntry3.getTradeTime(), equalTo(buyOrder1.getLastTradedTime()));
        assertThat(executedEntry3.getTradeType(), equalTo(TradeType.SELL));
    }
}