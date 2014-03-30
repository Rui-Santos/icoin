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

import java.util.Date;

/**
 * Data returned by BitcoindClient's getPeerInfo method.
 */
@JsonPropertyOrder({
        "addr",
        "services",
        "lastsend",
        "lastrecv",
        "conntime",
        "version",
        "subver",
        "inbound",
        "releasetime",
        "startingheight",
        "banscore"
})
public class PeerInfo extends JsonExtra {

    /**
     * Network address.
     */
    @JsonProperty("addr")
    private String address;

    @JsonProperty
    private String services;

    @JsonProperty("lastsend")
    private Date lastSend;

    @JsonProperty("lastrecv")
    private Date lastRecv;

    @JsonProperty("conntime")
    private Date connTime;

    @JsonProperty
    private Integer version;

    @JsonProperty("subver")
    private String subVersion;

    @JsonProperty
    private Boolean inbound;

    @JsonProperty("releasetime")
    private Date releaseTime;

    @JsonProperty("startingheight")
    private Long startingHeight;

    @JsonProperty("banscore")
    private Integer banScore;

    public String getAddress() {
        return address;
    }

    public String getServices() {
        return services;
    }

    public Date getLastSend() {
        return lastSend;
    }

    public Date getLastRecv() {
        return lastRecv;
    }

    public Date getConnTime() {
        return connTime;
    }

    public Integer getVersion() {
        return version;
    }

    public String getSubVersion() {
        return subVersion;
    }

    public Boolean getInbound() {
        return inbound;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public Long getStartingHeight() {
        return startingHeight;
    }

    public Integer getBanScore() {
        return banScore;
    }
}
