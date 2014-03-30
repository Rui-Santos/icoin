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

import java.math.BigDecimal;
import java.util.Date;

/**
 * Data about a transaction as returned by BitcoindClient's getTransaction method.
 */
@JsonPropertyOrder({
        "amount",
        "confirmations",
        "blockhash",
        "blockindex",
        "blocktime",
        "txid",
        "time",
        "timereceived",
        "details"
})
public class GetTransactionResult extends JsonExtra {

    /**
     * Total amount of the transaction.
     */
    @JsonProperty
    private BigDecimal amount;

    /**
     * Number of confirmations of the transaction.
     */
    @JsonProperty
    private Integer confirmations;

    @JsonProperty("blockhash")
    private String blockHash;

    @JsonProperty("blockindex")
    private Integer blockIndex;

    @JsonProperty("blocktime")
    private Date blockTime;

    /**
     * Transaction ID
     */
    @JsonProperty("txid")
    private String txId;

    /**
     * Time the transaction occurred.
     */
    @JsonProperty
    private Date time;

    @JsonProperty("timereceived")
    private Date timeReceived;

    @JsonProperty
    private TransactionDetail[] details;


    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getConfirmations() {
        return confirmations;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public Integer getBlockIndex() {
        return blockIndex;
    }

    public Date getBlockTime() {
        return blockTime;
    }

    public String getTxId() {
        return txId;
    }

    public Date getTime() {
        return time;
    }

    public Date getTimeReceived() {
        return timeReceived;
    }

    public TransactionDetail[] getDetails() {
        return details;
    }
}
