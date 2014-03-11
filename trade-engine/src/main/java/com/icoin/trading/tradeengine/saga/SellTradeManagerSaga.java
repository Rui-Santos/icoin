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
import com.icoin.trading.tradeengine.application.command.portfolio.cash.DepositCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.CancelAmountReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.ConfirmAmountReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.ReserveAmountCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.ConfirmTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.ExecutedTransactionCommand;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemToReserveNotAvailableInPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.NotEnoughItemAvailableToReserveInPortfolio;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionPartiallyExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionStartedEvent;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;

/**
 * @author Jettro Coenradie
 */
public class SellTradeManagerSaga extends TradeManagerSaga {

    private static final long serialVersionUID = 5337051021661868242L;
    private final static Logger logger = LoggerFactory.getLogger(SellTradeManagerSaga.class);
    private BigMoney leftTotalItem;

    @StartSaga
    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(SellTransactionStartedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "A new sell transaction is started with identifier {}, for portfolio with identifier {} and orderbook with identifier {}",
                    event.getTransactionIdentifier(),
                    event.getPortfolioIdentifier(),
                    event.getOrderBookIdentifier());
            logger.debug("The sell transaction with identifier {} is for selling {} items for the price of {}",
                    event.getTransactionIdentifier(),
                    event.getTotalItem(),
                    event.getPricePerItem());
        }

        setTransactionIdentifier(event.getTransactionIdentifier());
        setOrderBookIdentifier(event.getOrderBookIdentifier());
        setCoinId(event.getCoinId());
        setPortfolioIdentifier(event.getPortfolioIdentifier());
        setPricePerItem(event.getPricePerItem());
        setTotalItem(event.getTotalItem());
        setTotalCommission(event.getTotalCommission());
        setLeftCommission(event.getTotalCommission());

        leftTotalItem = event.getTotalItem();

        ReserveAmountCommand reserveAmountCommand =
                new ReserveAmountCommand(getPortfolioIdentifier(),
                        getCoinId(),
                        getTransactionIdentifier(),
                        event.getTotalItem(),
                        event.getTotalCommission(),
                        event.getTime());
        getCommandGateway().send(reserveAmountCommand);
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(ItemReservedEvent event) {
        final Date confirmDate = currentTime();
        if (logger.isDebugEnabled()) {
            logger.debug("Items for transaction {} are reserved, set confirm date {}", getTransactionIdentifier(), confirmDate);
        }
        ConfirmTransactionCommand confirmTransactionCommand = new ConfirmTransactionCommand(getTransactionIdentifier(), confirmDate);
        getCommandGateway().send(confirmTransactionCommand);
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(NotEnoughItemAvailableToReserveInPortfolio event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Cannot continue with transaction {} with item {} since the items needed cannot be reserved from portfolio {}",
                    getTransactionIdentifier(), getTotalItem(), getPortfolioIdentifier());
        }
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(ItemToReserveNotAvailableInPortfolioEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Cannot continue with transaction {} with item {} since the items needed cannot be found from portfolio {}",
                    getTransactionIdentifier(), getTotalItem(), getPortfolioIdentifier());
        }
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(SellTransactionConfirmedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Sell Transaction {} is approved to make the sell order", event.getTransactionIdentifier());
        }

        CreateSellOrderCommand command = new CreateSellOrderCommand(new OrderId(),
                getPortfolioIdentifier(),
                getOrderBookIdentifier(),
                getTransactionIdentifier(),
                getTotalItem(),
                getPricePerItem(),
                getTotalCommission(),
                event.getConfirmedDate());
        getCommandGateway().send(command);
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(SellTransactionCancelledEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Sell Transaction {} is cancelled, amount of cash reserved to cancel is {}, left commission is {}",
                    event.getTransactionIdentifier(),
                    leftTotalItem, getLeftCommission());
        }
        CancelAmountReservationForPortfolioCommand command =
                new CancelAmountReservationForPortfolioCommand(
                        getPortfolioIdentifier(),
                        getCoinId(),
                        getTransactionIdentifier(),
                        leftTotalItem,
                        getLeftCommission(),
                        event.getTime());
        getCommandGateway().send(command);
    }

    @SagaEventHandler(associationProperty = "sellTransactionId", keyName = "transactionIdentifier")
    public void handle(TradeExecutedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Sell Transaction {} is executed, items for transaction are {} for a price of {}",
                    getTransactionIdentifier(), event.getTradeAmount(), event.getTradedPrice());
        }

        ExecutedTransactionCommand command = new ExecutedTransactionCommand(getTransactionIdentifier(),
                getCoinId(),
                event.getTradeAmount(),
                event.getTradedPrice(),
                event.getExecutedMoney(),
                event.getSellCommission(),
                event.getTradeTime());
        getCommandGateway().send(command);
    }


    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(SellTransactionExecutedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Sell Transaction {} is executed, last amount of executed items is {} for a price of {}",
                    event.getTransactionIdentifier(), event.getAmountOfItem(), event.getItemPrice());
        }

        BigMoney commission = adjustCommission(event.getCommission());

        ConfirmAmountReservationForPortfolioCommand confirmCommand =
                new ConfirmAmountReservationForPortfolioCommand(getPortfolioIdentifier(),
                        getCoinId(),
                        getTransactionIdentifier(),
                        event.getAmountOfItem(),
                        commission,
                        event.getTime());
        getCommandGateway().sendAndWait(confirmCommand);

        DepositCashCommand depositCommand =
                new DepositCashCommand(getPortfolioIdentifier(),
                        event.getExecutedMoney(), event.getTime());
        getCommandGateway().send(depositCommand);


//        commandGateway.sendAndWait(new ClearReservedCashCommand());
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(SellTransactionPartiallyExecutedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Sell Transaction {} is partially executed, amount of executed items is {} for a price of {}",
                    event.getTransactionIdentifier(),
                    event.getAmountOfExecutedItem(),
                    event.getItemPrice());
        }

        BigMoney commission = adjustCommission(event.getCommission());

        ConfirmAmountReservationForPortfolioCommand confirmCommand =
                new ConfirmAmountReservationForPortfolioCommand(getPortfolioIdentifier(),
                        getCoinId(),
                        getTransactionIdentifier(),
                        event.getAmountOfExecutedItem(),
                        commission,
                        event.getTime());
        getCommandGateway().sendAndWait(confirmCommand);
        DepositCashCommand depositCommand =
                new DepositCashCommand(getPortfolioIdentifier(),
                        event.getExecutedMoney(),
                        event.getTime());
        getCommandGateway().send(depositCommand);
    }

//    private void adjustAmount(BigMoney executedItem) {
//        leftTotalItem = leftTotalItem.minus(executedItem);
//    }
}
