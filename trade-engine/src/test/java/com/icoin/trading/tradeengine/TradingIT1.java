package com.icoin.trading.tradeengine;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.icoin.trading.tradeengine.application.command.admin.EnsureCqrsIndexesCommand;
import com.icoin.trading.tradeengine.application.command.admin.ReinitializeOrderBookTradingExecutorsCommand;
import com.icoin.trading.tradeengine.application.command.admin.ReinstallDataBaseCommand;
import com.icoin.trading.tradeengine.application.command.coin.CreateCoinCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.DepositCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.AddAmountToPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartBuyTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartSellTransactionCommand;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
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
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.order.repositories.OrderQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.users.application.command.CreateUserCommand;
import com.icoin.trading.users.domain.model.user.Identifier;
import com.icoin.trading.users.domain.model.user.UserId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.homhon.util.TimeUtils.currentTime;
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
        "classpath:META-INF/spring/users-axon.xml",
        "classpath:META-INF/spring/users-context.xml",
        "classpath:META-INF/spring/users-external.xml",
        "classpath:META-INF/spring/users-persistence-mongo.xml",
        "classpath:META-INF/spring/coin-context.xml",
        "classpath:META-INF/spring/configuration-context.xml",
        "classpath:META-INF/spring/external-context.xml"
})
public class TradingIT1 {

    private final CoinId coinId = new CoinId(Currencies.BTC);
    private final BigDecimal money = BigDecimal.valueOf(100000);
    private final BigDecimal btcAmount = BigDecimal.valueOf(5.233412);
    private final String coinName = "Bitcoin";
    private final CurrencyPair currencyPair = new CurrencyPair(Currencies.BTC);
    private final Date time = currentTime();
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

        createBTCCoin();
        userId1 = createUser("User1st", "User", "One");
        userId2 = createUser("User2nd", "User", "Two");

        addMoney(userId1, money);
        addItems(userId2, "BTC", BigDecimal.valueOf(5.25988906));

        EnsureCqrsIndexesCommand ensureCqrsIndexesCommand = new EnsureCqrsIndexesCommand();
        commandGateway.send(ensureCqrsIndexesCommand);

        commandGateway.send(new ReinitializeOrderBookTradingExecutorsCommand());
    }

    private void createBTCCoin() {
        CreateCoinCommand createCoinCommand = new CreateCoinCommand(
                coinId,
                coinName,
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.ZERO),
                BigMoney.of(Constants.CURRENCY_UNIT_BTC, BigDecimal.ZERO));
        commandGateway.send(createCoinCommand);
    }

    private UserId createUser(String userName, String firstName, String lastName) {
        UserId userId = new UserId();
        CreateUserCommand createUser =
                new CreateUserCommand(userId,
                        userName,
                        firstName,
                        lastName,
                        new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252"),
                        userName + "@163.com",
                        userName,
                        userName,
                        Constants.DEFAULT_ROLES,
                        time);
        commandGateway.send(createUser);
        return userId;
    }

    private void addMoney(UserId userId, BigDecimal amount) {
        PortfolioEntry portfolioEntry = portfolioRepository.findByUserIdentifier(userId.toString());
        depositMoneyToPortfolio(portfolioEntry.getIdentifier(), amount);
    }

    public void depositMoneyToPortfolio(String portfolioIdentifier, BigDecimal amountOfMoney) {
        DepositCashCommand command =
                new DepositCashCommand(new PortfolioId(portfolioIdentifier), BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, amountOfMoney), time);
        commandGateway.send(command);
    }

    private void addItems(UserId user, String coinId, BigDecimal amount) {
        PortfolioEntry portfolioEntry = portfolioRepository.findByUserIdentifier(user.toString());
        AddAmountToPortfolioCommand command = new AddAmountToPortfolioCommand(
                new PortfolioId(portfolioEntry.getIdentifier()),
                this.coinId,
                BigMoney.of(CurrencyUnit.of(coinId), amount),
                time);
        commandGateway.send(command);
    }

    private OrderBookEntry obtainOrderBookByCoinName(String coinId) {
        Iterable<CoinEntry> coinEntries = coinRepository.findAll();
        for (CoinEntry entry : coinEntries) {
            if (entry.getPrimaryKey().equals(coinId)) {
                List<OrderBookEntry> orderBookEntries = orderBookRepository.findByCoinIdentifier(entry.getPrimaryKey());
                return orderBookEntries.get(0);
            }
        }
        throw new RuntimeException(String.format("Problem initializing, could not find coin with required name %s.", coinId));
    }


    @Test
    public void testExecuteOneExactOrder() throws Exception {
        BigMoney price = BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(12.456));
        BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of(Currencies.BTC), btcAmount);
        OrderBookEntry orderBookEntry = obtainOrderBookByCoinName(coinId.toString());

        //user1 place buy order 
        PortfolioEntry buyPortfolio = portfolioRepository.findByUserIdentifier(userId1.toString());
        TransactionId buyTransactionId = new TransactionId();
        placeBuyOrder(orderBookEntry, buyPortfolio, tradeAmount.plus(0.01), price, buyTransactionId);

        //user2 place a sell order 
        PortfolioEntry sellPortfolio = portfolioRepository.findByUserIdentifier(userId2.toString());
        TransactionId sellTransactionId = new TransactionId();
        placeSellOrder(orderBookEntry, sellPortfolio, tradeAmount, price, sellTransactionId);


        //sleep to wait for the execution complete 
        TimeUnit.MILLISECONDS.sleep(1000);
        /****************************data***************************************** 
         user1                             after trading           user1 
         money: 100,000                                             money: 99934.487 
         btc: 0                                                     btc: 5.233412
         price: 12.456 
         amount: 5.233412
         commission: 0.5%                                           commission: 65.187 * 0.5% = 0.326
         total: 65.187379872                                        total: 65.513 
         user2                             after trading           user2 
         money: 0                                                   money: 65.187 
         btc: 5.23                                                  btc: 0.00031
         price: 12.456 
         amount: 5.233412
         commission: 0.5%                                            commission: 5.233412 * 0.5% = 0.02616706
         total: 5.25957906                                           total: 5.15907906
         **********************************************************************************/

        //verify data 

        //buyPortfolio 
        buyPortfolio = portfolioRepository.findByUserIdentifier(userId1.toString());
        BigMoney buyerLeftAmount = buyPortfolio.getAmountOfMoney();
        BigMoney buyerLeftItem = buyPortfolio.obtainAmountOfAvailableItemFor(coinId.toString(), CurrencyUnit.of(coinId.toString()));
        assertThat("need CNY 99934.487, but have " + buyerLeftAmount, buyerLeftAmount.compareTo(Money.of(CurrencyUnit.of(Currencies.CNY), 99934.487)), is(0));
        assertThat(buyerLeftItem.compareTo(Money.of(CurrencyUnit.of(Currencies.BTC), 5.233412)), is(0));

        //sellPortfolio 
        sellPortfolio = portfolioRepository.findByUserIdentifier(userId2.toString());
        BigMoney sellerLeftAmount = sellPortfolio.getAmountOfMoney();
        BigMoney sellerLeftItem = sellPortfolio.obtainAmountOfAvailableItemFor(coinId.toString(), CurrencyUnit.of(coinId.toString()));
        assertThat(sellerLeftAmount.compareTo(Money.of(CurrencyUnit.of(Currencies.CNY), 65.187)), is(0));
        assertThat(sellerLeftItem.compareTo(Money.of(CurrencyUnit.of(Currencies.BTC), 0.00031)), is(0));

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
        assertThat(buyOrder.getItemRemaining().isNegativeOrZero(), is(false));
        assertThat(buyOrder.getCurrencyPair(), equalTo(currencyPair));
        assertThat(buyOrder.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(buyOrder.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(buyOrder.getTradeAmount().isEqual(tradeAmount.plus(0.01)), is(true));
        assertThat(buyOrder.getType(), equalTo(OrderType.BUY));
        assertThat(buyOrder.getPortfolioId(), equalTo(buyPortfolio.getPrimaryKey()));

        assertThat(buyOrder.getPlacedDate(), notNullValue());
        assertThat(buyOrder.getLastTradedTime(), notNullValue());
        assertThat(buyOrder.getCompleteDate(), nullValue());
        assertThat(buyOrder.getPlacedDate().compareTo(buyOrder.getLastTradedTime()), lessThanOrEqualTo(0));

        //verify sell order 
        Optional<OrderEntry> sellOrderOptional = Iterables.tryFind(orders, new Predicate<OrderEntry>() {
            @Override
            public boolean apply(OrderEntry input) {
                return input.getType() == OrderType.SELL;
            }
        });

        assertThat(sellOrderOptional.isPresent(), is(true));
        OrderEntry sellOrder = sellOrderOptional.get();
        assertThat(sellOrder.getItemPrice().isEqual(price), is(true));
        assertThat(sellOrder.getItemRemaining().isNegativeOrZero(), is(true));
        assertThat(sellOrder.getCurrencyPair(), equalTo(new CurrencyPair(Currencies.BTC)));
        assertThat(sellOrder.getOrderBookIdentifier(), equalTo(orderBookIdentifier));
        assertThat(sellOrder.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(sellOrder.getTradeAmount().isEqual(tradeAmount), is(true));
        assertThat(sellOrder.getType(), equalTo(OrderType.SELL));
        assertThat(sellOrder.getPortfolioId(), equalTo(sellPortfolio.getPrimaryKey()));

        assertThat(sellOrder.getCompleteDate(), notNullValue());
        assertThat(sellOrder.getPlacedDate(), notNullValue());
        assertThat(sellOrder.getLastTradedTime(), notNullValue());
        assertThat(sellOrder.getPlacedDate().compareTo(sellOrder.getLastTradedTime()), lessThanOrEqualTo(0));

        //order book 
        OrderBookEntry orderBook = obtainOrderBookByCoinName(coinId.toString());

        assertThat(orderBook.getPrimaryKey(), equalTo(orderBookIdentifier));
        assertThat(orderBook.getCurrencyPair(), equalTo(new CurrencyPair(Currencies.BTC)));
        assertThat(orderBook.getBaseCurrency(), equalTo(Constants.CURRENCY_UNIT_BTC));
        assertThat(orderBook.getCounterCurrency(), equalTo(Constants.DEFAULT_CURRENCY_UNIT));
        assertThat(orderBook.getCoinIdentifier(), equalTo(coinId.toString()));

        assertThat(orderBook.getHighestBuyId(), notNullValue());
        assertThat(orderBook.getHighestBuyId(), equalTo(buyOrder.getPrimaryKey()));
        assertThat(orderBook.getHighestBuyPrice().isEqual(buyOrder.getItemPrice()), is(true));
        assertThat(orderBook.getLowestSellId(), nullValue());
        assertThat(orderBook.getLowestSellPrice().isEqual(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, Constants.INIT_SELL_PRICE)), is(true));
        assertThat(orderBook.getLastTradedTime(), equalTo(sellOrder.getLastTradedTime()));
        assertThat(orderBook.getTradedPrice().isEqual(price), is(true));
        assertThat(orderBook.getSellTransactionId(), equalTo(sellTransactionId.toString()));
        assertThat(orderBook.getBuyTransactionId(), equalTo(buyTransactionId.toString()));
    }

    private void placeSellOrder(OrderBookEntry orderBookEntry, PortfolioEntry portfolioEntry, BigMoney tradeAmount, BigMoney price, TransactionId sellTransactionId) {
        StartSellTransactionCommand command = new StartSellTransactionCommand(sellTransactionId,
                coinId,
                currencyPair,
                new OrderBookId(orderBookEntry.getPrimaryKey()),
                new PortfolioId(portfolioEntry.getPrimaryKey()),
                tradeAmount,
                price, time);
        commandGateway.send(command);
    }

    private void placeBuyOrder(OrderBookEntry orderBookEntry, PortfolioEntry portfolioEntry, BigMoney tradeAmount, BigMoney price, TransactionId buyTransactionId) {
        StartBuyTransactionCommand command = new StartBuyTransactionCommand(buyTransactionId,
                coinId,
                currencyPair,
                new OrderBookId(orderBookEntry.getPrimaryKey()),
                new PortfolioId(portfolioEntry.getPrimaryKey()),
                tradeAmount,
                price,
                time);
        commandGateway.send(command);
    }
} 