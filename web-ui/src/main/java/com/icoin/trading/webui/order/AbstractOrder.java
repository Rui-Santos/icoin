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

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
public class AbstractOrder {

    private String coinId;
    private String coinName;

    @DecimalMin("0.00000001")
    private BigDecimal tradeAmount;

    @DecimalMin("0")
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
}
