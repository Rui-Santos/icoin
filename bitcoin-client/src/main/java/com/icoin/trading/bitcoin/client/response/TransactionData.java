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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.icoin.trading.bitcoin.client.BigDecimalPlainSerializer;
import com.icoin.trading.bitcoin.client.JsonExtra;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Data about one transaction as returned by BitconidClient's listTransactions
 * and listSinceBlock methods.
 */
@JsonPropertyOrder({
        "account",
        "address",
        "category",
        "amount",
        "fee",
        "confirmations",
        "generated",
        "blockhash",
        "blockindex",
        "blocktime",
        "txid",
        "time",
        "timereceived",
        "comment",
        "to"
})
public class TransactionData extends JsonExtra {
    @JsonProperty
    private String account;
    @JsonProperty
    private String address;
    @JsonProperty
    private String category;

    @JsonSerialize(using = BigDecimalPlainSerializer.class)
    private BigDecimal amount;

    @JsonInclude(Include.NON_NULL)
    @JsonSerialize(using = BigDecimalPlainSerializer.class)
    private BigDecimal fee;

    @JsonProperty
    private Integer confirmations;

    @JsonInclude(Include.NON_NULL)
    private Boolean generated;

    @JsonProperty("blockhash")
    @JsonInclude(Include.NON_NULL)
    private String blockHash;

    @JsonProperty("blockindex")
    @JsonInclude(Include.NON_NULL)
    private Integer blockIndex;

    @JsonProperty("blocktime")
    @JsonInclude(Include.NON_NULL)
    private Date blockTime;

    @JsonProperty("txid")
    private String txId;

    @JsonProperty
    private Date time;

    @JsonProperty("timereceived")
    private Date timeReceived;

    @JsonInclude(Include.NON_NULL)
    private String comment;

    @JsonInclude(Include.NON_NULL)
    private String to;


    public String getAccount() {
        return account;
    }

    public String getAddress() {
        return address;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public Integer getConfirmations() {
        return confirmations;
    }

    public Boolean getGenerated() {
        return generated;
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

    public String getComment() {
        return comment;
    }

    public String getTo() {
        return to;
    }
}
