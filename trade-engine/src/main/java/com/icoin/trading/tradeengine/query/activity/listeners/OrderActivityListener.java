package com.icoin.trading.tradeengine.query.activity.listeners;

import com.icoin.trading.tradeengine.domain.events.portfolio.PortfolioCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashDepositedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashWithdrawnEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemAddedToPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationCancelledForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationConfirmedForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.query.activity.Activity;
import com.icoin.trading.tradeengine.query.activity.PortfolioActivity;
import com.icoin.trading.tradeengine.query.activity.repositories.PortfolioActivityQueryRepository;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/26/14
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OrderActivityListener {
    private final static Logger logger = LoggerFactory.getLogger(OrderActivityListener.class);

    private PortfolioActivityQueryRepository portfolioActivityRepository;
    private PortfolioQueryRepository portfolioRepository;
    private OrderQueryRepository orderRepository;
    private UserQueryRepository userQueryRepository;


    @EventHandler
    public void handleEvent(PortfolioCreatedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("About to handle the PortfolioCreatedEvent for user with primaryKey {}, portfolio id {}",
                    event.getUserId(), event.getPortfolioId());
        }

//        WITHDRAW_COIN,
//                ADD_COIN,
//                WITHDRAW_MONEY,
//                ADD_MONEY,
//                BUY_ORDER_ACTIVITY,
//                SELL_ORDER_ACTIVITY,
//                WITHDRAW_LARGE_AMOUNT_OF_MONEY,
//                ADD_LARGE_AMOUNT_OF_MONEY,
//                WITHDRAW_LARGE_AMOUNT_OF_COIN,
//                ADD_LARGE_AMOUNT_OF_COIN;

        PortfolioActivity portfolioActivity = new PortfolioActivity();
        portfolioActivity.setPortfolioId(event.getPortfolioId().toString());
        portfolioActivity.setActivity(new Activity());
        final UserEntry user = userQueryRepository.findOne(event.getUserId().toString());
        portfolioActivity.setUsername(user.getUsername());
        portfolioActivity.setFullName(user.getFullName());
        portfolioActivity.setType(user.getFullName());
        portfolioActivity.setUserId(user.getPrimaryKey());

        portfolioActivityRepository.save(portfolioActivity);
    }


    @EventHandler
    public void handleEvent(ItemAddedToPortfolioEvent event) {
        logger.debug("Handle ItemAddedToPortfolioEvent {} for coin {} with amount {}", event.getPortfolioIdentifier(),
                event.getCoinId(), event.getAmountOfItemAdded());
        CoinEntry coin = findCoinEntry(event.getCoinId());

        if (coin == null) {
            logger.error("coin {} cannot be found.", event.getCoinId());
            return;
        }

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());

        if (!portfolioEntry.hasItem(event.getCoinId().toString())) {
            portfolioEntry.createItem(coin.getPrimaryKey(), coin.getName());
        }

        portfolioEntry.addItemInPossession(coin.getPrimaryKey(), event.getAmountOfItemAdded());

        portfolioRepository.save(portfolioEntry);
    }

    private CoinEntry findCoinEntry(CoinId coinId) {
        return coinQueryRepository.findOne(coinId.toString());
    }

    @EventHandler
    public void handleEvent(ItemReservationCancelledForPortfolioEvent event) {
        logger.debug("Handle ItemReservationCancelledForPortfolioEvent {} for coin {}, left commission {}, left total item {}", event.getPortfolioIdentifier(),
                event.getCoinId(), event.getLeftCommission(), event.getLeftTotalItem());
        CoinEntry coin = findCoinEntry(event.getCoinId());

        if (coin == null) {
            logger.error("coin {} cannot be found.", event.getCoinId());
            return;
        }

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.cancelReserved(coin.getPrimaryKey(), event.getLeftTotalItem().plus(event.getLeftCommission()));

        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(ItemReservationConfirmedForPortfolioEvent event) {
        logger.debug("Handle ItemReservationConfirmedForPortfolioEvent {} for coin {}, amount {}, commission {}", event.getPortfolioIdentifier(),
                event.getCoinId(), event.getAmount(), event.getCommission());
        CoinEntry coin = findCoinEntry(event.getCoinId());

        if (coin == null) {
            logger.error("coin {} cannot be found.", event.getCoinId());
            return;
        }

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.confirmReserved(event.getCoinId().toString(), event.getAmount().plus(event.getCommission()));

        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(ItemReservedEvent event) {
        logger.debug("Handle ItemReservedEvent {} for coin {}, amount {}", event.getPortfolioIdentifier(), event.getCoinId(), event.getAmountOfItemReserved());
        final CoinEntry coin = findCoinEntry(event.getCoinId());

        if (coin == null) {
            logger.error("coin {} cannot be found.", event.getCoinId());
            return;
        }

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.addReserved(coin.getPrimaryKey(), event.getAmountOfItemReserved());

        portfolioRepository.save(portfolioEntry);
    }


    @EventHandler
    public void handleEvent(CashDepositedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Handle CashDepositedEvent {} to add money {} ",
                    event.getPortfolioIdentifier(), event.getMoneyAdded());
        }
        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.setAmountOfMoney(portfolioEntry.getAmountOfMoney().plus(event.getMoneyAdded()));
        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(CashWithdrawnEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Handle CashWithdrawnEvent {} to withdraw money {} ",
                    event.getPortfolioIdentifier(), event.getAmountPaid());
        }
        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.setAmountOfMoney(portfolioEntry.getAmountOfMoney().minus(event.getAmountPaid()));
        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(CashReservedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Handle CashReservedEvent {} to withdraw money {} '+' commission {}",
                    event.getPortfolioIdentifier(), event.getTotalMoney(), event.getTotalCommission());
        }

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        final BigMoney total = event.getTotalMoney().plus(event.getTotalCommission());
        portfolioEntry.setReservedAmountOfMoney(portfolioEntry.getReservedAmountOfMoney().plus(total));
        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(CashReservationCancelledEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Handle CashReservationCancelledEvent {} with left money {} '+' left commission {}",
                    event.getPortfolioIdentifier(), event.getLeftTotalMoney(), event.getLeftCommission());
        }
        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        final BigMoney totalLeft = event.getLeftTotalMoney().plus(event.getLeftCommission());
        portfolioEntry.setReservedAmountOfMoney(
                portfolioEntry.getReservedAmountOfMoney().minus(totalLeft));

//        portfolioEntry.setAmountOfMoney(portfolioEntry.getAmountOfMoney().plus(totalLeft));
        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(CashReservationConfirmedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Handle CashReservationConfirmedEvent {} with money {} '+'  commission {}",
                    event.getPortfolioIdentifier(), event.getAmountOfMoney(), event.getCommission());
        }
        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        BigMoney reservedAmountOfMoney = portfolioEntry.getReservedAmountOfMoney();
        BigMoney amountOfMoneyConfirmed = event.getAmountOfMoney().plus(event.getCommission());
        if (amountOfMoneyConfirmed.compareTo(reservedAmountOfMoney) < 0) {
            portfolioEntry.setReservedAmountOfMoney(reservedAmountOfMoney.minus(amountOfMoneyConfirmed));
        } else {
            portfolioEntry.setReservedAmountOfMoney(BigMoney.zero(event.getAmountOfMoney().getCurrencyUnit()));
        }

        portfolioEntry.setAmountOfMoney(portfolioEntry.getAmountOfMoney().minus(amountOfMoneyConfirmed));
        portfolioRepository.save(portfolioEntry);
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


    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setOrderRepository(OrderQueryRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setUserQueryRepository(UserQueryRepository userQueryRepository) {
        this.userQueryRepository = userQueryRepository;
    }
}