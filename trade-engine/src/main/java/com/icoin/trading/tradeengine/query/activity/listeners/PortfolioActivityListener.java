package com.icoin.trading.tradeengine.query.activity.listeners;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import com.icoin.trading.tradeengine.query.activity.repositories.PortfolioActivityQueryRepository;
import com.icoin.trading.tradeengine.query.order.repositories.OrderQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
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
public class PortfolioActivityListener extends VersionedEntitySupport<PortfolioActivityListener, String, Long> {
    private final static Logger logger = LoggerFactory.getLogger(PortfolioActivityListener.class);

    private PortfolioActivityQueryRepository portfolioActivityRepository;
    private PortfolioQueryRepository portfolioRepository;
    private OrderQueryRepository orderRepository;

    @EventHandler
    public void handleTradeExecuted() {

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