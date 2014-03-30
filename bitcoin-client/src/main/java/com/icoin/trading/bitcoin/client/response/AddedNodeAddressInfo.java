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


@JsonPropertyOrder({
        "address",
        "connected"
})
public class AddedNodeAddressInfo extends JsonExtra {

    /**
     * The nodes network address.
     */
    @JsonProperty
    private String address;

    /**
     * Connected, "true" of "false".
     * <p/>
     * Seems like this ought to have been an boolean, but bitcoind does return a
     * string, so maybe it can also return other values than "true" and "false".
     */
    @JsonProperty
    private String connected;


    private AddedNodeAddressInfo() {
    }

    public AddedNodeAddressInfo(String address, String connected) {
        this.address = address;
        this.connected = connected;
    }

    public String getAddress() {
        return address;
    }

    public String getConnected() {
        return connected;
    }
}
