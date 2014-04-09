/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icoin.trading.webui.init;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.command.admin.EnsureCqrsIndexesCommand;
import com.icoin.trading.api.tradeengine.command.admin.ReinitializeOrderBookTradingExecutorsCommand;
import com.icoin.trading.api.tradeengine.command.admin.ReinstallDataBaseCommand;
import com.icoin.trading.api.tradeengine.command.coin.CreateCoinCommand;
import com.icoin.trading.api.tradeengine.command.portfolio.cash.DepositCashCommand;
import com.icoin.trading.api.tradeengine.command.portfolio.coin.AddAmountToPortfolioCommand;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.users.command.CreateUserCommand;
import com.icoin.trading.api.users.domain.Identifier;
import com.icoin.trading.api.users.domain.UserId;
import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>Initializes the repository with a number of users, coins and order books</p>
 *
 * @author Jettro Coenradie
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Component
public class SystemInit {
    private static Logger logger = LoggerFactory.getLogger(SystemInit.class);

    private CommandGateway commandGateway;
    private CoinQueryRepository coinRepository;
    private PortfolioQueryRepository portfolioRepository;
    private OrderBookQueryRepository orderBookRepository;

    @Autowired
    public SystemInit(CommandGateway commandGateway,
                      CoinQueryRepository coinRepository,
                      @Qualifier("trade.mongoTemplate")
                      org.springframework.data.mongodb.core.MongoTemplate mongoTemplate,
                      PortfolioQueryRepository portfolioRepository,
                      OrderBookQueryRepository orderBookRepository) {
        this.commandGateway = commandGateway;
        this.coinRepository = coinRepository;
        this.portfolioRepository = portfolioRepository;
        this.orderBookRepository = orderBookRepository;
    }

    public void reinstallDB() {
        logger.info("Reinstalling the collections ..");
        commandGateway.sendAndWait(new ReinstallDataBaseCommand());
        logger.info("Reinstalled the collections.");
    }

    public void ensureCqrsIndexes() {
        logger.info("Building the cqrs framework index.");
        commandGateway.sendAndWait(new EnsureCqrsIndexesCommand());
        logger.info("Indexes rebuilt.");
    }

    public void reinitializeTradingExecutors() {
        logger.info("Reinitializing Trading Executors ..");
        commandGateway.sendAndWait(new ReinitializeOrderBookTradingExecutorsCommand());
        logger.info("Reinitialized Trading Executors.");
    }

    public void createItems() {
        reinstallDB();

        UserId buyer1 = createUser("Buyer One", "buyer1");
        UserId buyer2 = createUser("Buyer two", "buyer2");
        UserId buyer3 = createUser("Buyer three", "buyer3");
        UserId buyer4 = createUser("Buyer four", "buyer4");
        UserId buyer5 = createUser("Buyer five", "buyer5");
        UserId buyer6 = createUser("Buyer six", "buyer6");

        createCoins();

//        try {
//            TimeUnit.MILLISECONDS.sleep(500L);
//        } catch (InterruptedException e) {
//            logger.error("System initialization interrupted!");
//            throw new IZookeyException("System initialization interrupted!");
//        }

        addMoney(buyer1, BigDecimal.valueOf(100000));
        addItems(buyer1, "BTC", BigDecimal.valueOf(10000l));
        addItems(buyer2, "BTC", BigDecimal.valueOf(10000l));
        addMoney(buyer3, BigDecimal.valueOf(100000));
        addItems(buyer4, "BTC", BigDecimal.valueOf(10000l));
        addMoney(buyer5, BigDecimal.valueOf(100000));
        addItems(buyer6, "LTC", BigDecimal.valueOf(100000));

        ensureCqrsIndexes();
        reinitializeTradingExecutors();
    }

    private void addItems(UserId user, String coinId, BigDecimal amount) {
        logger.info("to find user id {}", user);
        PortfolioEntry portfolioEntry = portfolioRepository.findByUserIdentifier(user.toString());
        logger.info("to find user id {}", user);
        AddAmountToPortfolioCommand command = new AddAmountToPortfolioCommand(
                new PortfolioId(portfolioEntry.getIdentifier()),
                new CoinId(coinId),
                BigMoney.of(CurrencyUnit.of(coinId), amount));
        commandGateway.send(command);
    }

    private OrderBookEntry obtainOrderBookByCoinName(String coinId) {
        Iterable<CoinEntry> coinEntries = coinRepository.findAll();
        for (CoinEntry entry : coinEntries) {
            if (entry.getPrimaryKey().equals(coinId)) {
                List<OrderBookEntry> orderBookEntries = orderBookRepository
                        .findByCoinIdentifier(entry.getPrimaryKey());

                return orderBookEntries.get(0);
            }
        }
        throw new RuntimeException(String.format("Problem initializing, could not find coin with required name %s.", coinId));
    }

    private void addMoney(UserId buyer1, BigDecimal amount) {
        PortfolioEntry portfolioEntry = portfolioRepository.findByUserIdentifier(buyer1.toString());
        depositMoneyToPortfolio(portfolioEntry.getIdentifier(), amount);
    }

    public void depositMoneyToPortfolio(String portfolioIdentifier, BigDecimal amountOfMoney) {
        DepositCashCommand command =
                new DepositCashCommand(new PortfolioId(portfolioIdentifier), BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, amountOfMoney));
        commandGateway.send(command);
    }


    private void createCoins() {
        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(10025.341));
        BigMoney.of(Constants.CURRENCY_UNIT_BTC, BigDecimal.valueOf(10000));
        CreateCoinCommand command = new CreateCoinCommand(
                new CoinId(Currencies.BTC),
                "Bitcoin",
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(10025.341)),
                BigMoney.of(Constants.CURRENCY_UNIT_BTC, BigDecimal.valueOf(10000)));
        commandGateway.send(command);

        command = new CreateCoinCommand(
                new CoinId(Currencies.LTC),
                "Litecoin",
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(1026.341)),
                BigMoney.of(Constants.CURRENCY_UNIT_LTC, BigDecimal.valueOf(10000)));
        commandGateway.send(command);

        command = new CreateCoinCommand(new CoinId(Currencies.PPC), "Peercoin",
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(66.341)),
                BigMoney.of(Constants.CURRENCY_UNIT_PPC, BigDecimal.valueOf(5000)));
        commandGateway.send(command);

        command = new CreateCoinCommand(new CoinId(Currencies.XPM), "Primecoin",
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(56.341)),
                BigMoney.of(Constants.CURRENCY_UNIT_XPM, BigDecimal.valueOf(10000)));
        commandGateway.send(command);
    }

    private UserId createUser(String longName, String username) {
        UserId userId = new UserId();
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");
        CreateUserCommand command =
                new CreateUserCommand(userId,
                        username,
                        longName,
                        longName,
                        identifier,
                        username + "@163.com",
                        username,
                        username,
                        ImmutableList.of("ROLE_USER", "ROLE_ADMIN"));
        commandGateway.sendAndWait(command);
        return userId;
    }
}
