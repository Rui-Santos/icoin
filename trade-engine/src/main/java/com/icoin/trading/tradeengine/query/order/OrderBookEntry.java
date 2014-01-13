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
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class OrderBookEntry extends AuditAwareEntitySupport<OrderBookEntry, String, Long> {

    private String coinIdentifier;
    private String coinName;
    private CurrencyPair currencyPair;
//    private CoinId coinId;

    private String highestBuyId;
    private String lowestSellId;
    private BigMoney tradedPrice;
    private BigMoney highestBuyPrice;
    private BigMoney lowestSellPrice;

    private String buyTransactionId;
    private String sellTransactionId;

    private Date lastTradedTime;

    public String getCoinIdentifier() {
        return coinIdentifier;
    }

    public void setCoinIdentifier(String coinIdentifier) {
        this.coinIdentifier = coinIdentifier;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public CurrencyUnit getBaseCurrency() {
        return CurrencyUnit.of(currencyPair.getBaseCurrency());
    }

    public CurrencyUnit getCounterCurrency() {
        return CurrencyUnit.of(currencyPair.getCounterCurrency());
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

//    public CoinId getCoinId() {
//        return coinId;
//    }
//
//    public void setCoinId(CoinId coinId) {
//        this.coinId = coinId;
//    }

    public String getHighestBuyId() {
        return highestBuyId;
    }

    public void setHighestBuyId(String highestBuyId) {
        this.highestBuyId = highestBuyId;
    }

    public String getLowestSellId() {
        return lowestSellId;
    }

    public void setLowestSellId(String lowestSellId) {
        this.lowestSellId = lowestSellId;
    }

    public BigMoney getTradedPrice() {
        return tradedPrice;
    }

    public void setTradedPrice(BigMoney tradedPrice) {
        this.tradedPrice = tradedPrice;
    }

    public BigMoney getHighestBuyPrice() {
        return highestBuyPrice;
    }

    public void setHighestBuyPrice(BigMoney highestBuyPrice) {
        this.highestBuyPrice = highestBuyPrice;
    }

    public BigMoney getLowestSellPrice() {
        return lowestSellPrice;
    }

    public void setLowestSellPrice(BigMoney lowestSellPrice) {
        this.lowestSellPrice = lowestSellPrice;
    }

    public String getBuyTransactionId() {
        return buyTransactionId;
    }

    public void setBuyTransactionId(String buyTransactionId) {
        this.buyTransactionId = buyTransactionId;
    }

    public String getSellTransactionId() {
        return sellTransactionId;
    }

    public void setSellTransactionId(String sellTransactionId) {
        this.sellTransactionId = sellTransactionId;
    }

    public Date getLastTradedTime() {
        return lastTradedTime;
    }

    public void setLastTradedTime(Date lastTradedTime) {
        this.lastTradedTime = lastTradedTime;
    }
}
