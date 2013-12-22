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

package com.icoin.trading.webui.order;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
public class AbstractOrder {

    private String coinId;
    private String coinName;
    private BigDecimal suggestedPrice = BigDecimal.ZERO;
    private BigDecimal balance = BigDecimal.ZERO;
    private String amountCcy = Constants.CURRENCY_UNIT_BTC.getCurrencyCode();// base ccy
    private String priceCcy = Constants.DEFAULT_CURRENCY_UNIT.getCurrencyCode();// counter ccy
    private OrderType orderType;

    @DecimalMin(value = "0.0001", message = "trading.minimal.amount")
    @NotNull(message = "trading.amount.required")
    private BigDecimal tradeAmount;

    @NotNull(message = "trading.price.required")
    private BigDecimal itemPrice;

    public AbstractOrder() {
    }

    public AbstractOrder(BigDecimal itemPrice, BigDecimal tradeAmount, String coinId, String coinName) {
        this.itemPrice = itemPrice;
        this.tradeAmount = tradeAmount;
        this.coinId = coinId;
        this.coinName = coinName;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public BigDecimal getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(BigDecimal tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public BigDecimal getSuggestedPrice() {
        return suggestedPrice;
    }

    public void setSuggestedPrice(BigDecimal suggestedPrice) {
        this.suggestedPrice = suggestedPrice;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAmountCcy() {
        return amountCcy;
    }

    public void setAmountCcy(String amountCcy) {
        this.amountCcy = amountCcy;
    }

    public String getPriceCcy() {
        return priceCcy;
    }

    public void setPriceCcy(String priceCcy) {
        this.priceCcy = priceCcy;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+ "{" +
                "coinId='" + coinId + '\'' +
                ", coinName='" + coinName + '\'' +
                ", orderType='" + orderType + '\'' +
                ", suggestedPrice=" + suggestedPrice +
                ", balance=" + balance +
                ", amountCcy='" + amountCcy + '\'' +
                ", priceCcy='" + priceCcy + '\'' +
                ", tradeAmount=" + tradeAmount +
                ", itemPrice=" + itemPrice +
                '}';
    }
}
