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

package com.icoin.trading.tradeengine.query.order;

import com.homhon.mongo.domainsupport.modelsupport.entity.AuditAwareEntitySupport;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import org.joda.money.BigMoney;

import java.util.Date;

import static com.homhon.mongo.TimeUtils.currentTime;

/**
 * @author Jettro Coenradie
 */
public class OrderEntry extends AuditAwareEntitySupport<OrderEntry, String, Long> {
    private String orderBookIdentifier;
    private BigMoney tradeAmount;
    private String userId;
    private BigMoney itemPrice;
    private BigMoney itemRemaining;
    private OrderType type;
    private Date completeDate;
    private Date lastTradedTime;
    private Date placedDate;
    private CurrencyPair currencyPair;
    private OrderStatus orderStatus = OrderStatus.PENDING;

    public BigMoney getItemPrice() {
        return itemPrice;
    }

    public Date getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(Date placedDate) {
        this.placedDate = placedDate;
    }

    public void setItemPrice(BigMoney itemPrice) {
        this.itemPrice = itemPrice;
    }

    public BigMoney getItemRemaining() {
        return itemRemaining;
    }

    public void setItemRemaining(BigMoney itemRemaining) {
        this.itemRemaining = itemRemaining;
    }


    public String getOrderBookIdentifier() {
        return orderBookIdentifier;
    }

    public void setOrderBookIdentifier(String orderBookIdentifier) {
        this.orderBookIdentifier = orderBookIdentifier;
    }

    public BigMoney getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(BigMoney tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getUserId() {
        return userId;
    }

    void setUserId(String userId) {
        this.userId = userId;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    @SuppressWarnings("UnusedDeclaration")
    private void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public Date getLastTradedTime() {
        return lastTradedTime;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setLastTradedTime(Date lastTradedTime) {
        this.lastTradedTime = lastTradedTime;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    private void completeOrder(Date completeDate) {
        this.completeDate = completeDate == null ? currentTime() : completeDate;
        this.orderStatus = OrderStatus.DONE;
    }

    public void recordTraded(BigMoney tradeAmount, Date lastTradedTime) {
        this.itemRemaining = itemRemaining.minus(tradeAmount);
        this.lastTradedTime = lastTradedTime;

        if (itemRemaining.isNegativeOrZero()) {
            completeOrder(lastTradedTime);
        }
    }
}
