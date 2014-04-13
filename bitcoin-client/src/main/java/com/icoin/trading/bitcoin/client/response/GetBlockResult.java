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
 * Data returned by BitcoindClient's getBlock method
 */
@JsonPropertyOrder({
        "hash",
        "confirmations",
        "size",
        "height",
        "version",
        "merkleroot",
        "tx",
        "time",
        "nonce",
        "bits",
        "difficulty",
        "previousblockhash",
        "nextblockhash"
})
public class GetBlockResult extends JsonExtra {
    @JsonProperty
    private String hash;
    @JsonProperty
    private Integer confirmations;
    @JsonProperty
    private Integer size;
    @JsonProperty
    private Long height;
    @JsonProperty
    private Integer version;

    @JsonProperty("merkleroot")
    private String merkleRoot;

    @JsonProperty("tx")
    private String[] transactions;

    @JsonProperty
    private Date time;
    @JsonProperty
    private Long nonce;
    @JsonProperty
    private String bits;
    @JsonProperty
    private BigDecimal difficulty;

    @JsonProperty("previousblockhash")
    private String previousBlockHash;

    @JsonProperty("nextblockhash")
    private String nextBlockHash;


    public String getHash() {
        return hash;
    }

    public Integer getConfirmations() {
        return confirmations;
    }

    public Integer getSize() {
        return size;
    }

    public Long getHeight() {
        return height;
    }

    public Integer getVersion() {
        return version;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public String[] getTransactions() {
        return transactions;
    }

    public Date getTime() {
        return time;
    }

    public Long getNonce() {
        return nonce;
    }

    public String getBits() {
        return bits;
    }

    public BigDecimal getDifficulty() {
        return difficulty;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public String getNextBlockHash() {
        return nextBlockHash;
    }
}
