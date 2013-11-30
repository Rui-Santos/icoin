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

package com.icoin.trading.tradeengine.query.portfolio;

import com.homhon.mongo.domainsupport.modelsupport.entity.AuditAwareEntitySupport;

import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
public class ItemEntry extends AuditAwareEntitySupport<ItemEntry, String, Long> {
    private String coinIdentifier;
    private String coinName;
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

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

    @Override
    public String toString() {
        return "ItemEntry{" +
                "amount=" + amount +
                ", primaryKey='" + primaryKey + '\'' +
                ", coinIdentifier='" + coinIdentifier + '\'' +
                ", coinName='" + coinName + '\'' +
                '}';
    }
}
