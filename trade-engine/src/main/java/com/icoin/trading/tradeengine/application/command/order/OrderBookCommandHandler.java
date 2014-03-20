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

package com.icoin.trading.tradeengine.application.command.order;

import com.icoin.trading.api.tradeengine.command.order.CreateBuyOrderCommand;
import com.icoin.trading.api.tradeengine.command.order.CreateOrderBookCommand;
import com.icoin.trading.api.tradeengine.command.order.CreateOrderCommand;
import com.icoin.trading.api.tradeengine.command.order.CreateSellOrderCommand;
import com.icoin.trading.api.tradeengine.command.order.RefreshOrderBookPriceCommand;
import com.icoin.trading.api.coin.domain.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.OrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderType;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Allard Buijze
 */
@Component
public class OrderBookCommandHandler {
    private static Logger logger = LoggerFactory.getLogger(OrderBookCommandHandler.class);

    private Repository<OrderBook> repository;
    private OrderRepository orderRepository;
    private TradeExecutor tradeExecutor;
    private OrderExecutorHelper orderExecutorHelper;

    @SuppressWarnings("unused")
    @CommandHandler
    public void handleBuyOrder(CreateBuyOrderCommand command) {
        OrderBook orderBook = repository.load(command.getOrderBookId(), null);
        final Order buyOrder = createBuyOrder(command, orderBook.getCurrencyPair());
        orderRepository.save(buyOrder);

        orderBook.addBuyOrder(
                command.getOrderId(),
                command.getTransactionId(),
                command.getTradeAmount(),
                command.getItemPrice(),
                command.getTotalCommission(),
                command.getPortfolioId(),
                command.getPlaceDate());
        tradeExecutor.execute(buyOrder);
    }

    private Order createBuyOrder(CreateBuyOrderCommand command, CurrencyPair currencyPair) {
        final Order buyOrder = new Order(OrderType.BUY);
        return fillOrder(buyOrder, command, currencyPair);
    }

    @SuppressWarnings("unused")
    @CommandHandler
    public void handleSellOrder(CreateSellOrderCommand command) {
        OrderBook orderBook = repository.load(command.getOrderBookId(), null);
        final Order sellOrder = createSellOrder(command, orderBook.getCurrencyPair());

        orderRepository.save(sellOrder);
        orderBook.addSellOrder(command.getOrderId(),
                command.getTransactionId(),
                command.getTradeAmount(),
                command.getItemPrice(),
                command.getTotalCommission(),
                command.getPortfolioId(),
                command.getPlaceDate());

        tradeExecutor.execute(sellOrder);
    }

    @SuppressWarnings("unused")
    @CommandHandler
    public void handleRefreshOrderBook(RefreshOrderBookPriceCommand command) {
        OrderBook orderBook = repository.load(command.getOrderBookId(), null);

        if (orderBook == null) {
            logger.warn("Orderbook is null for {}", command.getOrderBookId());
            return;
        }
        logger.info("Before refresh, order book status is: " + ReflectionToStringBuilder.toString(orderBook, ToStringStyle.SHORT_PREFIX_STYLE));
        orderExecutorHelper.refresh(orderBook);
        logger.info("After refresh, order book status is: " + ReflectionToStringBuilder.toString(orderBook, ToStringStyle.SHORT_PREFIX_STYLE));
    }

    private Order createSellOrder(CreateSellOrderCommand command, CurrencyPair currencyPair) {
        Order sellOrder = new Order(OrderType.SELL);
        return fillOrder(sellOrder, command, currencyPair);
    }

    private Order fillOrder(Order order,
                            CreateOrderCommand command,
                            CurrencyPair currencyPair) {
        order.setPrimaryKey(command.getOrderId().toString());
        order.setOrderBookId(command.getOrderBookId());
        order.setTransactionId(command.getTransactionId());
        order.setCurrencyPair(currencyPair);
        order.setPlaceDate(command.getPlaceDate());
        order.setItemPrice(command.getItemPrice());
        order.setTradeAmount(command.getTradeAmount());
        order.setItemRemaining(command.getTradeAmount());
        order.setLeftCommission(command.getTotalCommission());
        order.setPortfolioId(command.getPortfolioId());

        return order;
    }

    @SuppressWarnings("unused")
    @CommandHandler
    public void handleCreateOrderBook(CreateOrderBookCommand command) {
        OrderBook orderBook =
                new OrderBook(command.getOrderBookIdentifier(), command.getCurrencyPair());
        repository.add(orderBook);
    }

    @Resource(name = "orderBookRepository")
    public void setRepository(Repository<OrderBook> orderBookRepository) {
        this.repository = orderBookRepository;
    }

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Autowired
    public void setTradeExecutor(TradeExecutor tradeExecutor) {
        this.tradeExecutor = tradeExecutor;
    }

    @Autowired
    public void setOrderExecutorHelper(OrderExecutorHelper orderExecutorHelper) {
        this.orderExecutorHelper = orderExecutorHelper;
    }
}
