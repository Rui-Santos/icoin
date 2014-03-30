/*
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
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.icoin.trading.bitcoin.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * A bitcoin transaction.
 *
 * @author Claus Nielsen
 */
@JsonPropertyOrder({
        "data",
        "hash",
        "required",
        "depends",
        "fee",
        "sigops"
})
public class Transaction extends JsonExtra {

    /**
     * Transaction data encoded in hexadecimal (byte-for-byte).
     */
    private String data;

    /**
     * If provided and true, this transaction must be in the final block.
     */
    @JsonInclude(Include.NON_NULL)
    private Boolean required;

    /**
     * Hash/id encoded in little-endian hexadecimal.
     */
    private String hash;

    /**
     * Other transactions before this one (by 1-based index in "transactions"
     * list) that must be present in the final block if this one is; if key is
     * not present, dependencies are unknown and clients MUST NOT assume there
     * aren't any.
     */
    @JsonInclude(Include.NON_NULL)
    private Integer[] depends;

    /**
     * Fee in Satoshis.
     * <p/>
     * Difference in value between transaction inputs and outputs (in Satoshis);
     * for coinbase transactions, this is a negative Number of the total
     * collected block fees (ie, not including the block subsidy); if key is not
     * present, fee is unknown and clients MUST NOT assume there isn't one.
     */
    @JsonInclude(Include.NON_NULL)
    private Long fee;

    /**
     * Total number of SigOps, as counted for purposes of block limits; if key
     * is not present, sigop count is unknown and clients MUST NOT assume there
     * aren't any.
     */
    @JsonInclude(Include.NON_NULL)
    private Integer sigops;

}
