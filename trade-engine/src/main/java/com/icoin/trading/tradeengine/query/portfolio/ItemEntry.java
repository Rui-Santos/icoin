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

import com.homhon.base.domain.model.ValueObjectSupport;
import org.joda.money.BigMoney;

/**
 * @author Jettro Coenradie
 */
public class ItemEntry extends ValueObjectSupport<ItemEntry> {
    private String coinIdentifier;
//    private String orderBookIdentifier;
    private String coinName;
    private BigMoney amount;

    public BigMoney getAmount() {
        return amount;
    }

    public void setAmount(BigMoney amount) {
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

//    public String getOrderBookIdentifier() {
//        return orderBookIdentifier;
//    }
//
//    public void setOrderBookIdentifier(String orderBookIdentifier) {
//        this.orderBookIdentifier = orderBookIdentifier;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ItemEntry itemEntry = (ItemEntry) o;

        if (amount != null ? !amount.equals(itemEntry.amount) : itemEntry.amount != null) return false;
        if (coinIdentifier != null ? !coinIdentifier.equals(itemEntry.coinIdentifier) : itemEntry.coinIdentifier != null)
            return false;
        if (coinName != null ? !coinName.equals(itemEntry.coinName) : itemEntry.coinName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (coinIdentifier != null ? coinIdentifier.hashCode() : 0);
        result = 31 * result + (coinName != null ? coinName.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ItemEntry{" +
                "coinIdentifier='" + coinIdentifier + '\'' +
                ", coinName='" + coinName + '\'' +
                ", amount=" + amount +
                '}';
    }
}
