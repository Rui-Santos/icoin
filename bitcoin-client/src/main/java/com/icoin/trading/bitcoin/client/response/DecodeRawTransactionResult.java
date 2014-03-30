/**
 * Copyright (C) 2013, Claus Nielsen, cn@cn-consult.dk
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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.icoin.trading.bitcoin.client.JsonExtra;
import com.icoin.trading.bitcoin.client.TransactionInput;
import com.icoin.trading.bitcoin.client.TransactionOutput;


/**
 * Decoded raw transaction.
 */
@JsonPropertyOrder({
        "txid",
        "version",
        "locktime",
        "vin",
        "vout"
})
public class DecodeRawTransactionResult extends JsonExtra {

    @JsonProperty("txid")
    private String txId;

    private Integer version;
    private Integer locktime;

    @JsonProperty("vin")
    private TransactionInput[] txInputs;

    @JsonProperty("vout")
    private TransactionOutput[] txOutputs;


    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getLocktime() {
        return locktime;
    }

    public void setLocktime(Integer locktime) {
        this.locktime = locktime;
    }

    public TransactionInput[] getTxInputs() {
        return txInputs;
    }

    public void setTxInputs(TransactionInput[] txInputs) {
        this.txInputs = txInputs;
    }

    public TransactionOutput[] getTxOutputs() {
        return txOutputs;
    }

    public void setTxOutputs(TransactionOutput[] txOutputs) {
        this.txOutputs = txOutputs;
    }
}
