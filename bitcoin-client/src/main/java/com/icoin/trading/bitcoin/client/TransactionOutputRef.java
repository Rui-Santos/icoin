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
package com.icoin.trading.bitcoin.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Identifies an transaction output.
 * <p/>
 * TransactionOutput identifies the transaction outputs to spend when making
 * a new payment transaction. These will become the <i>input</i> to the new
 * transaction.<br>
 * Figuratively speeking, you can say that TransactionOutputs point
 * out exactly which coins you use when making a payment.
 */
@JsonPropertyOrder({"txid", "vout"})
public class TransactionOutputRef extends ValueObject {

    @JsonProperty("txid")
    private String txId;

    @JsonProperty("vout")
    private Integer vout;

    /**
     * Full constructor.
     *
     * @param txId - transaction id.
     * @param vout - output number.
     */
    public TransactionOutputRef(@JsonProperty("txid") String txId, @JsonProperty("vout") Integer vout) {
        this.txId = txId;
        this.vout = vout;
    }


}
