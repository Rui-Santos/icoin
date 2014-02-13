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
import org.joda.money.CurrencyUnit;
import org.springframework.data.annotation.Transient;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * @author Jettro Coenradie
 */
public class ItemEntry extends ValueObjectSupport<ItemEntry> {
    private String coinIdentifier;

    @Transient
    private CurrencyUnit currencyUnit;
    private String coinName;
    private BigMoney amountInPossession;
    private BigMoney reservedAmount;

    public ItemEntry(String coinIdentifier) {
        hasLength(coinIdentifier);
        this.coinIdentifier = coinIdentifier;
        this.currencyUnit = CurrencyUnit.of(coinIdentifier);
    }

    public BigMoney getAmountInPossession() {
        return amountInPossession == null ? BigMoney.zero(currencyUnit) : amountInPossession;
    }

    public BigMoney getReservedAmount() {
        return reservedAmount == null ? BigMoney.zero(currencyUnit) : reservedAmount;
    }

    public BigMoney getAvailableAmount() {
        return amountInPossession == null ? BigMoney.zero(currencyUnit) : amountInPossession.minus(getReservedAmount());
    }

    @SuppressWarnings("unused")
    private void setReservedAmount(BigMoney reservedAmount) {
        this.reservedAmount = reservedAmount;
    }

    public void addAmountInPossession(BigMoney amount) {
        notNull(amount);
        isTrue(amount.isPositiveOrZero());

        if (amountInPossession == null) {
            amountInPossession = BigMoney.zero(currencyUnit);
        }
        this.amountInPossession = amountInPossession.plus(amount);
    }

    public void addReservedAmount(BigMoney amount) {
        notNull(amount);
        isTrue(amount.isPositiveOrZero());
        if (reservedAmount == null) {
            reservedAmount = BigMoney.zero(currencyUnit);
        }
        this.reservedAmount = reservedAmount.plus(amount);
    }

    public void cancelReserved(BigMoney amount) {
        if (reservedAmount == null) {
            reservedAmount = BigMoney.zero(currencyUnit);
        }

        isTrue(reservedAmount.minus(amount).isPositiveOrZero(), "reserved amount " + reservedAmount + " cannot be less than " + amount + "!");

        this.reservedAmount = reservedAmount.minus(amount);
    }

    public void confirmReserved(BigMoney amount) {
        if (reservedAmount == null) {
            reservedAmount = BigMoney.zero(currencyUnit);
        }
        if (amountInPossession == null) {
            amountInPossession = BigMoney.zero(currencyUnit);
        }

        isTrue(reservedAmount.minus(amount).isPositiveOrZero(), "reserved amount " + reservedAmount + " cannot be less than " + amount + "!");
        isTrue(amountInPossession.minus(amount).isPositiveOrZero(), "amount in possession " + amountInPossession + " cannot be less than " + amount + "!");

        this.amountInPossession = amountInPossession.minus(amount);
        this.reservedAmount = reservedAmount.minus(amount);

    }

    @SuppressWarnings("unused")
    private void setAmountInPossession(BigMoney amountInPossession) {
        this.amountInPossession = amountInPossession;
    }

    public String getCoinIdentifier() {
        return coinIdentifier;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ItemEntry itemEntry = (ItemEntry) o;

        if (amountInPossession != null ? !amountInPossession.equals(itemEntry.amountInPossession) : itemEntry.amountInPossession != null)
            return false;
        if (reservedAmount != null ? !reservedAmount.equals(itemEntry.reservedAmount) : itemEntry.reservedAmount != null)
            return false;
        if (!coinIdentifier.equals(itemEntry.coinIdentifier)) return false;
        if (coinName != null ? !coinName.equals(itemEntry.coinName) : itemEntry.coinName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + coinIdentifier.hashCode();
        result = 31 * result + (coinName != null ? coinName.hashCode() : 0);
        result = 31 * result + (amountInPossession != null ? amountInPossession.hashCode() : 0);
        result = 31 * result + (reservedAmount != null ? reservedAmount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ItemEntry{" +
                "coinIdentifier='" + coinIdentifier + '\'' +
                ", coinName='" + coinName + '\'' +
                ", amountInPossession=" + amountInPossession +
                ", reservedAmount=" + reservedAmount +
                '}';
    }
}
