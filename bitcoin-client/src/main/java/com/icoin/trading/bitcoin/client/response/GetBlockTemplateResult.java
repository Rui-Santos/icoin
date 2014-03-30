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
import com.icoin.trading.bitcoin.client.JsonExtra;
import com.icoin.trading.bitcoin.client.Transaction;

import java.util.Date;

/**
 * Data returned by BitcoindClient's getBlockTemplate method.
 */
@JsonPropertyOrder({
        "version",
        "previousblockhash",
        "transactions",
        "coinbaseaux",
        "coinbasetxn",
        "coinbasevalue",
        "target",
        "mintime",
        "mutable",
        "noncerange",
        "sigoplimit",
        "sizelimit",
        "curtime",
        "bits",
        "height",
        "workid"
})
public class GetBlockTemplateResult extends JsonExtra {

    /**
     * Block version.
     */
    @JsonProperty
    private Integer version;

    /**
     * Hash of current highest block.
     */
    @JsonProperty("previousblockhash")
    private String previousBlockHash;

    /**
     * Contents of non-coinbase transactions that should be included in the next
     * block.
     */
    @JsonProperty
    private Transaction[] transactions;

    /**
     * Data that should be included in coinbase.
     */
    @JsonProperty("coinbaseaux")
    @JsonInclude(Include.NON_EMPTY)
    private AnyJsonObject coinBaseAux;

    /**
     * Information for coinbase transaction.
     * <p/>
     * This OR coinBaseValue is required.
     */
    @JsonProperty("coinbasetxn")
    @JsonInclude(Include.NON_EMPTY)
    private Transaction coinBaseTransaction;

    /**
     * Maximum allowable input to coinbase transaction in Satoshis, including the generation
     * award and transaction fees.
     * <p/>
     * This OR coinBaseTransaction is required.
     */
    @JsonProperty("coinbasevalue")
    @JsonInclude(Include.NON_EMPTY)
    private Long coinBaseValue;

    /**
     * Hash target.
     */
    @JsonProperty
    private String target;

    /**
     * Minimum timestamp appropriate for next block.
     */
    @JsonProperty("mintime")
    private Date minTime;

    /**
     * List of ways the block template may be changed.
     */
    @JsonProperty
    private String[] mutable;

    /**
     * Range of valid nonces.
     */
    @JsonProperty("noncerange")
    private String nonceRange;

    /**
     * Limit of sigops in blocks.
     */
    @JsonProperty("sigoplimit")
    @JsonInclude(Include.NON_EMPTY)
    private Integer sigOpLimit;

    /**
     * Limit of block size.
     */
    @JsonProperty("sizelimit")
    @JsonInclude(Include.NON_EMPTY)
    private Integer sizeLimit;

    /**
     * Current timestamp.
     */
    @JsonProperty("curtime")
    private Date curTime;

    /**
     * Compressed target of next block.
     */
    @JsonProperty
    private String bits;

    /**
     * Height of the next block.
     */
    @JsonProperty
    private Long height;

    @JsonProperty("workid")
    @JsonInclude(Include.NON_EMPTY)
    private String workId;

    public Integer getVersion() {
        return version;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public Transaction[] getTransactions() {
        return transactions;
    }

    public AnyJsonObject getCoinBaseAux() {
        return coinBaseAux;
    }

    public Transaction getCoinBaseTransaction() {
        return coinBaseTransaction;
    }

    public Long getCoinBaseValue() {
        return coinBaseValue;
    }

    public String getTarget() {
        return target;
    }

    public Date getMinTime() {
        return minTime;
    }

    public String[] getMutable() {
        return mutable;
    }

    public String getNonceRange() {
        return nonceRange;
    }

    public Integer getSigOpLimit() {
        return sigOpLimit;
    }

    public Integer getSizeLimit() {
        return sizeLimit;
    }

    public Date getCurTime() {
        return curTime;
    }

    public String getBits() {
        return bits;
    }

    public Long getHeight() {
        return height;
    }

    public String getWorkId() {
        return workId;
    }
}
