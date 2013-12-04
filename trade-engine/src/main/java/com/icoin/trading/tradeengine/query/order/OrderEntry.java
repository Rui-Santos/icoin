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

import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
public class OrderEntry extends AuditAwareEntitySupport<OrderEntry, String, Long> {
    private String orderBookIdentifier;
    private BigDecimal tradeAmount;
    private BigDecimal itemPrice;
    private String userId;
    private BigDecimal itemsRemaining;
    private String type;

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public BigDecimal getItemsRemaining() {
        return itemsRemaining;
    }

    void setItemsRemaining(BigDecimal itemsRemaining) {
        this.itemsRemaining = itemsRemaining;
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

    public String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }
}
