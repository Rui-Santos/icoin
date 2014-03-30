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


/**
 * Data returned by BitcoindClient's getMiningInfo method.
 */
@JsonPropertyOrder({
        "blocks",
        "currentblocksize",
        "currentblocktx",
        "difficulty",
        "errors",
        "generate",
        "genproclimit",
        "hashespersec",
        "pooledtx",
        "testnet"
})
public class GetMiningInfoResult extends JsonExtra {
    @JsonProperty
    private Long blocks;

    @JsonProperty("currentblocksize")
    private Integer currentBlockSize;

    @JsonProperty("currentblocktx")
    private Integer currentBlockTx;
    @JsonProperty
    private Double difficulty;
    @JsonProperty
    private String errors;
    @JsonProperty
    private Boolean generate;

    @JsonProperty("genproclimit")
    private Integer genProcLlimit;

    @JsonProperty("hashespersec")
    private Integer hashespersec;

    @JsonProperty("pooledtx")
    private Integer pooledTx;
    @JsonProperty
    private Boolean testnet;


    public Long getBlocks() {
        return blocks;
    }

    public Integer getCurrentBlockSize() {
        return currentBlockSize;
    }

    public Integer getCurrentBlockTx() {
        return currentBlockTx;
    }

    public Double getDifficulty() {
        return difficulty;
    }

    public String getErrors() {
        return errors;
    }

    public Boolean getGenerate() {
        return generate;
    }

    public Integer getGenProcLlimit() {
        return genProcLlimit;
    }

    public Integer getHashespersec() {
        return hashespersec;
    }

    public Integer getPooledTx() {
        return pooledTx;
    }

    public Boolean getTestnet() {
        return testnet;
    }
}
