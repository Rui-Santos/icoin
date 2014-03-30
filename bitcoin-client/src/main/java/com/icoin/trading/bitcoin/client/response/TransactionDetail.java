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

import java.math.BigDecimal;

/**
 * @author Claus Nielsen
 */
@JsonPropertyOrder({
        "account",
        "address",
        "category",
        "amount",
        "fee"
})
public class TransactionDetail extends JsonExtra {
    @JsonProperty
    private String account;
    @JsonProperty
    private String address;
    @JsonProperty
    private String category;
    @JsonProperty
    private BigDecimal amount;

    @JsonInclude(Include.NON_NULL)
    private BigDecimal fee;


    public String getAccount() {
        return account;
    }

    public String getAddress() {
        return address;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getFee() {
        return fee;
    }
}
