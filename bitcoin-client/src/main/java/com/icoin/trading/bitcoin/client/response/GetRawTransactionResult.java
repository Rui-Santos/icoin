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

import java.util.Date;

/**
 * Data returned by BitcoindClient's getRawTransaction method.
 */
@JsonPropertyOrder({
        "hex",
        "txid",
        "version",
        "locktime",
        "vin",
        "vout",
        "blockhash",
        "confirmations",
        "time",
        "blocktime"
})
public class GetRawTransactionResult extends JsonExtra {
    @JsonProperty
    private String hex;

    @JsonProperty("txid")
    private String txId;
    @JsonProperty
    public Integer version;

    @JsonProperty("locktime")
    private Integer lockTime;

    @JsonProperty("vin")
    private TransactionInput[] txInputs;

    @JsonProperty("vout")
    private TransactionOutput[] txOutputs;

    @JsonProperty("blockhash")
    private String blockHash;
    @JsonProperty
    private Integer confirmations;
    @JsonProperty
    private Date time;

    @JsonProperty("blocktime")
    private Date blockTime;

    public String getHex() {
        return hex;
    }

    public String getTxId() {
        return txId;
    }

    public Integer getVersion() {
        return version;
    }

    public Integer getLockTime() {
        return lockTime;
    }

    public TransactionInput[] getTxInputs() {
        return txInputs;
    }

    public TransactionOutput[] getTxOutputs() {
        return txOutputs;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public Integer getConfirmations() {
        return confirmations;
    }

    public Date getTime() {
        return time;
    }

    public Date getBlockTime() {
        return blockTime;
    }
}
