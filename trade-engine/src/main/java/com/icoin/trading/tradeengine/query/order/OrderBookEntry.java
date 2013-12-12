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
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;

import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
public class OrderBookEntry extends AuditAwareEntitySupport<OrderBookEntry, String, Long> {

    private String coinIdentifier;
    private String coinName;
    private CurrencyPair currencyPair;
    private CoinId coinId;

    private String highestBuyId;
    private String lowestSellId;
    private BigDecimal tradedPrice;
    private BigDecimal highestBuyPrice;
    private BigDecimal lowestSellPrice;

    private String buyTransactionId;
    private String sellTransactionId;

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

    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public void setCoinId(CoinId coinId) {
        this.coinId = coinId;
    }

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

    public BigDecimal getTradedPrice() {
        return tradedPrice;
    }

    public void setTradedPrice(BigDecimal tradedPrice) {
        this.tradedPrice = tradedPrice;
    }

    public BigDecimal getHighestBuyPrice() {
        return highestBuyPrice;
    }

    public void setHighestBuyPrice(BigDecimal highestBuyPrice) {
        this.highestBuyPrice = highestBuyPrice;
    }

    public BigDecimal getLowestSellPrice() {
        return lowestSellPrice;
    }

    public void setLowestSellPrice(BigDecimal lowestSellPrice) {
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
}
