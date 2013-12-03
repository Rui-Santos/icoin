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

import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrderRepository;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;

/**
 * @author Allard Buijze
 */
public class OrderBookCommandHandler {

    private Repository<OrderBook> repository;
    private SellOrderRepository sellOrderRepository;
    private BuyOrderRepository buyOrderRepository;
    private TradeExecutor tradeExecutor;


    @CommandHandler
    public void handleBuyOrder(CreateBuyOrderCommand command) {
        final BuyOrder buyOrder = createBuyOrder(command);
        buyOrderRepository.save(buyOrder);


//        OrderBook orderBook = repository.load(command.getOrderBookId(), null);

        tradeExecutor.put(buyOrder);

//        orderBook.addBuyOrder(command.getOrderId(),
//                command.getTransactionId(),
//                command.getTradeAmount(),
//                command.getItemPrice(),
//                command.getPortfolioId(),
//                command.getPlaceDate());
    }

    private BuyOrder createBuyOrder(CreateBuyOrderCommand command) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    @CommandHandler
    public void handleSellOrder(CreateSellOrderCommand command) {
        final SellOrder sellOrder = createSellOrder(command);

        sellOrderRepository.save(sellOrder);
        tradeExecutor.put(sellOrder);

//        OrderBook orderBook = repository.load(command.getOrderBookId(), null);
//        orderBook.addSellOrder(command.getOrderId(),
//                command.getTransactionId(),
//                command.getTradeAmount(),
//                command.getItemPrice(),
//                command.getPortfolioId(),
//                command.getPlaceDate());
    }

    private SellOrder createSellOrder(CreateSellOrderCommand command) {
        return null;
    }

    @CommandHandler
    public void handleCreateOrderBook(CreateOrderBookCommand command) {
        OrderBook orderBook =
                new OrderBook(command.getOrderBookIdentifier(), command.getCoinId(), command.getCoinExchangePair());
        repository.add(orderBook);
    }

    @CommandHandler
    public void handleRefreshOrderBook(CreateOrderBookCommand command) {
        OrderBook orderBook =
                new OrderBook(command.getOrderBookIdentifier(), command.getCoinId(), command.getCoinExchangePair());
        repository.add(orderBook);
    }

    public void setRepository(Repository<OrderBook> orderBookRepository) {
        this.repository = orderBookRepository;
    }
}
