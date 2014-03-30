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
import com.icoin.trading.bitcoin.client.JsonExtra;

import java.math.BigDecimal;

/**
 * Amount received by an account.
 * <p/>
 * BitcoindClient.listReceivedByAccountResult returns an array with one of these
 * for each account.
 */

public class ListReceivedByAccountResult extends JsonExtra {

    /**
     * The account of the receiving addresses.
     */
    @JsonProperty
    private String account;

    /**
     * Total amount received by addresses with this account.
     */
    @JsonProperty
    private BigDecimal amount;

    /**
     * Number of confirmations of the most recent transaction included.
     */
    @JsonProperty
    private Integer confirmations;


    public String getAccount() {
        return account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getConfirmations() {
        return confirmations;
    }
}
