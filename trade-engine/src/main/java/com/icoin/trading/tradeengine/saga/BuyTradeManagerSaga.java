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

import com.icoin.trading.tradeengine.application.command.order.CreateBuyOrderCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.CancelCashReservationCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.ConfirmCashReservationCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.ReserveCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.AddAmountToPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.ConfirmTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.ExecutedTransactionCommand;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationRejectedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservedEvent;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionPartiallyExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionStartedEvent;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class BuyTradeManagerSaga extends TradeManagerSaga {

    private static final long serialVersionUID = 5948996680443725871L;
    private final static Logger logger = LoggerFactory.getLogger(BuyTradeManagerSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(BuyTransactionStartedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "A new buy transaction is started with identifier {}, for portfolio with identifier {} and orderbook with identifier {}",
                    new Object[]{event.getTransactionIdentifier(),
                            event.getPortfolioIdentifier(),
                            event.getOrderbookIdentifier()});
            logger.debug("The new buy transaction with identifier {} is for buying {} items for the price of {}",
                    new Object[]{event.getTransactionIdentifier(),
                            event.getTotalItems(),
                            event.getPricePerItem()});
        }
        setTransactionIdentifier(event.getTransactionIdentifier());
        setOrderbookIdentifier(event.getOrderbookIdentifier());
        setPortfolioIdentifier(event.getPortfolioIdentifier());
        setPricePerItem(event.getPricePerItem());
        setTotalItems(event.getTotalItems());

        ReserveCashCommand command = new ReserveCashCommand(getPortfolioIdentifier(),
                getTransactionIdentifier(),
                getTotalItems()
                        .multiply(getPricePerItem()));
        getCommandBus().dispatch(new GenericCommandMessage<ReserveCashCommand>(command));
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(CashReservedEvent event) {
        final Date confirmDate = new Date();
        logger.debug("Money for transaction with identifier {} is reserved, confirm date {}", getTransactionIdentifier(), confirmDate);
        ConfirmTransactionCommand command = new ConfirmTransactionCommand(getTransactionIdentifier(), confirmDate);
        getCommandBus().dispatch(new GenericCommandMessage<ConfirmTransactionCommand>(command),
                new CommandCallback<Object>() {
                    @Override
                    public void onSuccess(Object result) {
                        // TODO jettro : Do we really need this?
                        logger.debug("Confirm transaction is dispatched successfully!");
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        logger.error("********* WOW!!!", cause);
                    }
                });
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(CashReservationRejectedEvent event) {
        logger.debug(
                "Not enough cash was available to make reservation in transaction {} for portfolio {}. Required: {}",
                new Object[]{getTransactionIdentifier(),
                        event.getPortfolioIdentifier(),
                        event.getAmountToPayInCents()});
        //is is necessary?
        //when the whole saga complete, change the price here
        //todo after whole completion for this exec event, refresh done price
        //todo after whole completion for this exec event, refresh data

        //orderbookhandler to handle refresh data
//        commandGateway.sendAndWait(new RefreshHighestSellPriceCoommand());
//        commandGateway.sendAndWait(new RefreshLowestSellPriceCoommand());
//        commandGateway.sendAndWait(new RefreshCurrentDonePriceCoommand());
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(BuyTransactionConfirmedEvent event) {
        logger.debug("Buy Transaction {} is approved to make the buy order", event.getTransactionIdentifier());
        CreateBuyOrderCommand command = new CreateBuyOrderCommand(new OrderId(), getPortfolioIdentifier(),
                getOrderbookIdentifier(),
                getTransactionIdentifier(),
                getTotalItems(),
                getPricePerItem(),
                event.getConfirmedDate());
        getCommandBus().dispatch(new GenericCommandMessage<CreateBuyOrderCommand>(command));
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(BuyTransactionCancelledEvent event) {
        BigDecimal amountToCancel = (event.getTotalAmountOfItems().subtract(event.getAmountOfExecutedItems())).multiply(getPricePerItem());
        logger.debug("Buy Transaction {} is cancelled, amount of cash reserved to cancel is {}",
                event.getTransactionIdentifier(),
                amountToCancel);
        CancelCashReservationCommand command = new CancelCashReservationCommand(
                getPortfolioIdentifier(),
                getTransactionIdentifier(),
                amountToCancel);
        getCommandBus().dispatch(new GenericCommandMessage<CancelCashReservationCommand>(command));
    }

    @SagaEventHandler(associationProperty = "buyTransactionId", keyName = "transactionIdentifier")
    public void handle(TradeExecutedEvent event) {
        logger.debug("Buy Transaction {} is executed, items for transaction are {} for a price of {}",
                new Object[]{getTransactionIdentifier(), event.getTradeAmount(), event.getTradedPrice()});
        ExecutedTransactionCommand command = new ExecutedTransactionCommand(getTransactionIdentifier(),
                event.getTradeAmount(),
                event.getTradedPrice());
        getCommandBus().dispatch(new GenericCommandMessage<ExecutedTransactionCommand>(command));
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(BuyTransactionExecutedEvent event) {
        logger.debug("Buy Transaction {} is executed, last amount of executed items is {} for a price of {}",
                new Object[]{event.getTransactionIdentifier(), event.getAmountOfItems(), event.getItemPrice()});
        ConfirmCashReservationCommand confirmCommand =
                new ConfirmCashReservationCommand(getPortfolioIdentifier(),
                        getTransactionIdentifier(),
                        event.getAmountOfItems().multiply(event.getItemPrice()));
        getCommandBus().dispatch(new GenericCommandMessage<ConfirmCashReservationCommand>(confirmCommand));
        AddAmountToPortfolioCommand addItemsCommand =
                new AddAmountToPortfolioCommand(getPortfolioIdentifier(),
                        getOrderbookIdentifier(),
                        event.getAmountOfItems());
        getCommandBus().dispatch(new GenericCommandMessage<AddAmountToPortfolioCommand>(addItemsCommand));

        //when the whole saga complete, change the price here
        //todo after whole completion for this exec event, refresh done price
        //todo after whole completion for this exec event, refresh data

        //orderbookhandler to handle refresh data
//        commandGateway.sendAndWait(new RefreshHighestSellPriceCoommand());
//        commandGateway.sendAndWait(new RefreshLowestSellPriceCoommand());
//        commandGateway.sendAndWait(new RefreshCurrentDonePriceCoommand());
//        commandGateway.sendAndWait(new AddBackLeftReservedCommand());
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(BuyTransactionPartiallyExecutedEvent event) {
        logger.debug("Buy Transaction {} is partially executed, amount of executed items is {} for a price of {}",
                new Object[]{event.getTransactionIdentifier(),
                        event.getAmountOfExecutedItems(),
                        event.getItemPrice()});
        ConfirmCashReservationCommand confirmCommand =
                new ConfirmCashReservationCommand(getPortfolioIdentifier(),
                        getTransactionIdentifier(),
                        event.getAmountOfExecutedItems().multiply(event.getItemPrice()));
        getCommandBus().dispatch(new GenericCommandMessage<ConfirmCashReservationCommand>(confirmCommand));
        AddAmountToPortfolioCommand addItemsCommand =
                new AddAmountToPortfolioCommand(getPortfolioIdentifier(),
                        getOrderbookIdentifier(),
                        event.getAmountOfExecutedItems());
        getCommandBus().dispatch(new GenericCommandMessage<AddAmountToPortfolioCommand>(addItemsCommand));
    }
}
