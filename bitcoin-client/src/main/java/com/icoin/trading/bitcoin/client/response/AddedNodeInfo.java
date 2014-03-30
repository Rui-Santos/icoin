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
 * Information about an added node.
 * <p/>
 * An array of these are returned by BitcoindClients getAddedNodeInfo method.
 * Connected information is only available if getAddedNodeInfo was called with
 * <code>dns=true</code>.
 */
@JsonPropertyOrder({
        "addednode",
        "connected",
        "addresses"
})
public class AddedNodeInfo extends JsonExtra {

    @JsonProperty("addednode")
    private String addedNode;

    @JsonProperty
    private Boolean connected;

    @JsonProperty
    private AddedNodeAddressInfo[] addresses;

}
