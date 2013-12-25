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
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.commission.Commission;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicy;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.RoundingMode;
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
        setTotalItems(event.getTotalItem());
        setTotalCommission(event.getTotalCommission());

        final BigMoney amountOfMoney = getTotalItems()
                .convertRetainScale(event.getPricePerItem().getCurrencyUnit(), getPricePerItem().getAmount(), RoundingMode.HALF_EVEN);

        BuyOrder order = toBuyOrder(event);
        final CommissionPolicy commissionPolicy = getCommissionPolicyFactory().createCommissionPolicy(order);
        final Commission commission = commissionPolicy.calculateBuyCommission(order);
        final BigMoney total = amountOfMoney.plus(commission.getCommission()).toMoney(RoundingMode.HALF_EVEN).toBigMoney();

        logger.info("amount to reserve details: Calculated commission {}, amountOfMoney {}, total {}, for buy order: {}", commission, amountOfMoney, total, order);

        ReserveCashCommand command = new ReserveCashCommand(getPortfolioIdentifier(),
                getTransactionIdentifier(),
                total);
        getCommandBus().dispatch(new GenericCommandMessage<ReserveCashCommand>(command));
    }

    private BuyOrder toBuyOrder(BuyTransactionStartedEvent event) {
        final BuyOrder order = new BuyOrder();
        order.setOrderBookId(event.getOrderBookIdentifier());
        order.setPortfolioId(event.getPortfolioIdentifier());
        order.setItemPrice(event.getPricePerItem());
        order.setTradeAmount(event.getTotalItem());
        order.setTransactionId(event.getTransactionIdentifier());
        order.setCurrencyPair(new CurrencyPair(event.getTotalItem().getCurrencyUnit(), event.getPricePerItem().getCurrencyUnit()));

        return order;

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
                getTransactionIdentifier(),
                        event.getPortfolioIdentifier(),
                        event.getAmountToPay());
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
        CreateBuyOrderCommand command = new CreateBuyOrderCommand(
                new OrderId(),
                getPortfolioIdentifier(),
                getOrderBookIdentifier(),
                getTransactionIdentifier(),
                getTotalItems(),
                getPricePerItem(),
                getTotalCommission(),
                event.getConfirmedDate());
        getCommandBus().dispatch(new GenericCommandMessage<CreateBuyOrderCommand>(command));
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(BuyTransactionCancelledEvent event) {
        BigMoney amountToCancel =
                event.getTotalAmountOfItem().minus(event.getAmountOfExecutedItem())
                        .convertRetainScale(getPricePerItem().getCurrencyUnit(), getPricePerItem().getAmount(), RoundingMode.HALF_EVEN);

        logger.info("amount to cancel details: Calculated commission {}, amountOfMoney {}, total {}, for buy order: {}",
                commission, amountToCancel, total, order);


        logger.debug("Buy Transaction {} is cancelled, amount of cash reserved to cancel is {}",
                event.getTransactionIdentifier(),
                total);
        CancelCashReservationCommand command = new CancelCashReservationCommand(
                getPortfolioIdentifier(),
                getTransactionIdentifier(),
                amountToCancel);
        getCommandBus().dispatch(new GenericCommandMessage<CancelCashReservationCommand>(command));
    }

    private BuyOrder toBuyOrder(BuyTransactionCancelledEvent event) {
        final BuyOrder order = new BuyOrder();
        order.setItemPrice(event.getCancelledPrice());
        order.setTradeAmount(event.getAmountOfExecutedItem());
        order.setTransactionId(event.getTransactionIdentifier());
        order.setCurrencyPair(new CurrencyPair(event.getAmountOfExecutedItem().getCurrencyUnit(), event.getCancelledPrice().getCurrencyUnit()));

        return order;
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
                        event.getBuyCommission());
        getCommandBus().dispatch(new GenericCommandMessage<ExecutedTransactionCommand>(command));
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(BuyTransactionExecutedEvent event) {
        logger.debug("Buy Transaction {} is executed, last amount of executed items is {} for a price of {}",
                event.getTransactionIdentifier(), event.getAmountOfItem(), event.getItemPrice());


        final BigMoney amountOfMoneyToConfirm = event.getAmountOfItem().convertRetainScale(
                event.getItemPrice().getCurrencyUnit(),
                event.getItemPrice().getAmount(), RoundingMode.HALF_EVEN);

        BuyOrder order = toBuyOrder(event);
        final CommissionPolicy commissionPolicy = getCommissionPolicyFactory().createCommissionPolicy(order);
        final Commission commission = commissionPolicy.calculateBuyCommission(order);
        final BigMoney total = amountOfMoneyToConfirm.plus(commission.getCommission()).toMoney(RoundingMode.HALF_EVEN).toBigMoney();

        logger.info("amount to executed details: Calculated commission {}, amountOfMoney {}, total {}, for buy order: {}", commission, amountOfMoneyToConfirm, total, order);

        ConfirmCashReservationCommand confirmCommand =
                new ConfirmCashReservationCommand(getPortfolioIdentifier(),
                        getTransactionIdentifier(),
                        total);
        getCommandBus().dispatch(new GenericCommandMessage<ConfirmCashReservationCommand>(confirmCommand));
        AddAmountToPortfolioCommand addItemsCommand =
                new AddAmountToPortfolioCommand(getPortfolioIdentifier(),
                        getCoinId(),
                        event.getAmountOfItem());
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

    private BuyOrder toBuyOrder(BuyTransactionExecutedEvent event) {
        final BuyOrder order = new BuyOrder();
        order.setItemPrice(event.getItemPrice());
        order.setTradeAmount(event.getAmountOfItem());
        order.setTransactionId(event.getTransactionIdentifier());
        order.setCurrencyPair(new CurrencyPair(event.getAmountOfItem().getCurrencyUnit(), event.getItemPrice().getCurrencyUnit()));

        return order;
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(BuyTransactionPartiallyExecutedEvent event) {
        logger.debug("Buy Transaction {} is partially executed, amount of executed items is {} for a price of {}",
                event.getTransactionIdentifier(),
                        event.getAmountOfExecutedItem(),
                        event.getItemPrice());

        final BigMoney amountOfMoneyToConfirm = event.getAmountOfExecutedItem().convertRetainScale(
                event.getItemPrice().getCurrencyUnit(),
                event.getItemPrice().getAmount(), RoundingMode.HALF_EVEN);

        logger.info("amount to executed details: Calculated commission {}, amountOfMoney {}, total {}, for buy order: {}",
                commission, amountOfMoneyToConfirm, total, order);

        ConfirmCashReservationCommand confirmCommand =
                new ConfirmCashReservationCommand(getPortfolioIdentifier(),
                        getTransactionIdentifier(),
                        total);
        getCommandBus().dispatch(new GenericCommandMessage<ConfirmCashReservationCommand>(confirmCommand));
        AddAmountToPortfolioCommand addItemsCommand =
                new AddAmountToPortfolioCommand(getPortfolioIdentifier(),
                        getCoinId(),
                        event.getAmountOfExecutedItem());
        getCommandBus().dispatch(new GenericCommandMessage<AddAmountToPortfolioCommand>(addItemsCommand));
    }

    private BuyOrder toBuyOrder(BuyTransactionPartiallyExecutedEvent event) {
        final BuyOrder order = new BuyOrder();
        order.setItemPrice(event.getItemPrice());
        order.setTradeAmount(event.getAmountOfExecutedItem());
        order.setTransactionId(event.getTransactionIdentifier());
        order.setCurrencyPair(new CurrencyPair(event.getAmountOfExecutedItem().getCurrencyUnit(), event.getItemPrice().getCurrencyUnit()));

        return order;
    }
}
