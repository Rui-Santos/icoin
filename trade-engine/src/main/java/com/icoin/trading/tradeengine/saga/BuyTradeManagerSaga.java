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
import com.icoin.trading.tradeengine.application.command.portfolio.cash.ClearReservedCashCommand;
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
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class BuyTradeManagerSaga extends TradeManagerSaga {

    private static final long serialVersionUID = 5948996680443725871L;
    private final static Logger logger = LoggerFactory.getLogger(BuyTradeManagerSaga.class);
    private BigMoney totalMoney;
    private BigMoney leftTotalMoney;

    @StartSaga
    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(BuyTransactionStartedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "A new buy transaction is started with identifier {}, for portfolio with identifier {} and orderbook with identifier {}",
                    event.getTransactionIdentifier(),
                    event.getPortfolioIdentifier(),
                    event.getOrderBookIdentifier());
            logger.debug("The new buy transaction with identifier {} is for buying {} items for the price of {}",
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

        totalMoney = event.getTotalMoney();
        leftTotalMoney = event.getTotalMoney();

        ReserveCashCommand command = new ReserveCashCommand(getPortfolioIdentifier(),
                getTransactionIdentifier(),
                event.getTotalMoney(),
                event.getTotalCommission());
        getCommandGateway().send(command);
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(CashReservedEvent event) {
        final Date confirmDate = new Date();
        logger.debug("Money for transaction with identifier {} is reserved, confirm date {}", getTransactionIdentifier(), confirmDate);
        ConfirmTransactionCommand command = new ConfirmTransactionCommand(getTransactionIdentifier(), confirmDate);
        getCommandGateway().send(command,
                new CommandCallback<Object>() {
                    @Override
                    public void onSuccess(Object result) {
                        // TODO jettro : Do we really need this?
                        logger.debug("Confirm transaction is dispatched successfully!");
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        logger.error("********* transaction {} failed to confirm!!!", getTransactionIdentifier(), cause);
                    }
                });
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(CashReservationRejectedEvent event) {
        logger.debug(
                "Not enough cash was available to make reservation in transaction {} for portfolio {}. Required: {} + {}",
                getTransactionIdentifier(),
                event.getPortfolioIdentifier(),
                event.getTotalMoney(),
                event.getTotalCommission());
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(BuyTransactionConfirmedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.info("Buy Transaction {} is approved to make the buy order", event.getTransactionIdentifier());
        }
        CreateBuyOrderCommand command = new CreateBuyOrderCommand(
                new OrderId(),
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
    public void handle(BuyTransactionCancelledEvent event) {
        CancelCashReservationCommand command = new CancelCashReservationCommand(
                getPortfolioIdentifier(),
                getTransactionIdentifier(),
                leftTotalMoney,
                getLeftCommission());
        logger.info("transaction {} is to cancel, left money {} , left commission {}.", getTransactionIdentifier(), leftTotalMoney, getLeftCommission());
        getCommandGateway().send(command);
        logger.info("transaction {} was cancelled, left money {} , left commission {}.", getTransactionIdentifier(), leftTotalMoney, getLeftCommission());
    }

    @SagaEventHandler(associationProperty = "buyTransactionId", keyName = "transactionIdentifier")
    public void handle(TradeExecutedEvent event) {
        logger.debug("Buy Transaction {} is executed, items for transaction are {} for a price of {}",
                getTransactionIdentifier(), event.getTradeAmount(), event.getTradedPrice());

        ExecutedTransactionCommand command =
                new ExecutedTransactionCommand(getTransactionIdentifier(),
                        getCoinId(),
                        event.getTradeAmount(),
                        event.getTradedPrice(),
                        event.getExecutedMoney(),
                        event.getBuyCommission());
        getCommandGateway().send(command);
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(BuyTransactionExecutedEvent event) {
        logger.debug("Buy Transaction {} is executed, last amount of executed items is {} for a price of {}",
                event.getTransactionIdentifier(), event.getAmountOfItem(), event.getItemPrice());

        adjustAmount(event.getExecutedMoney());
        BigMoney commission = adjustCommission(event.getCommission());

        ConfirmCashReservationCommand confirmCommand =
                new ConfirmCashReservationCommand(getPortfolioIdentifier(),
                        getTransactionIdentifier(),
                        event.getExecutedMoney(),
                        commission);
        getCommandGateway().sendAndWait(confirmCommand);

        AddAmountToPortfolioCommand addItemsCommand =
                new AddAmountToPortfolioCommand(getPortfolioIdentifier(),
                        getCoinId(),
                        event.getAmountOfItem());
        getCommandGateway().sendAndWait(addItemsCommand);

        //calc left commission and left reserved

        if (leftTotalMoney.isPositive() || getLeftCommission().isPositive()) {
            logger.info("need to clear reserved amount for transaction {}, left money {}, left commission {}",
                    getTransactionIdentifier(), leftTotalMoney, getLeftCommission());
            final ClearReservedCashCommand clearReservedCashCommand =
                    new ClearReservedCashCommand(getPortfolioIdentifier(),
                            getTransactionIdentifier(),
                            getOrderBookIdentifier(),
                            leftTotalMoney,
                            getLeftCommission());
            getCommandGateway().send(clearReservedCashCommand);
        }
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(BuyTransactionPartiallyExecutedEvent event) {
        logger.debug("Buy Transaction {} is partially executed, amount of executed items is {} for a price of {}",
                event.getTransactionIdentifier(),
                event.getAmountOfExecutedItem(),
                event.getItemPrice());

        adjustAmount(event.getExecutedMoney());
        BigMoney commission = adjustCommission(event.getCommission());

        ConfirmCashReservationCommand confirmCommand =
                new ConfirmCashReservationCommand(getPortfolioIdentifier(),
                        getTransactionIdentifier(),
                        event.getExecutedMoney(),
                        commission);
        getCommandGateway().sendAndWait(confirmCommand);

        AddAmountToPortfolioCommand addItemsCommand =
                new AddAmountToPortfolioCommand(getPortfolioIdentifier(),
                        getCoinId(),
                        event.getAmountOfExecutedItem());
        getCommandGateway().send(addItemsCommand);
    }

    private void adjustAmount(BigMoney executedMoney) {
        leftTotalMoney = leftTotalMoney.minus(executedMoney);
    }
}
