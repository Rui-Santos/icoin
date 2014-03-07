/*
 * Copyright (c) 2012. Axon Framework
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

package com.icoin.trading.tradeengine.infrastructure.listener;

import com.google.common.collect.ImmutableMap;
import com.homhon.util.Strings;
import com.icoin.trading.infrastructure.mail.VelocityEmailSender;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashWithdrawnEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemWithdrawnEvent;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

import static com.homhon.util.Strings.hasLength;

/**
 *
 * @author Jettro Coenradie
 */
@Component
public class PortfolioExternalListener {
    private static final Logger logger = LoggerFactory.getLogger(PortfolioExternalListener.class);

    private PortfolioQueryRepository portfolioRepository;
    private UserQueryRepository userRepository;
    private OrderQueryRepository orderQueryRepository;
    private String from;
    private VelocityEmailSender sender;

    @EventHandler
    public void handle(TradeExecutedEvent event) {
        doHandleSell(event.getTradeAmount(),
                event.getTradedPrice(),
                "Sold coin successfully",
                event.getTradeTime(),
                event.getSellOrderId(),
                event);

        doHandleSell(event.getExecutedMoney(),
                event.getTradedPrice(),
                "Bought coin successfully",
                event.getTradeTime(),
                event.getBuyOrderId(),
                event);
    }

    private void doHandleSell(BigMoney money,
                              BigMoney tradedPrice,
                              String subject,
                              Date tradeTime,
                              String orderId,
                              TradeExecutedEvent event) {
        try {
            final UserEntry user = obtainUserWithOrderId(orderId);
            if (user == null) {
                logger.error("cannot find user via orderId id {}", orderId);
                return;
            }
            if (!user.isExecutedAlert()) {
                logger.info("user switched off withdrawn item alert: {}", event);
                return;
            }

            final Map<String, Object> model = ImmutableMap.of("username", (Object) user.getUsername(),
                    "money", money,
                    "price", tradedPrice,
                    "time", tradeTime
            );

            sender.sendEmail(
                    subject,
                    user.getEmail(),
                    from,
                    "listener/withdrawn.vm",
                    "utf-8",
                    model,
                    true);
        } catch (Exception e) {
            logger.error("cannot send {} for {}", subject, event);
        }
    }

    @EventHandler
    public void handle(ItemWithdrawnEvent event) {
        final UserEntry user = obtainUser(event.getPortfolioIdentifier());
        if (user == null) {
            logger.error("cannot find user via portfolio id {}", event.getPortfolioIdentifier());
            return;
        }
        if (!user.isWithdrawItemAlert()) {
            logger.info("user switched off withdrawn item alert: {}", event);
            return;
        }

        handle(user, "Coin withdrawal notification", event.getAmountOfItemAdded(), event.getWithdrawnTime());
    }

    @EventHandler
    public void handle(CashWithdrawnEvent event) {
        final UserEntry user = obtainUser(event.getPortfolioIdentifier());
        if (user == null) {
            logger.error("cannot find user via portfolio id {}", event.getPortfolioIdentifier());
            return;
        }
        if (!user.isWithdrawMoneyAlert()) {
            logger.info("user switched off withdrawn money alert: {}", event);
            return;
        }

        handle(user, "Cash withdrawal notification", event.getAmountPaid(), event.getWithdrawnTime());
    }

    private void handle(final UserEntry user, final String subject, final BigMoney money, final Date time) {
        if (money == null || money.isZero()) {
            logger.info("money is null or zero");
            return;
        }
        if (!Strings.hasLength(user.getEmail())) {
            logger.info("token is null or empty");
            return;
        }

        final Map<String, Object> model = ImmutableMap.of("username", (Object) user.getUsername(),
                "money", money,
                "time", time
        );

        sender.sendEmail(
                subject,
                user.getEmail(),
                from,
                "listener/withdrawn.vm",
                "utf-8",
                model,
                true);
    }

    private UserEntry obtainUserWithOrderId(String orderId) {
        if (!hasLength(orderId)) {
            return null;
        }
        final OrderEntry order = orderQueryRepository.findOne(orderId);

        if (order == null || !hasLength(order.getPortfolioId())) {
            return null;
        }

        return obtainUser(new PortfolioId(order.getPortfolioId()));
    }

    private UserEntry obtainUser(PortfolioId portfolioId) {
        final PortfolioEntry portfolio = portfolioRepository.findOne(portfolioId.toString());

        if (portfolio == null || !hasLength(portfolio.getUserIdentifier())) {
            logger.warn("portfolio is null or userid is empty");
            return null;
        }

        return userRepository.findOne(portfolio.getUserIdentifier());
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Resource(name = "trading.velocityEmailSender")
    public void setSender(VelocityEmailSender sender) {
        this.sender = sender;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setOrderQueryRepository(OrderQueryRepository orderQueryRepository) {
        this.orderQueryRepository = orderQueryRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPortfolioRepository(PortfolioQueryRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setUserRepository(UserQueryRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Value("${email.username}")
    public void setFrom(String from) {
        this.from = from;
    }
}
