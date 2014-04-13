package com.icoin.trading.tradeengine.query.activity.listeners;

import com.homhon.base.domain.Specification;
import com.homhon.util.Strings;
import com.icoin.money.specification.GreaterOrEqualSpecification;
import com.icoin.money.specification.LessOrEqualSpecification;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.query.activity.ExecutedAlarmActivity;
import com.icoin.trading.tradeengine.query.activity.ExecutedAlarmType;
import com.icoin.trading.tradeengine.query.activity.repositories.ExecutedAlarmActivityQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeType;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/26/14
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ExecutedAlarmActivityListener {
    private final static Logger logger = LoggerFactory.getLogger(ExecutedAlarmActivityListener.class);

    private ExecutedAlarmActivityQueryRepository executedAlarmActivityRepository;
    private PortfolioQueryRepository portfolioRepository;

    private BigDecimal highestPriceThreshold;
    private BigDecimal lowestPriceThreshold;
    private BigDecimal highestAmountThreshold;
    private BigDecimal lowestAmountThreshold;

    private BigDecimal highestMoneyThreshold;
    private BigDecimal lowestMoneyThreshold;

    @EventHandler
    public void handleTradeExecuted(TradeExecutedEvent event) {
        notNull(event.getTradedPrice());
        notNull(event.getTradeAmount());
        notNull(event.getExecutedMoney());

        notNull(event.getBuyOrderId());
        notNull(event.getSellOrderId());

        final CurrencyUnit priceCurrency = event.getTradedPrice().getCurrencyUnit();
        final CurrencyUnit amountCurrency = event.getTradeAmount().getCurrencyUnit();
        final CurrencyUnit moneyCurrency = event.getExecutedMoney().getCurrencyUnit();

        final Specification<BigMoney> moneyCheck = new LessOrEqualSpecification(BigMoney.of(moneyCurrency, lowestMoneyThreshold))
                .or(new GreaterOrEqualSpecification(BigMoney.of(moneyCurrency, highestMoneyThreshold)));


        final Specification<BigMoney> priceCheck = new LessOrEqualSpecification(BigMoney.of(priceCurrency, lowestPriceThreshold))
                .or(new GreaterOrEqualSpecification(BigMoney.of(priceCurrency, highestPriceThreshold)));

        final Specification<BigMoney> amountCheck = new LessOrEqualSpecification(BigMoney.of(amountCurrency, lowestAmountThreshold))
                .or(new GreaterOrEqualSpecification(BigMoney.of(amountCurrency, highestAmountThreshold)));

        ExecutedAlarmType type = null;

        if (moneyCheck.isSatisfiedBy(event.getExecutedMoney())) {
            type = ExecutedAlarmType.MONEY;
        }

        if (type == null && amountCheck.isSatisfiedBy(event.getTradeAmount())) {
            type = ExecutedAlarmType.AMOUNT;
        }

        if (type == null && priceCheck.isSatisfiedBy(event.getTradedPrice())) {
            type = ExecutedAlarmType.PRICE;
        }

        if (type == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("this event seems normal: {}", event);
            }
            return;
        }

//        final OrderEntry sellOrder = orderRepository.findOne(event.getSellOrderId());
//        if (sellOrder == null) {
//            logger.error("cannot found sellorder for suspicious executed activity: {}", event);
//        }
//
//        final OrderEntry buyOrder = orderRepository.findOne(event.getBuyOrderId());
//        if (buyOrder == null) {
//            logger.error("cannot found buyOrder for suspicious executed activity: {}", event);
//        }

        final PortfolioEntry sellPortfolio = getPortfolio(event.getSellPortfolioId());
        if (sellPortfolio == null) {
            logger.error("cannot found sellPortfolio for suspicious executed activity: {}", event);
        }

        final PortfolioEntry buyPortfolio = getPortfolio(event.getBuyPortfolioId());
        if (buyPortfolio == null) {
            logger.error("cannot found buyPortfolio for suspicious executed activity: {}", event);
        }

        ExecutedAlarmActivity alarm = createAlarm(event, type, sellPortfolio, buyPortfolio);

        executedAlarmActivityRepository.save(alarm);
    }

    private ExecutedAlarmActivity createAlarm(TradeExecutedEvent event, ExecutedAlarmType type, PortfolioEntry sellPortfolio, PortfolioEntry buyPortfolio) {
        final ExecutedAlarmActivity activity = new ExecutedAlarmActivity();

        activity.setBuyOrderId(event.getBuyOrderId());
        activity.setSellOrderId(event.getSellOrderId());
        activity.setBuyTransactionId(event.getBuyTransactionId().toString());
        activity.setSellTransactionId(event.getSellTransactionId().toString());
        activity.setCoinId(event.getCoinId().toString());
        activity.setOrderBookIdentifier(event.getOrderBookId().toString());
        activity.setTradedAmount(event.getTradeAmount());
        activity.setExecutedMoney(event.getExecutedMoney());
        activity.setTradedPrice(event.getTradedPrice());
        activity.setTradeTime(event.getTradeTime());
        activity.setType(type);
        activity.setTradeType(TradeType.convert(event.getTradeType()));

        activity.setSellPortfolioId(event.getSellPortfolioId().toString());
        activity.setSellUsername(sellPortfolio == null ? null : sellPortfolio.getUsername());
        activity.setBuyPortfolioId(event.getBuyPortfolioId().toString());
        activity.setBuyUsername(buyPortfolio == null ? null : buyPortfolio.getUsername());

        return activity;
    }


    private PortfolioEntry getPortfolio(PortfolioId portfolioId) {
        if (portfolioId == null || !Strings.hasText(portfolioId.toString())) {
            return null;
        }

        return portfolioRepository.findOne(portfolioId.toString());
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setExecutedAlarmActivityRepository(ExecutedAlarmActivityQueryRepository executedAlarmActivityRepository) {
        this.executedAlarmActivityRepository = executedAlarmActivityRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPortfolioRepository(PortfolioQueryRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Value("${trading.highest.alarm.price}")
    public void setHighestPriceThreshold(BigDecimal highestPriceThreshold) {
        this.highestPriceThreshold = highestPriceThreshold;
    }

    @Value("${trading.lowest.alarm.price}")
    public void setLowestPriceThreshold(BigDecimal lowestPriceThreshold) {
        this.lowestPriceThreshold = lowestPriceThreshold;
    }

    @Value("${trading.highest.alarm.coin.amount}")
    public void setHighestAmountThreshold(BigDecimal highestAmountThreshold) {
        this.highestAmountThreshold = highestAmountThreshold;
    }

    @Value("${trading.lowest.alarm.coin.amount}")
    public void setLowestAmountThreshold(BigDecimal lowestAmountThreshold) {
        this.lowestAmountThreshold = lowestAmountThreshold;
    }

    @Value("${trading.highest.alarm.money}")
    public void setHighestMoneyThreshold(BigDecimal highestMoneyThreshold) {
        this.highestMoneyThreshold = highestMoneyThreshold;
    }

    @Value("${trading.lowest.alarm.money}")
    public void setLowestMoneyThreshold(BigDecimal lowestMoneyThreshold) {
        this.lowestMoneyThreshold = lowestMoneyThreshold;
    }
}