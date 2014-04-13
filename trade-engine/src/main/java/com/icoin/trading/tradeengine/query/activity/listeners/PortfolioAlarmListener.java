package com.icoin.trading.tradeengine.query.activity.listeners;

import com.homhon.base.domain.Specification;
import com.icoin.money.specification.GreaterOrEqualSpecification;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.events.portfolio.cash.CashDepositedEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.cash.CashWithdrawnEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.coin.ItemAddedToPortfolioEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.coin.ItemWithdrawnEvent;
import com.icoin.trading.tradeengine.query.activity.PortfolioAlarmActivity;
import com.icoin.trading.tradeengine.query.activity.PortfolioAlarmType;
import com.icoin.trading.tradeengine.query.activity.repositories.PortfolioAlarmQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
public class PortfolioAlarmListener {
    private final static Logger logger = LoggerFactory.getLogger(PortfolioAlarmListener.class);

    private PortfolioAlarmQueryRepository portfolioAlarmRepository;
    private PortfolioQueryRepository portfolioRepository;
    private BigDecimal highestAmountThreshold;
    private BigDecimal highestMoneyThreshold;

    @EventHandler
    public void handleEvent(ItemAddedToPortfolioEvent event) {
        notNull(event.getAmountOfItemAdded());
        final CurrencyUnit amountCurrency = event.getAmountOfItemAdded().getCurrencyUnit();
        final BigMoney threshold = BigMoney.of(amountCurrency, highestAmountThreshold);

        handler(event.getPortfolioIdentifier().toString(),
                PortfolioAlarmType.ADD_LARGE_AMOUNT_OF_COIN,
                event.getCoinId(),
                event.getAmountOfItemAdded(),
                threshold,
                event.getTime());
    }

    @EventHandler
    public void handleEvent(ItemWithdrawnEvent event) {
        notNull(event.getAmount());
        final CurrencyUnit amountCurrency = event.getAmount().getCurrencyUnit();
        final BigMoney threshold = BigMoney.of(amountCurrency, highestAmountThreshold);

        handler(event.getPortfolioIdentifier().toString(),
                PortfolioAlarmType.WITHDRAW_LARGE_AMOUNT_OF_COIN,
                event.getCoinId(),
                event.getAmount(),
                threshold,
                event.getWithdrawnTime());
    }

    @EventHandler
    public void handleEvent(CashDepositedEvent event) {
        notNull(event.getMoneyAdded());
        final CurrencyUnit amountCurrency = event.getMoneyAdded().getCurrencyUnit();
        final BigMoney threshold = BigMoney.of(amountCurrency, highestMoneyThreshold);

        handler(event.getPortfolioIdentifier().toString(),
                PortfolioAlarmType.ADD_LARGE_AMOUNT_OF_MONEY,
                null,
                event.getMoneyAdded(),
                threshold,
                event.getTime());
    }

    @EventHandler
    public void handleEvent(CashWithdrawnEvent event) {
        notNull(event.getAmountPaid());
        final CurrencyUnit amountCurrency = event.getAmountPaid().getCurrencyUnit();
        final BigMoney threshold = BigMoney.of(amountCurrency, highestMoneyThreshold);

        handler(event.getPortfolioIdentifier().toString(),
                PortfolioAlarmType.WITHDRAW_LARGE_AMOUNT_OF_MONEY,
                null,
                event.getAmountPaid(),
                threshold,
                event.getWithdrawnTime());
    }

    private void handler(String portfolioId,
                         PortfolioAlarmType type,
                         CoinId coinId,
                         BigMoney amount,
                         BigMoney threshold,
                         Date time) {
        notNull(portfolioId);
        notNull(threshold);
        notNull(amount);


        final Specification<BigMoney> moneyCheck =
                new GreaterOrEqualSpecification(threshold);

        if (!moneyCheck.isSatisfiedBy(amount)) {
            if (logger.isDebugEnabled()) {
                logger.debug("No need to alert amount {} given threshold {} ", amount, threshold);
            }
            return;
        }

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(portfolioId);

        if (portfolioEntry == null) {
            logger.error("Cannot find portfolio with {}", portfolioId);
        }


        PortfolioAlarmActivity activity = new PortfolioAlarmActivity();

        activity.setPortfolioId(portfolioId);
        activity.setUserId(portfolioEntry == null ? null : portfolioEntry.getUserIdentifier());
        activity.setUsername(portfolioEntry == null ? null : portfolioEntry.getUsername());
        activity.setTime(time);
        activity.setType(type);


        logger.info("portfolioId {} has {} alarm {}: {}", portfolioId, type, portfolioEntry == null ? null : portfolioEntry.describe());
        portfolioAlarmRepository.save(activity);
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPortfolioAlarmRepository(PortfolioAlarmQueryRepository portfolioAlarmRepository) {
        this.portfolioAlarmRepository = portfolioAlarmRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPortfolioRepository(PortfolioQueryRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Value("${trading.highest.alarm.coin.amount}")
    public void setHighestAmountThreshold(BigDecimal highestAmountThreshold) {
        this.highestAmountThreshold = highestAmountThreshold;
    }

    @Value("${trading.highest.alarm.money}")
    public void setHighestMoneyThreshold(BigDecimal highestMoneyThreshold) {
        this.highestMoneyThreshold = highestMoneyThreshold;
    }
} 