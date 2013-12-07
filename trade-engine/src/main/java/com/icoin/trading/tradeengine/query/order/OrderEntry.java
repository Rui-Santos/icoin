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
import com.icoin.trading.tradeengine.domain.model.coin.CoinExchangePair;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;

import java.math.BigDecimal;
import java.util.Date;

import static com.homhon.mongo.TimeUtils.currentTime;

/**
 * @author Jettro Coenradie
 */
public class OrderEntry extends AuditAwareEntitySupport<OrderEntry, String, Long> {
    private String orderBookIdentifier;
    private BigDecimal tradeAmount;
    private BigDecimal itemPrice;
    private String userId;
    private BigDecimal itemRemaining;
    private OrderType type;
    private Date completeDate;
    private Date lastTradedTime;
    private CoinExchangePair coinExchangePair;
    private OrderStatus orderStatus = OrderStatus.PENDING;

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public BigDecimal getItemRemaining() {
        return itemRemaining;
    }

    void setItemRemaining(BigDecimal itemRemaining) {
        this.itemRemaining = itemRemaining;
    }


    public String getOrderBookIdentifier() {
        return orderBookIdentifier;
    }

    public void setOrderBookIdentifier(String orderBookIdentifier) {
        this.orderBookIdentifier = orderBookIdentifier;
    }

    public BigDecimal getTradeAmount() {
        return tradeAmount;
    }

    void setTradeAmount(BigDecimal tradeAmount) {
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

    void setType(OrderType type) {
        this.type = type;
    }

    public CoinExchangePair getCoinExchangePair() {
        return coinExchangePair;
    }

    public void setCoinExchangePair(CoinExchangePair coinExchangePair) {
        this.coinExchangePair = coinExchangePair;
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

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    private void completeOrder(Date completeDate) {
        this.completeDate = completeDate == null ? currentTime() : completeDate;
        this.orderStatus = OrderStatus.DONE;
    }

    public void recordTraded(BigDecimal tradeAmount, Date lastTradedTime) {
        this.itemRemaining = itemRemaining.subtract(tradeAmount);
        this.lastTradedTime = lastTradedTime;

        if (BigDecimal.ZERO.compareTo(itemRemaining) >= 0) {
            completeOrder(lastTradedTime);
        }
    }
}
