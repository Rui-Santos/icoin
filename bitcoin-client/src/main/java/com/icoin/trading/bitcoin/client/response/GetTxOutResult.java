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
import com.icoin.trading.bitcoin.client.ScriptPubKey;

import java.math.BigDecimal;

/**
 * Data about a transaction output as returned by BitcoindClient's getTxOut method.
 */
@JsonPropertyOrder({
        "bestblock",
        "confirmations",
        "value",
        "scriptPubKey",
        "version",
        "coinbase"
})
public class GetTxOutResult extends JsonExtra {

    @JsonProperty("bestblock")
    private String bestBlock;

    @JsonProperty
    private Integer confirmations;
    @JsonProperty
    private BigDecimal value;
    @JsonProperty
    private ScriptPubKey scriptPubKey;
    @JsonProperty
    private Integer version;

    @JsonProperty("coinbase")
    private Boolean coinBase;

    public String getBestBlock() {
        return bestBlock;
    }

    public Integer getConfirmations() {
        return confirmations;
    }

    public BigDecimal getValue() {
        return value;
    }

    public ScriptPubKey getScriptPubKey() {
        return scriptPubKey;
    }

    public Integer getVersion() {
        return version;
    }

    public Boolean getCoinBase() {
        return coinBase;
    }
}
