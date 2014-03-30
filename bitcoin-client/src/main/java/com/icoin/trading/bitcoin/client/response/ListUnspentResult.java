/* Copyright (C) 2013, Claus Nielsen, cn@cn-consult.dk
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA. */
package com.icoin.trading.bitcoin.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.icoin.trading.bitcoin.client.JsonExtra;
import com.icoin.trading.bitcoin.client.TransactionOutputRef;

import java.math.BigDecimal;

/**
 * Data about one unspent transaction output.
 */
public class ListUnspentResult extends JsonExtra {
    @JsonUnwrapped
    private TransactionOutputRef txRef;
    @JsonProperty
    private String scriptPubKey;
    @JsonProperty
    private BigDecimal amount;
    @JsonProperty
    private Integer confirmations;

    public ListUnspentResult() {
    }

    public ListUnspentResult(TransactionOutputRef txRef, String scriptPubKey, BigDecimal amount, Integer confirmations) {
        this.txRef = txRef;
        this.scriptPubKey = scriptPubKey;
        this.amount = amount;
        this.confirmations = confirmations;
    }

    public TransactionOutputRef getTxRef() {
        return txRef;
    }

    public String getScriptPubKey() {
        return scriptPubKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getConfirmations() {
        return confirmations;
    }
}
