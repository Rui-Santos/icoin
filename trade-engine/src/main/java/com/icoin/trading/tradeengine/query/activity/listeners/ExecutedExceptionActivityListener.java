package com.icoin.trading.tradeengine.query.activity.listeners;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.query.activity.repositories.PortfolioActivityQueryRepository;
import com.icoin.trading.tradeengine.query.order.repositories.OrderQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/26/14
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ExecutedExceptionActivityListener extends VersionedEntitySupport<ExecutedExceptionActivityListener, String, Long> {
    private final static Logger logger = LoggerFactory.getLogger(ExecutedExceptionActivityListener.class);

    private PortfolioActivityQueryRepository portfolioActivityRepository;
    private PortfolioQueryRepository portfolioRepository;
    private OrderQueryRepository orderRepository;

    private BigDecimal highestPriceThreshold;
    private BigDecimal lowestPriceThreshold;
    private BigDecimal highestAmountThreshold;
    private BigDecimal lowestAmountThreshold;

    private BigDecimal highestMoneyThreshold;
    private BigDecimal lowestMoneyThreshold;

    @EventHandler
    public void handleTradeExecuted(TradeExecutedEvent event) {
           event.
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
}