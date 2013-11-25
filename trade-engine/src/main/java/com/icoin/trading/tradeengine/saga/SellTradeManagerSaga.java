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

package com.icoin.trading.tradeengine.saga;

import com.icoin.trading.tradeengine.application.command.order.CreateSellOrderCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.ConfirmTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.ExecutedTransactionCommand;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionPartiallyExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionStartedEvent;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.DepositCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.CancelItemReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.NotEnoughItemsAvailableToReserveInPortfolio;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.ReserveItemsCommand;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.ConfirmItemReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemsReservedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jettro Coenradie
 */
public class SellTradeManagerSaga extends TradeManagerSaga {

    private static final long serialVersionUID = 5337051021661868242L;
    private final static Logger logger = LoggerFactory.getLogger(SellTradeManagerSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(SellTransactionStartedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "A new sell transaction is started with identifier {}, for portfolio with identifier {} and orderbook with identifier {}",
                    new Object[]{event.getTransactionIdentifier(),
                            event.getPortfolioIdentifier(),
                            event.getOrderbookIdentifier()});
            logger.debug("The sell transaction with identifier {} is for selling {} items for the price of {}",
                    new Object[]{event.getTransactionIdentifier(),
                            event.getTotalItems(),
                            event.getPricePerItem()});
        }

        setTransactionIdentifier(event.getTransactionIdentifier());
        setOrderbookIdentifier(event.getOrderbookIdentifier());
        setPortfolioIdentifier(event.getPortfolioIdentifier());
        setPricePerItem(event.getPricePerItem());
        setTotalItems(event.getTotalItems());

        ReserveItemsCommand reserveItemsCommand =
                new ReserveItemsCommand(getPortfolioIdentifier(),
                        getOrderbookIdentifier(),
                        getTransactionIdentifier(),
                        event.getTotalItems());
        getCommandBus().dispatch(new GenericCommandMessage<ReserveItemsCommand>(reserveItemsCommand));
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(ItemsReservedEvent event) {
        logger.debug("Items for transaction {} are reserved", getTransactionIdentifier());
        ConfirmTransactionCommand confirmTransactionCommand = new ConfirmTransactionCommand(getTransactionIdentifier());
        getCommandBus().dispatch(new GenericCommandMessage<ConfirmTransactionCommand>(confirmTransactionCommand));
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(NotEnoughItemsAvailableToReserveInPortfolio event) {
        logger.debug("Cannot continue with transaction with id {} since the items needed cannot be reserved",
                getTotalItems());
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(SellTransactionConfirmedEvent event) {
        logger.debug("Sell Transaction {} is approved to make the sell order", event.getTransactionIdentifier());

        CreateSellOrderCommand command = new CreateSellOrderCommand(new OrderId(),
                getPortfolioIdentifier(),
                getOrderbookIdentifier(),
                getTransactionIdentifier(),
                getTotalItems(),
                getPricePerItem());
        getCommandBus().dispatch(new GenericCommandMessage<CreateSellOrderCommand>(command));
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(SellTransactionCancelledEvent event) {
        long amountOfCancelledItems = event.getTotalAmountOfItems() - event.getAmountOfExecutedItems();
        logger.debug("Sell Transaction {} is cancelled, amount of cash reserved to cancel is {}",
                event.getTransactionIdentifier(),
                amountOfCancelledItems);
        CancelItemReservationForPortfolioCommand command =
                new CancelItemReservationForPortfolioCommand(getPortfolioIdentifier(),
                        getOrderbookIdentifier(),
                        getTransactionIdentifier(),
                        amountOfCancelledItems);
        getCommandBus().dispatch(new GenericCommandMessage<CancelItemReservationForPortfolioCommand>(command));
    }

    @SagaEventHandler(associationProperty = "sellTransactionId", keyName = "transactionIdentifier")
    public void handle(TradeExecutedEvent event) {
        logger.debug("Sell Transaction {} is executed, items for transaction are {} for a price of {}",
                new Object[]{getTransactionIdentifier(), event.getTradeAmount(), event.getTradePrice()});
        ExecutedTransactionCommand command = new ExecutedTransactionCommand(getTransactionIdentifier(),
                event.getTradeAmount(),
                event.getTradePrice());
        getCommandBus().dispatch(new GenericCommandMessage<ExecutedTransactionCommand>(command));
    }


    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(SellTransactionExecutedEvent event) {
        logger.debug("Sell Transaction {} is executed, last amount of executed items is {} for a price of {}",
                new Object[]{event.getTransactionIdentifier(), event.getAmountOfItems(), event.getItemPrice()});

        ConfirmItemReservationForPortfolioCommand confirmCommand =
                new ConfirmItemReservationForPortfolioCommand(getPortfolioIdentifier(),
                        getOrderbookIdentifier(),
                        getTransactionIdentifier(),
                        event.getAmountOfItems());
        getCommandBus().dispatch(new GenericCommandMessage<ConfirmItemReservationForPortfolioCommand>(confirmCommand));
        DepositCashCommand depositCommand =
                new DepositCashCommand(getPortfolioIdentifier(),
                        event.getItemPrice() * event.getAmountOfItems());
        getCommandBus().dispatch(new GenericCommandMessage<DepositCashCommand>(depositCommand));
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(SellTransactionPartiallyExecutedEvent event) {
        logger.debug("Sell Transaction {} is partially executed, amount of executed items is {} for a price of {}",
                new Object[]{event.getTransactionIdentifier(),
                        event.getAmountOfExecutedItems(),
                        event.getItemPrice()});

        ConfirmItemReservationForPortfolioCommand confirmCommand =
                new ConfirmItemReservationForPortfolioCommand(getPortfolioIdentifier(),
                        getOrderbookIdentifier(),
                        getTransactionIdentifier(),
                        event.getAmountOfExecutedItems());
        getCommandBus().dispatch(new GenericCommandMessage<ConfirmItemReservationForPortfolioCommand>(confirmCommand));
        DepositCashCommand depositCommand =
                new DepositCashCommand(getPortfolioIdentifier(),
                        event.getItemPrice() * event.getAmountOfExecutedItems());
        getCommandBus().dispatch(new GenericCommandMessage<DepositCashCommand>(depositCommand));
    }
}
