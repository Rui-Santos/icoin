package com.icoin.trading.tradeengine.query.activity.listeners;

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.events.portfolio.cash.CashDepositedEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.cash.CashWithdrawnEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.coin.ItemAddedToPortfolioEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.coin.ItemWithdrawnEvent;
import com.icoin.trading.tradeengine.query.activity.Activity;
import com.icoin.trading.tradeengine.query.activity.ActivityItem;
import com.icoin.trading.tradeengine.query.activity.PortfolioActivity;
import com.icoin.trading.tradeengine.query.activity.PortfolioActivityType;
import com.icoin.trading.tradeengine.query.activity.repositories.PortfolioActivityQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/26/14
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class PortfolioActivityListener {
    private final static Logger logger = LoggerFactory.getLogger(PortfolioActivityListener.class);

    private PortfolioActivityQueryRepository portfolioActivityRepository;
    private PortfolioQueryRepository portfolioRepository;

    @EventHandler
    public void handleEvent(ItemAddedToPortfolioEvent event) {
        handler(event.getPortfolioIdentifier().toString(),
                PortfolioActivityType.ADD_COIN,
                event.getCoinId(),
                event.getAmountOfItemAdded(),
                event.getTime());
    }


    //ItemAddedToPortfolioEvent
    @EventHandler
    public void handleEvent(ItemWithdrawnEvent event) {
        handler(event.getPortfolioIdentifier().toString(),
                PortfolioActivityType.WITHDRAW_COIN,
                event.getCoinId(),
                event.getAmount(),
                event.getWithdrawnTime());
    }

    @EventHandler
    public void handleEvent(CashDepositedEvent event) {
        handler(event.getPortfolioIdentifier().toString(),
                PortfolioActivityType.ADD_MONEY,
                null,
                event.getMoneyAdded(),
                event.getTime());
    }

    @EventHandler
    public void handleEvent(CashWithdrawnEvent event) {
        handler(event.getPortfolioIdentifier().toString(),
                PortfolioActivityType.WITHDRAW_MONEY,
                null,
                event.getAmountPaid(),
                event.getWithdrawnTime());
    }

    private void handler(String portfolioId,
                         PortfolioActivityType type,
                         CoinId coinId,
                         BigMoney amount,
                         Date time) {
        notNull(portfolioId);

        PortfolioActivity portfolioActivity =
                portfolioActivityRepository.findByPortfolioId(portfolioId, type);

        if (portfolioActivity == null) {
            create(portfolioId,
                    type,
                    coinId,
                    amount,
                    time);

            return;
        }

        portfolioActivity.addActivityItem(time,
                new ActivityItem(time, null, null, amount));

        portfolioActivityRepository.save(portfolioActivity);
    }

    private PortfolioActivity create(String portfolioId,
                                     PortfolioActivityType type,
                                     CoinId coinId,
                                     BigMoney amountOfItemAdded,
                                     Date time) {
        PortfolioEntry portfolioEntry = portfolioRepository.findOne(portfolioId);

        if (portfolioEntry == null) {
            logger.error("Cannot find portfolio with {}", portfolioId);
        }


        final Activity activity = new Activity();

        activity.addItems(time, new ActivityItem(time, null, null, amountOfItemAdded));


        final String userIdentifier = portfolioEntry == null ? null : portfolioEntry.getUserIdentifier();
        final String username = portfolioEntry == null ? null : portfolioEntry.getUsername();


        PortfolioActivity portfolioActivity = new PortfolioActivity(userIdentifier,
                username, portfolioId, type, activity);
        portfolioActivityRepository.save(portfolioActivity);

        return portfolioActivity;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPortfolioActivityRepository(PortfolioActivityQueryRepository portfolioActivityRepository) {
        this.portfolioActivityRepository = portfolioActivityRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPortfolioRepository(PortfolioQueryRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }
}
