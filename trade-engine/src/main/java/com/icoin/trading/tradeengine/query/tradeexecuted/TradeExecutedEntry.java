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

package com.icoin.trading.tradeengine.query.tradeexecuted;

import com.homhon.mongo.domainsupport.modelsupport.entity.AuditAwareEntitySupport;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class TradeExecutedEntry extends AuditAwareEntitySupport<TradeExecutedEntry, String, Long> {

    private BigMoney tradedAmount;
    private BigMoney tradedPrice;
    private String coinName;
    private String orderBookIdentifier;
    private Date tradeTime;

    private TradeType tradeType;

    public BigMoney getTradedAmount() {
        return tradedAmount;
    }

    public void setTradedAmount(BigMoney tradedAmount) {
        this.tradedAmount = tradedAmount;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public BigMoney getTradedPrice() {
        return tradedPrice;
    }

    public void setTradedPrice(BigMoney tradedPrice) {
        this.tradedPrice = tradedPrice;
    }

    public String getOrderBookIdentifier() {
        return orderBookIdentifier;
    }

    public void setOrderBookIdentifier(String orderBookIdentifier) {
        this.orderBookIdentifier = orderBookIdentifier;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }
}