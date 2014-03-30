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
 * Data returned by BitcoindClient's getInfo method.
 */
@JsonPropertyOrder({
        "version",
        "protocolversion",
        "walletversion",
        "balance",
        "blocks",
        "connections",
        "proxy",
        "difficulty",
        "testnet",
        "keypoololdest",
        "keypoolsize",
        "paytxfee",
        "errors",
        "unlocked_until"
})
public class GetInfoResult extends JsonExtra {
    @JsonProperty
    private Integer version;

    @JsonProperty("protocolversion")
    private Integer protocolVersion;

    @JsonProperty("walletversion")
    private Integer walletVersion;
    @JsonProperty
    private BigDecimal balance;
    @JsonProperty
    private Integer blocks;
    @JsonProperty
    private Integer connections;
    @JsonProperty
    private String proxy;
    @JsonProperty
    private Double difficulty;
    @JsonProperty
    private Boolean testnet;

    @JsonProperty("keypoololdest")
    private Long keyPoolOldest;

    @JsonProperty("keypoolsize")
    private Integer keyPoolSize;

    @JsonProperty("paytxfee")
    private BigDecimal payTxFee;

    @JsonProperty
    private String errors;

    @JsonProperty("unlocked_until")
    private Date unlockedUntil;


    public Integer getVersion() {
        return version;
    }

    public Integer getProtocolVersion() {
        return protocolVersion;
    }

    public Integer getWalletVersion() {
        return walletVersion;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Integer getBlocks() {
        return blocks;
    }

    public Integer getConnections() {
        return connections;
    }

    public String getProxy() {
        return proxy;
    }

    public Double getDifficulty() {
        return difficulty;
    }

    public Boolean getTestnet() {
        return testnet;
    }

    public Long getKeyPoolOldest() {
        return keyPoolOldest;
    }

    public Integer getKeyPoolSize() {
        return keyPoolSize;
    }

    public BigDecimal getPayTxFee() {
        return payTxFee;
    }

    public String getErrors() {
        return errors;
    }

    public Date getUnlockedUntil() {
        return unlockedUntil;
    }
}
