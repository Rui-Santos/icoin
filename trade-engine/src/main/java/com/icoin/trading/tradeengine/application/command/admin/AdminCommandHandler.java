package com.icoin.trading.tradeengine.application.command.admin;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeExecutedEntry;
import com.icoin.trading.tradeengine.query.transaction.TransactionEntry;
import com.icoin.trading.users.query.UserEntry;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventstore.mongo.MongoEventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/27/13
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AdminCommandHandler {
    private static List<Class> queryEntityClasses = ImmutableList.<Class>of(
            UserEntry.class,
            OrderBookEntry.class,
            OrderEntry.class,
            CoinEntry.class,
            TradeExecutedEntry.class,
            PortfolioEntry.class,
            TransactionEntry.class,
            Order.class);

    private MongoEventStore eventStore;

    private MongoTemplate mongoTemplate;
    private org.axonframework.saga.repository.mongo.MongoTemplate systemAxonSagaMongo;
    private org.axonframework.eventstore.mongo.MongoTemplate systemAxonMongo;

    @CommandHandler
    public void handleReInstallDataBase(ReinstallDataBaseCommand command) {
        dropCqrsInfraTables();

        dropQueryTables();
    }

     /**
     * this is to ensure event store relating collection indexes. In case that the collections
     * are not created at very first, it should be invoked after some events generated in.
     * @param command
     * */
    @CommandHandler
    public void handleEnsureCqrsIndexes(EnsureCqrsIndexesCommand command) {
        eventStore.ensureIndexes();
    }
    
    @CommandHandler
    public void handleReinitializeOrderBookTradingExecutors(ReinitializeOrderBookTradingExecutorsCommand command) {
       tradeExecutor.reinitialize();
    }

    private void dropQueryTables() {
        for (Class queryEntityClass : queryEntityClasses) {
            mongoTemplate.dropCollection(queryEntityClass);
        }
    }

    private void dropCqrsInfraTables() {
        systemAxonMongo.domainEventCollection().drop();
        systemAxonMongo.snapshotEventCollection().drop();
        systemAxonSagaMongo.sagaCollection().drop();
    }

    @Autowired
    public void setEventStore(MongoEventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Resource(name = "trade.mongoTemplate")
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Autowired
    public void setSystemAxonSagaMongo(org.axonframework.saga.repository.mongo.MongoTemplate systemAxonSagaMongo) {
        this.systemAxonSagaMongo = systemAxonSagaMongo;
    }

    @Autowired
    public void setSystemAxonMongo(org.axonframework.eventstore.mongo.MongoTemplate systemAxonMongo) {
        this.systemAxonMongo = systemAxonMongo;
    }
    
    @Autowired
    public void setTradeExecutor(TradeExecutor tradeExecutor) {
        this.tradeExecutor = tradeExecutor;
    }
}
