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

import com.icoin.trading.tradeengine.application.command.coin.CreateCoinCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.DepositCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.AddItemsToPortfolioCommand;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.orderbook.OrderBookEntry;
import com.icoin.trading.tradeengine.query.orderbook.OrderEntry;
import com.icoin.trading.tradeengine.query.orderbook.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeExecutedEntry;
import com.icoin.trading.tradeengine.query.transaction.TransactionEntry;
import com.icoin.trading.users.domain.UserId;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.eventstore.mongo.MongoEventStore;
import org.axonframework.saga.repository.mongo.MongoTemplate;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.application.command.CreateUserCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>Initializes the repository with a number of users, companiess and order books</p>
 *
 * @author Jettro Coenradie
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Component
public class DBInit {

    private CommandBus commandBus;
    private CoinQueryRepository coinRepository;
    private PortfolioQueryRepository portfolioRepository;
    private OrderBookQueryRepository orderBookRepository;
    private org.axonframework.eventstore.mongo.MongoTemplate systemAxonMongo;
    private MongoEventStore eventStore;
    private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;
    private MongoTemplate systemAxonSagaMongo;

    @Autowired
    public DBInit(CommandBus commandBus,
                  CoinQueryRepository coinRepository,
                  org.axonframework.eventstore.mongo.MongoTemplate systemMongo,
                  MongoEventStore eventStore,
                  org.springframework.data.mongodb.core.MongoTemplate mongoTemplate,
                  MongoTemplate systemAxonSagaMongo,
                  PortfolioQueryRepository portfolioRepository,
                  OrderBookQueryRepository orderBookRepository) {
        this.commandBus = commandBus;
        this.coinRepository = coinRepository;
        this.systemAxonMongo = systemMongo;
        this.eventStore = eventStore;
        this.mongoTemplate = mongoTemplate;
        this.systemAxonSagaMongo = systemAxonSagaMongo;
        this.portfolioRepository = portfolioRepository;
        this.orderBookRepository = orderBookRepository;
    }

    public void createItems() {
        systemAxonMongo.domainEventCollection().drop();
        systemAxonMongo.snapshotEventCollection().drop();

        systemAxonSagaMongo.sagaCollection().drop();

        mongoTemplate.dropCollection(UserEntry.class);
        mongoTemplate.dropCollection(OrderBookEntry.class);
        mongoTemplate.dropCollection(OrderEntry.class);
        mongoTemplate.dropCollection(CoinEntry.class);
        mongoTemplate.dropCollection(TradeExecutedEntry.class);
        mongoTemplate.dropCollection(PortfolioEntry.class);
        mongoTemplate.dropCollection(TransactionEntry.class);

        UserId buyer1 = createuser("Buyer One", "buyer1");
        UserId buyer2 = createuser("Buyer two", "buyer2");
        UserId buyer3 = createuser("Buyer three", "buyer3");
        UserId buyer4 = createuser("Buyer four", "buyer4");
        UserId buyer5 = createuser("Buyer five", "buyer5");
        UserId buyer6 = createuser("Buyer six", "buyer6");

        createCompanies(buyer1);

        addMoney(buyer1, BigDecimal.valueOf(100000));
        addItems(buyer2, "Philips", BigDecimal.valueOf(10000l));
        addMoney(buyer3, BigDecimal.valueOf(100000));
        addItems(buyer4, "Shell", BigDecimal.valueOf(10000l));
        addMoney(buyer5, BigDecimal.valueOf(100000));
        addItems(buyer6, "Bp", BigDecimal.valueOf(100000));

        eventStore.ensureIndexes();
    }

    private void addItems(UserId user, String coinName, BigDecimal amount) {
        PortfolioEntry portfolioEntry = portfolioRepository.findByUserIdentifier(user.toString());
        OrderBookEntry orderBookEntry = obtainOrderBookByCoinName(coinName);
        AddItemsToPortfolioCommand command = new AddItemsToPortfolioCommand(
                new PortfolioId(portfolioEntry.getIdentifier()),
                new OrderBookId(orderBookEntry.getIdentifier()),
                amount);
        commandBus.dispatch(new GenericCommandMessage<AddItemsToPortfolioCommand>(command));
    }

    private OrderBookEntry obtainOrderBookByCoinName(String coinName) {
        Iterable<CoinEntry> coinEntries = coinRepository.findAll();
        for (CoinEntry entry : coinEntries) {
            if (entry.getName().equals(coinName)) {
                List<OrderBookEntry> orderBookEntries = orderBookRepository
                        .findByCoinIdentifier(entry.getIdentifier());

                return orderBookEntries.get(0);
            }
        }
        throw new RuntimeException("Problem initializing, could not find coin with required name.");
    }

    private void addMoney(UserId buyer1, BigDecimal amount) {
        PortfolioEntry portfolioEntry = portfolioRepository.findByUserIdentifier(buyer1.toString());
        depositMoneyToPortfolio(portfolioEntry.getIdentifier(), amount);
    }

    public void depositMoneyToPortfolio(String portfolioIdentifier, BigDecimal amountOfMoney) {
        DepositCashCommand command =
                new DepositCashCommand(new PortfolioId(portfolioIdentifier), amountOfMoney);
        commandBus.dispatch(new GenericCommandMessage<DepositCashCommand>(command));
    }


    private void createCompanies(UserId userIdentifier) {
        CreateCoinCommand command = new CreateCoinCommand(new CoinId(), userIdentifier, "Philips", BigDecimal.valueOf(1000), BigDecimal.valueOf(10000));
        commandBus.dispatch(new GenericCommandMessage<CreateCoinCommand>(command));

        command = new CreateCoinCommand(new CoinId(), userIdentifier, "Shell", BigDecimal.valueOf(500), BigDecimal.valueOf(5000));
        commandBus.dispatch(new GenericCommandMessage<CreateCoinCommand>(command));

        command = new CreateCoinCommand(new CoinId(), userIdentifier, "Bp", BigDecimal.valueOf(15000), BigDecimal.valueOf(100000));
        commandBus.dispatch(new GenericCommandMessage<CreateCoinCommand>(command));

//        To bo used for performance tests
//        for (int i=0; i < 1000; i++) {
//            command = new CreateCoinCommand(userIdentifier, "Stock " + i, 15000, 100000);
//            commandBus.dispatch(command);
//        }

    }

    private UserId createuser(String longName, String userName) {
        UserId userId = new UserId();
        CreateUserCommand createUser = new CreateUserCommand(userId, longName, userName, userName);
        commandBus.dispatch(new GenericCommandMessage<CreateUserCommand>(createUser));
        return userId;
    }
}
